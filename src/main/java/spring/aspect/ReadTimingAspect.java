package spring.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
@Order(2)
public class ReadTimingAspect {

	@Pointcut(value = "within(spring.service..*)")
	public void isServiceLayer() {
	}

	@Pointcut(value = "@within(org.springframework.transaction.annotation.Transactional) || "
			+ "@annotation(org.springframework.transaction.annotation.Transactional)")
	public void isTransactional() {
	}

	@Pointcut(value = "isServiceLayer() && isTransactional() && execution(public * *..*Service.get*ById(..))")
	public void anyGetByIdServiceMethod() {
	}

	@Pointcut(value = "isServiceLayer() && isTransactional() && execution(public * *..*Service.get*ByName(..))")
	public void anyGetByNameServiceMethod() {
	}

	@Pointcut(value = "isServiceLayer() " + "&& isTransactional() " + "&& execution(public * *..*Service.get*(..)) "
			+ "&& !anyGetByIdServiceMethod() " + "&& !anyGetByNameServiceMethod()")
	public void anyAnotherGetServiceMethod() {
	}

	@Around(value = "anyAnotherGetServiceMethod()")
	public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return pjp.proceed();
		} finally {
			long time = System.currentTimeMillis() - start;
			log.info("AROUND. Класс - {}, метод - {}, ответил за: {} ms", pjp.getTarget().getClass().getSimpleName(),
					pjp.getSignature().getName(), time);
		}
	}

}
