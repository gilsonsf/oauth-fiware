package com.gsf.executor.api.service;

import com.gsf.executor.api.enums.AttackTypes;
import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;
import com.gsf.executor.api.util.Utilities;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static com.gsf.executor.api.enums.AttackTypes.MIXUP;

@Aspect
@Configuration
public class OAuthHackerAspect {

    private Logger LOGGER = LoggerFactory.getLogger(OAuthHackerAspect.class);

    @Around("execution(* com.gsf.executor.api.service.ManagerService.createTask(..))")
    public void tryAttack(ProceedingJoinPoint joinPoint) throws Throwable {

        if (Utilities.isTimeToHack()) {
            LOGGER.info("tryAttack() is running! " + LocalDateTime.now());
            LOGGER.info("hijacked method : " + joinPoint.getSignature().getName());
            //LOGGER.info("hijacked arguments : " + Arrays.toString(joinPoint.getArgs()));

            int randomKindOfAttack = ThreadLocalRandom.current().nextInt(1, 3);

            UserTemplate user = UserTemplateMemoryRepository.copyValues((UserTemplate) joinPoint.getArgs()[0]);
            AttackTypes kindOfAttack = Utilities.getAttackTypesById(randomKindOfAttack);

            if (kindOfAttack == MIXUP) {
                user.setAs(user.getAs() + "_mixup");
                joinPoint.getArgs()[0] = user;
            }


            joinPoint.getArgs()[1] = kindOfAttack;

            LOGGER.info("kindOfAttack chosen " + kindOfAttack.toString());

            LOGGER.info("Around before is running!");

            joinPoint.proceed(joinPoint.getArgs()); //continue on the intercepted method

            LOGGER.info("Around after is running!");

        } else {
            LOGGER.info("tryAttack() (is NOT TimeToHack)! " + LocalDateTime.now());
            joinPoint.proceed();
        }
    }


}
