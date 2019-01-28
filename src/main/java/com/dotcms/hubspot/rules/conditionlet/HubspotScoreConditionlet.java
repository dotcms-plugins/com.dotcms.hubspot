package com.dotcms.hubspot.rules.conditionlet;

import static com.dotmarketing.portlets.rules.parameter.comparison.Comparison.NUMERIC_COMPARISONS;

import java.security.InvalidParameterException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotcms.hubspot.api.HubspotAPI;
import com.dotmarketing.portlets.rules.RuleComponentInstance;
import com.dotmarketing.portlets.rules.conditionlet.ComparisonParameterDefinition;
import com.dotmarketing.portlets.rules.conditionlet.Conditionlet;
import com.dotmarketing.portlets.rules.exception.ComparisonNotPresentException;
import com.dotmarketing.portlets.rules.exception.ComparisonNotSupportedException;
import com.dotmarketing.portlets.rules.model.ParameterModel;
import com.dotmarketing.portlets.rules.parameter.ParameterDefinition;
import com.dotmarketing.portlets.rules.parameter.comparison.Comparison;
import com.dotmarketing.portlets.rules.parameter.display.NumericInput;
import com.dotmarketing.portlets.rules.parameter.type.NumericType;
import com.dotmarketing.util.Logger;


public class HubspotScoreConditionlet extends Conditionlet<HubspotScoreConditionlet.Instance> {

    private static final long serialVersionUID = 1L;

    public static final String HUBSPOT_SCORE_KEY = "com.dotcms.hubspotscore.key";

    private static final ParameterDefinition<NumericType> HUBSPOT_SCORE_PARAMETER =
            new ParameterDefinition<>(3, HUBSPOT_SCORE_KEY, new NumericInput<>(new NumericType().required()));

    public HubspotScoreConditionlet() {
        super(HUBSPOT_SCORE_KEY, new ComparisonParameterDefinition(2, NUMERIC_COMPARISONS), HUBSPOT_SCORE_PARAMETER);
    }

    /**
     * This method evaluates if a user's hubspot score is greater or less than a value
     * 
     * @param request Http servlet request
     * @param response Http servlet response
     * 
     * @return true if the conditions are met, false if not
     */
    @Override
    public boolean evaluate(HttpServletRequest request, HttpServletResponse response, Instance instance) {
        int score = 0;

        try {
            score = new HubspotAPI().getContact(request).getJSONObject("properties").getJSONObject("hubspotscore").optInt("value");
        } catch (Exception e) {
            Logger.warn(this, "Could not parse hubspot score");
        }
        System.out.println("hubby score" + score);

        return instance.comparison.perform(score, instance.hubspotScore);

    }

    @Override
    public Instance instanceFrom(Map<String, ParameterModel> parameters) {
        return new Instance(this, parameters);
    }

    public static class Instance implements RuleComponentInstance {

        private final int hubspotScore;
        private final Comparison<Number> comparison;

        private Instance(HubspotScoreConditionlet definition, Map<String, ParameterModel> parameters) {
            try {
                this.hubspotScore = Integer.parseInt(parameters.get(HUBSPOT_SCORE_KEY).getValue());
            } catch (NumberFormatException e) {
                throw new InvalidParameterException(HUBSPOT_SCORE_KEY + " must be an integer value");
            }
            String comparisonValue = parameters.get(COMPARISON_KEY).getValue();
            try {
                // noinspection unchecked
                this.comparison = ((ComparisonParameterDefinition) definition.getParameterDefinitions().get(COMPARISON_KEY))
                        .comparisonFrom(comparisonValue);
            } catch (ComparisonNotPresentException e) {
                throw new ComparisonNotSupportedException("The comparison '%s' is not supported on Condition type '%s'", comparisonValue,
                        definition.getId());
            }
        }
    }

}
