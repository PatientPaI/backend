package com.patientpal.backend.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;

public class CustomMariaDbFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        BasicType<Double> resultType = functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.DOUBLE);
        functionContributions.getFunctionRegistry()
                .registerPattern("match", "match(?1, ?2) against (?3 WITH QUERY EXPANSION)", resultType);
    }
}
