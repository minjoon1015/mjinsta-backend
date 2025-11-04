// package back_end.springboot.component;


// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;

// @Aspect
// @Component
// public class PerformanceAspect {

//     private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

//     @Around("execution(* back_end.springboot.controller..*(..))")
//     public Object logControllerExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
//         long start = System.currentTimeMillis();

//         Object result = joinPoint.proceed(); // 실제 메서드 호출

//         long duration = System.currentTimeMillis() - start;
//         logger.info("Controller {} 실행 시간: {}ms", joinPoint.getSignature(), duration);

//         return result;
//     }

//     @Around("execution(* back_end.springboot.service..*(..))")
//     public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
//         long start = System.currentTimeMillis();

//         Object result = joinPoint.proceed();

//         long duration = System.currentTimeMillis() - start;
//         logger.info("Service {} 실행 시간: {}ms", joinPoint.getSignature(), duration);

//         return result;
//     }
// }