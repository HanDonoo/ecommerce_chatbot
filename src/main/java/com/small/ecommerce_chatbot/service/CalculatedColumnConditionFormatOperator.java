package com.small.ecommerce_chatbot.service;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Objects;

public class CalculatedColumnConditionFormatOperator {
    private final String operator;

    private CalculatedColumnConditionFormatOperator(final Builder builder) {
        this.operator = builder.withOperator;
    }

    public String getOperator() {
        return this.operator;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getOperator()).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (Objects.isNull(obj) || !(obj instanceof CalculatedColumnConditionFormatOperator)) {
            return false;
        }
        final CalculatedColumnConditionFormatOperator other = (CalculatedColumnConditionFormatOperator) obj;
        return new EqualsBuilder()
                .append(getOperator(), other.getOperator())
                .isEquals();
    }

    // Builder Pattern
    public static class Builder {
        private String withOperator;

        public Builder() {
        }

        public Builder withOperator(final String operator) {
            this.withOperator = operator;
            return this;
        }

        public CalculatedColumnConditionFormatOperator build() {
            return new CalculatedColumnConditionFormatOperator(this);
        }
    }
}
