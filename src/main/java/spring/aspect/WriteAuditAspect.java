package spring.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Aspect
@Order(1)
public class WriteAuditAspect {

	@Pointcut(value = "within(spring.service..*)")
	public void isServiceLayer() {
	}

	@Pointcut(value = "@within(org.springframework.transaction.annotation.Transactional) "
			+ "|| @annotation(org.springframework.transaction.annotation.Transactional)")
	public void isTransactional() {
	}

	@Pointcut(value = "execution(public * *..*Service.add*(..))" + "|| execution(public * *..*Service.make*(..)) "
			+ "|| execution(public * *..*Service.update*(..))" + "|| execution(public * *..*Service.delete*(..)) "
			+ "|| execution(public * *..*Service.remove*(..))")
	public void anyWriteMethod() {
	}

	@Pointcut(value = "isServiceLayer() && isTransactional() && anyWriteMethod()")
	public void anyWriteServiceMethod() {
	}

	@Around(value = "anyWriteServiceMethod()")
	public Object auditWriteOperation(ProceedingJoinPoint pjp) throws Throwable {
		String service = pjp.getTarget().getClass().getSimpleName();
		String method = pjp.getSignature().getName();

		Object[] args = pjp.getArgs();
		Object id = extractId(args);
		log.info("AROUND Before. Зашли в класс - {}, метод - {}, передали id - {}", service, method, id);
		try {
			Object result = pjp.proceed();
			log.info("AROUND AfterReturning. В классе - {}, методе - {}, получили result - {}", service, method,
					result);
			return result;
		} catch (Throwable exception) {
			log.warn("AROUND AfterThrowing. В классе - {}, методе - {}, получили exception - {}: {}", service, method,
					exception.getClass(), exception.getMessage());
			throw exception;
		} finally {
			log.info("AROUND After(finally). Вышли из класса - {}, метода - {}", service, method);
		}
	}

	private Object extractId(Object[] args) {
		if (args == null) {
			return null;
		}
		for (Object arg : args) {
			if (arg instanceof java.util.UUID) {
				return arg;
			}
		}
		return null;
	}

}
