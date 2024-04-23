package com.ctrip.car.osd.framework.soa.server.validator;

import com.ctrip.car.osd.framework.soa.server.exception.ValidationException;
import com.ctriposs.baiji.rpc.server.validation.results.ValidationFailure;
import com.ctriposs.baiji.rpc.server.validation.results.ValidationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by lmxin on 2017/8/18.
 */
public class ValidatorRunner {

    private void throwException(String paraName, String msg) throws ValidationException {
        List<ValidationFailure> failures = new ArrayList<>();
        ValidationFailure vf = new ValidationFailure(paraName, paraName + msg);
        failures.add(vf);
        ValidationResult vr = new ValidationResult(failures);
        throw new ValidationException(vr);
    }

    public ValidatorRunner notNull(Object value, String paraName) throws ValidationException {
        if (value == null) {
            throwException(paraName, "不能为null");
        }
        return this;
    }

    public ValidatorRunner notNull(Object value, String paraName, boolean when) throws ValidationException {
        if (when) {
            return notNull(value, paraName);
        }
        return this;
    }

    public ValidatorRunner notEmpty(Collection value, String paraName) throws ValidationException {
        if (value == null || value.isEmpty()) {
            throwException(paraName, "不能为空");
        }
        return this;
    }

    public ValidatorRunner notEmpty(Collection value, String paraName, boolean when) throws ValidationException {
        if (when) {
            return notEmpty(value, paraName);
        }
        return this;
    }

    public ValidatorRunner notEmpty(String value, String paraName) throws ValidationException {
        if (value == null || value.isEmpty()) {
            throwException(paraName, "不能为空");
        }
        return this;
    }

    public ValidatorRunner notEmpty(String value, String paraName, boolean when) throws ValidationException {
        if (when) {
            return notEmpty(value, paraName);
        }
        return this;
    }

    public <T> ValidatorRunner greaterThan(Comparable<T> value, Comparable<T> base, String paraName) throws ValidationException {
        if (base == null) {
            return this;
        }
        if (value == null || value.compareTo((T) base) <= 0) {
            throwException(paraName, "必须大于" + base);
        }
        return this;
    }

    public <T> ValidatorRunner greaterThan(Comparable<T> value, Comparable<T> base, String paraName, boolean when) throws ValidationException {
        if (when) {
            return greaterThan(value, base, paraName);
        }
        return this;
    }

    public <T> ValidatorRunner greaterThanOrEqual(Comparable<T> value, Comparable<T> base, String paraName) throws ValidationException {
        if (base == null) {
            return this;
        }
        if (value == null || value.compareTo((T) base) < 0) {
            throwException(paraName, "必须大于等于" + base);
        }
        return this;
    }

    public <T> ValidatorRunner greaterThanOrEqual(Comparable<T> value, Comparable<T> base, String paraName, boolean when) throws ValidationException {
        if (when) {
            return greaterThanOrEqual(value, base, paraName);
        }
        return this;
    }

    public <T> ValidatorRunner lessThan(Comparable<T> value, Comparable<T> base, String paraName) throws ValidationException {
        if (base == null) {
            return this;
        }
        if (value == null || value.compareTo((T) base) >= 0) {
            throwException(paraName, "必须小于" + base);
        }
        return this;
    }

    public <T> ValidatorRunner lessThan(Comparable<T> value, Comparable<T> base, String paraName, boolean when) throws ValidationException {
        if (when) {
            return lessThan(value, base, paraName);
        }
        return this;
    }

    public <T> ValidatorRunner lessThanOrEqual(Comparable<T> value, Comparable<T> base, String paraName) throws ValidationException {
        if (base == null) {
            return this;
        }
        if (value == null || value.compareTo((T) base) > 0) {
            throwException(paraName, "必须小于等于" + base);
        }
        return this;
    }

    public <T> ValidatorRunner lessThanOrEqual(Comparable<T> value, Comparable<T> base, String paraName, boolean when) throws ValidationException {
        if (when) {
            return lessThanOrEqual(value, base, paraName);
        }
        return this;
    }

    public <T> ValidatorRunner must(T value, Function<T, Boolean> function, String paraName) throws ValidationException {
        Boolean result = function.apply(value);
        if (result == null || !result) {
            throwException(paraName, "不满足条件");
        }
        return this;
    }

    public <T> ValidatorRunner must(T value, Function<T, Boolean> function, String paraName, boolean when) throws ValidationException {
        if (when) {
            return must(value, function, paraName);
        }
        return this;
    }

    public <T> ValidatorRunner equals(Comparable<T> value, Comparable<T> base, String paraName) throws ValidationException {
        if (value == null && base == null) {
            return this;
        } else if (value == null || base == null || value.compareTo((T) base) != 0) {
            throwException(paraName, "不等于" + (base == null ? "null" : base.toString()));
        }
        return this;
    }

    public <T> ValidatorRunner equals(Comparable<T> value, Comparable<T> base, String paraName, boolean when) throws ValidationException {
        if (when) {
            return equals(value, base, paraName);
        }
        return this;
    }

    public ValidatorRunner lengthGreaterThan(String value, int base, String paraName) throws ValidationException {
        if (value == null || value.length() <= base) {
            throwException(paraName, "长度必须大于" + base);
        }
        return this;
    }

    public ValidatorRunner lengthGreaterThan(String value, int base, String paraName, boolean when) throws ValidationException {
        if (when) {
            lengthGreaterThan(value, base, paraName);
        }
        return this;
    }

    public ValidatorRunner lengthGreaterThanOrEqual(String value, int base, String paraName) throws ValidationException {
        if (value == null || value.length() < base) {
            throwException(paraName, "长度不能小于" + base);
        }
        return this;
    }

    public ValidatorRunner lengthGreaterThanOrEqual(String value, int base, String paraName, boolean when) throws ValidationException {
        if (when) {
            lengthGreaterThanOrEqual(value, base, paraName);
        }
        return this;
    }

    public ValidatorRunner lengthLessThan(String value, int base, String paraName) throws ValidationException {
        if (value == null || value.length() >= base) {
            throwException(paraName, "长度必须小于" + base);
        }
        return this;
    }

    public ValidatorRunner lengthLessThan(String value, int base, String paraName, boolean when) throws ValidationException {
        if (when) {
            lengthLessThan(value, base, paraName);
        }
        return this;
    }

    public ValidatorRunner lengthLessThanOrEqual(String value, int base, String paraName) throws ValidationException {
        if (value == null || value.length() > base) {
            throwException(paraName, "长度不能超过" + base);
        }
        return this;
    }

    public ValidatorRunner lengthLessThanOrEqual(String value, int base, String paraName, boolean when) throws ValidationException {
        if (when) {
            lengthLessThanOrEqual(value, base, paraName);
        }
        return this;
    }

    public ValidatorRunner matches(String value, String regex, String paraName) throws ValidationException {
        if (regex == null) {
            return this;
        }
        if (value == null || !value.matches(regex)) {
            throwException(paraName, "不匹配");
        }
        return this;
    }

    public ValidatorRunner matches(String value, String regex, String paraName, boolean when) throws ValidationException {
        if (when) {
            return matches(value, regex, paraName);
        }
        return this;
    }
}
