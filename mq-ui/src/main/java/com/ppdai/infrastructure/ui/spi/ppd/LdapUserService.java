package com.ppdai.infrastructure.ui.spi.ppd;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.Organization;
import com.ppdai.infrastructure.mq.biz.dto.UserInfo;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.ui.spi.UserProviderService;

//@Service
//@ConditionalOnMissingBean
//提供数据源访问
public class LdapUserService implements UserProviderService {
	private final Logger LOG = LoggerFactory.getLogger(LdapUserService.class);
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private RoleService roleService;
	private AtomicReference<String> adServer = new AtomicReference<>();
	private AtomicReference<String[]> searchBase = new AtomicReference<String[]>(new String[0]);
	private final AtomicReference<Map<String, UserInfo>> mapUserRef = new AtomicReference<>(
			new HashMap<String, UserInfo>());
	private final AtomicReference<Map<String, Organization>> orgsRef = new AtomicReference<Map<String, Organization>>(
			new HashMap<String, Organization>());
	private final Executor executor = Executors.newFixedThreadPool(2);
	private volatile boolean isRunning = true;
	private String preconfig = "";

	@PreDestroy
	private void close() {
		isRunning = false;
	}

	@PostConstruct
	private void init() {
		doInitParm();
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (isRunning) {
					try {
						initUser();
					} catch (Throwable t) {

					}
					Util.sleep(1000);
				}
			}
		});

		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (isRunning) {
					try {
						doInitUser();
					} catch (Throwable t) {

					}
					Util.sleep(1000 * 60 * 5);
				}
			}
		});
	}

	private void doInitParm() {
		String adServerTemp = soaConfig.getMqLdapUrl();
		if (StringUtils.isEmpty(adServerTemp)) {
			return;
		}
		adServer.set(adServerTemp);
		String searchBaseTemp = soaConfig.getMqLdapPath();
		if (StringUtils.isEmpty(searchBaseTemp)) {
			throw new RuntimeException("parameter SearchBase cannot be null");
		}
		String[] searchBaseTemp1 = searchBaseTemp.split("\\|");
		searchBase.set(searchBaseTemp1);

	}

	private boolean initUser() {
		try {
			String config = adServer.get() + soaConfig.getMqLdapPath() + soaConfig.getMqAdminUser()
					+ soaConfig.getMqAdminPass();
			if (!preconfig.equals(config)) {
				doInitUser();
				preconfig = config;
			}

			return true;
		} catch (Exception e) {
			LOG.error("ldap初始化失败", e);
			return false;
		}
	}

	private volatile long lastTime = System.currentTimeMillis()-1001;

	private void doInitUser() {
		if (System.currentTimeMillis() - lastTime < 1000) {
			return;
		}
		lastTime = System.currentTimeMillis();
		doInitParm();
		Map<String, UserInfo> mapUser = new HashMap<>();
		Map<String, Organization> orgMap = new HashMap<>();
		String[] searchBase1 = searchBase.get();
		for (String serverPath : searchBase1) {
			try {
				doInitUser(mapUser, orgMap, serverPath);
			} catch (Exception e) {

			}
			Util.sleep(10);
		}
		addMq(mapUser);
		mapUserRef.set(mapUser);
		orgsRef.set(orgMap);

	}

	private void addMq(Map<String, UserInfo> mapUser) {
		if (!mapUser.containsKey(soaConfig.getMqAdminUser())) {
			UserInfo userInfo = new UserInfo();
			userInfo.setAdmin(true);
			userInfo.setDepartment("基础框架");
			userInfo.setEmail("mq@mq.com");
			userInfo.setName(soaConfig.getMqAdminUser());
			userInfo.setUserId(soaConfig.getMqAdminPass());
			mapUser.put(soaConfig.getMqAdminUser(), userInfo);
		}

	}

	private void doInitUser(Map<String, UserInfo> userInfos, Map<String, Organization> orgMap, String serverPath)
			throws NamingException {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "corp\\" + soaConfig.getMqLdapUser());
		env.put(Context.SECURITY_CREDENTIALS, soaConfig.getMqLdapPass());
		env.put(Context.PROVIDER_URL, adServer.get());

		LdapContext ctx = new InitialLdapContext(env, null);
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		String searchFilter = String
				.format("(&(objectClass=top)(objectClass=user)(objectClass=person)(objectClass=organizationalPerson))");

		String returnedAtts[] = { "memberOf", "sAMAccountName", "cn", "distinguishedName", "mail" };
		searchCtls.setReturningAttributes(returnedAtts);
		NamingEnumeration<SearchResult> answer = ctx.search(serverPath, searchFilter, searchCtls);
		while (answer.hasMoreElements()) {
			SearchResult sr = (SearchResult) answer.next();
			Attributes at = sr.getAttributes();
			UserInfo userInfo = new UserInfo();
			userInfo.setDepartment(getDValue(at.get("distinguishedName")));
			userInfo.setEmail(getValue(at.get("mail")));
			userInfo.setUserId(getValue(at.get("sAMAccountName")));
			userInfo.setName(getValue(at.get("cn")));
			userInfo.setAdmin(roleService.isAdmin(userInfo.getUserId()));
			userInfos.put(userInfo.getUserId(), userInfo);
			if (!StringUtils.isEmpty(userInfo.getDepartment())) {
				Organization organization = new Organization();
				organization.setOrgId(userInfo.getDepartment());
				orgMap.put(userInfo.getDepartment(), organization);
			}
		}
		ctx.close();
	}

	@Override
	public boolean login(String username, String password) {
		doInitParm();
		if (username.equals(soaConfig.getMqAdminUser()) && password.equals(soaConfig.getMqAdminPass())) {
			return true;
		}
		return doLogin(username, password);
	}

	private boolean doLogin(String username, String password) {
		Transaction transaction = Tracer.newTransaction("Ldap", "doLogin");
		LdapContext ctx = null;
		try {
			Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, "corp\\" + username);
			env.put(Context.SECURITY_CREDENTIALS, password);
			env.put(Context.PROVIDER_URL, adServer.get());

			ctx = new InitialLdapContext(env, null);
			transaction.setStatus(Transaction.SUCCESS);
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
			transaction.setStatus(e);
		} finally {
			transaction.complete();
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {

				}
			}
		}
		return false;
	}

	private String getDValue(Attribute attribute) {
		String value = getValue(attribute);
		if (value.indexOf(",") > -1) {
			value = value.split(",")[1];
			value = value.replaceAll("OU=", "").trim();
		}
		return value;
	}

	@Override
	public Map<String, UserInfo> getUsers() {
		return mapUserRef.get();
	}

	@Override
	public Map<String, Organization> getOrgs() {
		return orgsRef.get();
	}

	private String getValue(Attribute attribute) {
		if (attribute == null) {
			return "";
		}
		String value = attribute.toString();
		if (StringUtils.isEmpty(value)) {
			return "";
		}
		if (value.indexOf(":") != -1) {
			value = value.replaceAll(value.split(":")[0], "").trim();
			value = value.substring(1, value.length()).trim();
		}
		return value;
	}
}
