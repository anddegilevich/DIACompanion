package com.almazov.diacompanion.model.config;


import com.almazov.diacompanion.model.learner.ObjFunction;

public class PredictorConfiguration {
    public static final PredictorConfiguration DEFAULT = new PredictorConfiguration();
    private ObjFunction objFunction;

    public ObjFunction getObjFunction() {
        return objFunction;
    }

    public static class Builder {
        private PredictorConfiguration predictorConfiguration;

        Builder() {
            predictorConfiguration = new PredictorConfiguration();
        }

        public Builder objFunction(ObjFunction objFunction) {
            predictorConfiguration.objFunction = objFunction;
            return this;
        }

        public PredictorConfiguration build() {
            PredictorConfiguration result = predictorConfiguration;
            predictorConfiguration = null;
            return result;
        }
    }

}
