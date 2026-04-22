package spring.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
@Order(3)
public class ReadLoggingAspect {

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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Before(value = "anyGetByIdServiceMethod() && args(name)")
	public void addLoggingForGetIdBeforeMethods(JoinPoint joinPoint, Object name) {
		log.info("Before. Зашли в класс - {}, метод - {}, передали id - {}",
				joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), name);
	}

	@AfterReturning(pointcut = "anyGetByIdServiceMethod()", returning = "result")
	public void addLoggingForGetIdAfterReturningMethods(JoinPoint joinPoint, Object result) {
		log.info("AfterReturning. В классе - {}, методе - {}, получили result - {}",
				joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), result);
	}

	@AfterThrowing(pointcut = "anyGetByIdServiceMethod()", throwing = "exception")
	public void addLoggingForGetIdAfterThrowingMethods(JoinPoint joinPoint, Throwable exception) {
		log.info("AfterThrowing. В классе - {}, методе - {}, получили exception - {}: {}",
				joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(),
				exception.getClass(), exception.getMessage());
	}

	@After(value = "anyGetByIdServiceMethod()")
	public void addLoggingForGetIdAfterFinallyMethods(JoinPoint joinPoint) {
		log.info("After(finally). Вышли из класса - {}, метода - {}", joinPoint.getTarget().getClass().getSimpleName(),
				joinPoint.getSignature().getName());
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Before(value = "anyGetByNameServiceMethod() && args(id)")
	public void addLoggingForGetNameBeforeMethods(JoinPoint joinPoint, Object id) {
		log.info("Before. Зашли в класс - {}, метод - {}, передали id - {}",
				joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), id);
	}

	@AfterReturning(pointcut = "anyGetByNameServiceMethod()", returning = "result")
	public void addLoggingForGetNameAfterReturningMethods(JoinPoint joinPoint, Object result) {
		log.info("AfterReturning. В классе - {}, методе - {}, получили result - {}",
				joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), result);
	}

	@AfterThrowing(pointcut = "anyGetByNameServiceMethod()", throwing = "exception")
	public void addLoggingForGetNameAfterThrowingMethods(JoinPoint joinPoint, Throwable exception) {
		log.info("AfterThrowing. В классе - {}, методе - {}, получили exception - {}: {}",
				joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(),
				exception.getClass(), exception.getMessage());
	}

	@After(value = "anyGetByNameServiceMethod()")
	public void addLoggingForGetNameAfterFinallyMethods(JoinPoint joinPoint) {
		log.info("After(finally). Вышли из класса - {}, метода - {}", joinPoint.getTarget().getClass().getSimpleName(),
				joinPoint.getSignature().getName());
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Before(value = "anyAnotherGetServiceMethod()")
	public void addLoggingForOtherGetMethods(JoinPoint joinPoint) {
		log.info("Before. Зашли в класс - {}, метод - {}", joinPoint.getTarget().getClass().getSimpleName(),
				joinPoint.getSignature().getName());
	}

	@AfterReturning(pointcut = "anyAnotherGetServiceMethod()", returning = "result")
	public void addLoggingForOtherAfterReturningMethods(JoinPoint joinPoint, Object result) {
		log.info("AfterReturning. В классе - {}, методе - {}, получили result - {}",
				joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), result);
	}

	@AfterThrowing(pointcut = "anyAnotherGetServiceMethod()", throwing = "exception")
	public void addLoggingForOtherAfterThrowingMethods(JoinPoint joinPoint, Throwable exception) {
		log.warn("AfterThrowing. В классе - {}, методе - {}, получили exception - {}: {}",
				joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(),
				exception.getClass(), exception.getMessage());
	}

	@After(value = "anyAnotherGetServiceMethod()")
	public void addLoggingForOtherAfterFinallyMethods(JoinPoint joinPoint) {
		log.info("After(finally). Вышли из класса - {}, метода - {}", joinPoint.getTarget().getClass().getSimpleName(),
				joinPoint.getSignature().getName());
	}
}
