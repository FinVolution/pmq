package com.ppdai.infrastructure.mq.biz.common.util;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import org.apache.commons.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;

@Component
public class EmailUtil {
	private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);
	@Autowired
	private SoaConfig soaConfig;
	private final String[] arrInfo = {"info", "warn", "error"};
	private volatile String adminEmail = "";
	private volatile List<String> adminEmailLst = new ArrayList<>();
	private final Object lokObj = new Object();
	private LinkedBlockingDeque<EmailVo> emailVos = new LinkedBlockingDeque<>(4000);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 3L, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(50), SoaThreadFactory.create("EmailUtil", true),
			new ThreadPoolExecutor.DiscardPolicy());

	@PostConstruct
	public void init() {
		System.setProperty("mail.mime.charset", "UTF-8");
		executor.submit(new Runnable() {
			@Override
			public void run() {
				EmailVo emailVo = null;
				while (true) {
					try {
						emailVo = emailVos.take();
					} catch (Throwable e) {

					}
					if (emailVo != null) {
						doSendMail(emailVo);
					}
				}

			}
		});
	}

	private List<String> getAdminEmail() {
		if (!adminEmail.equals(soaConfig.getAdminEmail())) {
			synchronized (lokObj) {
				if (!adminEmail.equals(soaConfig.getAdminEmail())) {
					adminEmail = soaConfig.getAdminEmail();
					adminEmailLst = Arrays.asList(adminEmail.split(","));
				}
			}
		}
		return adminEmailLst;
	}

	public boolean sendInfoMail(String title, String content, List<String> rev) {
		try {
			return sendMail(title, content, rev, 0);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendInfoMail(String title, String content, String rev) {
		try {
			rev = rev + "";
			return sendMail(title, content, Arrays.asList(rev.split(",")), 0);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendInfoMail(String title, String content) {
		try {
			return sendMail(title, content, new ArrayList<>(), 0);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendWarnMail(String title, String content, List<String> rev) {
		try {
			return sendMail(title, content, rev, 1);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendWarnMail(String title, String content, String rev) {
		try {
			rev = rev + "";
			return sendMail(title, content, Arrays.asList(rev.split(",")), 1);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendWarnMail(String title, String content) {
		try {
			return sendMail(title, content, new ArrayList<>(), 1);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendErrorMail(String title, String content, List<String> rev) {
		try {
			return sendMail(title, content, rev, 2);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendErrorMail(String title, String content, String rev) {
		try {
			rev = rev + "";
			return sendMail(title, content, Arrays.asList(rev.split(",")), 2);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendErrorMail(String title, String content) {
		try {
			return sendMail(title, content, new ArrayList<>(), 2);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean sendMail(String title, String content, List<String> rev, int type) {
		if (!soaConfig.isEmailEnable()) {
			return true;
		}
		if (!CollectionUtils.isEmpty(rev) || !CollectionUtils.isEmpty(getAdminEmail())) {
			try {
				emailVos.add(new EmailVo(title, content + ",and send time is " + Util.formateDate(new Date()), rev, type));
				return true;
			} catch (Exception e) {
				return false;
			}
			// doSendMail(title, content, rev, type);
		}
		return true;
	}

	private void doSendMail(EmailVo emailVo) {
		if (!soaConfig.isEmailEnable()) {
			return;
		}
		try {
			Email email = new SimpleEmail();
			email.setHostName(soaConfig.getEmailHost());
			email.setSmtpPort(soaConfig.getEmailPort());
			if (soaConfig.enableMailAuth()) {
				email.setAuthenticator(
						new DefaultAuthenticator(soaConfig.getEmailAuName(), soaConfig.getEmailAuPass()));
			}
			email.setSSLOnConnect(false);
			email.setFrom(soaConfig.getEmailAuName(), "Mq3管理员");
			email.setSubject("[Mq3-" + arrInfo[emailVo.getType()] + "-" + soaConfig.getEnvName() + "环境]:"
					+ emailVo.getTitle() + " ,send by " + IPUtil.getLocalIP());
			email.setMsg(emailVo.getContent());
			if (!CollectionUtils.isEmpty(emailVo.getRev()) && !soaConfig.isOnlyEmailAdmin()) {
				emailVo.getRev().forEach(t1 -> {
					try {
						if (!StringUtils.isEmpty(t1)) {
							email.addTo(t1);
						}
					} catch (EmailException e) {

					}
				});
			}
			List<String> adminMails = getAdminEmail();
			adminMails.forEach(t1 -> {
				try {
					if (!StringUtils.isEmpty(t1)) {
						email.addTo(t1);
					}
				} catch (EmailException e) {

				}
			});
			email.send();
		} catch (Throwable e) {
			log.error("sendMail_error", e);
		}
	}
	public boolean sendImmediately(EmailVo emailVo, int maxRetry) {
		String[] receivers = filterInvalidEmail(emailVo.getRev());
		if(receivers.length <= 0) {
			return false;
		}

		HtmlEmail email = new HtmlEmail();
		email.setHostName(soaConfig.getEmailHost());
		email.setSmtpPort(soaConfig.getEmailPort());
		email.setAuthenticator(new DefaultAuthenticator(soaConfig.getEmailAuName(), soaConfig.getEmailAuPass()));
		email.setSSLOnConnect(false);
		email.setCharset("UTF-8");
		email.setSubject(emailVo.getTitle());
		int retries = 1;
		do {
			try {
				email.setFrom(soaConfig.getEmailAuName(), emailVo.getSenderName());
				email.setHtmlMsg(emailVo.getContent());
				email.setTextMsg("你的邮件客户端不支持html邮件");
				email.addTo(receivers);
				email.send();
				return true;
			} catch (EmailException e) {
				if( retries >= maxRetry) {
					Tracer.logError(e);
				}
				retries++;
			}
		} while(retries <= maxRetry);
		return false;
	}

	private String[] filterInvalidEmail(Collection<String> addresses) {
		return addresses.stream().toArray(String[]::new);
	}

	public static class EmailVo {
		String title;
		String content;
		List<String> rev;
		int type;
		String senderName;


		public EmailVo() {

		}

		public EmailVo(String title, String content, List<String> rev, int type) {
			this.title = title;
			this.content = content;
			this.rev = rev;
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public List<String> getRev() {
			return rev;
		}

		public void setRev(List<String> rev) {
			this.rev = rev;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getSenderName() {
			return senderName;
		}

		public EmailVo setSenderName(String senderName) {
			this.senderName = senderName;
			return this;
		}
	}

}
