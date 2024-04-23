package com.ctrip.car.osd.framework.soa.server.validator;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.soa.server.ExceptionInterceptor;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory.ExecutorDescriptor;
import com.ctrip.car.osd.framework.soa.server.Interceptor;
import com.ctrip.car.osd.framework.soa.server.exception.ValidationException;
import com.ctrip.car.osd.framework.soa.server.util.BaseInfoHelper;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import com.ctriposs.baiji.rpc.server.validation.ValidationContext;
import com.ctriposs.baiji.rpc.server.validation.internal.DefaultValidatorSelector;
import com.ctriposs.baiji.rpc.server.validation.internal.PropertyChain;
import com.ctriposs.baiji.rpc.server.validation.results.ValidationFailure;
import com.ctriposs.baiji.rpc.server.validation.results.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
@Order(-10)
public class ValidationInterceptor implements Interceptor, ExceptionInterceptor {

    @Autowired
    private ExecutorFactory executorFactory;
    private Map<Class, AbstractValidator> validators;
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public ValidationInterceptor() {
        this.validators = new ConcurrentHashMap<>();
    }

    @Override
    public void handle(Object req) throws Exception {
        if (executorFactory.hasValidator(req)) {
            AbstractValidator validator = getValidator(req);
            ValidationResult result = validator
                    .validate(new ValidationContext(req, new PropertyChain(), new DefaultValidatorSelector()));

            if (!result.isValid()) {
                throw new ValidationException(result);
            }
        }
    }

    @Override
    public void exceptionHandle(Object request, Object response, Exception exception) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (exception instanceof ValidationException) {
            logger.info("The validation error.", exception);
            ValidationException validationException = (ValidationException) exception;
            for (ValidationFailure failure : validationException.getResult().errors()) {
                sb.append(failure.toString());
            }
        }
        if (sb.length() > 0) {
            String requestId = BaseInfoHelper.getRequestId(request);
            BaseInfoHelper.setBaseResponse(response, System.currentTimeMillis(),false,sb.toString(),requestId);
        }
    }

    @Override
    public boolean canHandle(Exception exception) {
        return Objects.nonNull(exception) && exception instanceof ValidationException;
    }

    private AbstractValidator getValidator(Object req) {
        if (!validators.containsKey(req.getClass())) {
            this.createValidator(req);
        }
        return validators.get(req.getClass());
    }

    private void createValidator(Object req) {
        ExecutorDescriptor descriptor = executorFactory.getExecuterDescriptor(req);
        AbstractValidator validator = new AbstractValidator<>(descriptor.getRequestServiceType());
        Validator validatorRule = (Validator) descriptor.getExecutor();
        validatorRule.validate(validator);
        this.validators.put(descriptor.getRequestServiceType(), validator);
    }


}
