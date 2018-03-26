/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import edu.ucla.cs.scai.canali.core.autocompleter.AutocompleteObject;
import edu.ucla.cs.scai.canali.core.autocompleter.AutocompleteService;
import edu.ucla.cs.scai.canali.core.query.QueryService;
import edu.ucla.cs.scai.canali.core.query.ResultObject;
import edu.ucla.cs.scai.canali.core.query.ResultWrapper;
import edu.ucla.cs.scai.canali.core.translation.TranslationService;
import edu.ucla.cs.scai.canali.core.translation.TranslationWrapper;

import java.util.ArrayList;

/**
 *
 * @author lucia
 */
public class CanaliQASystem implements QASystem {

	private static String getCurrentState(ArrayList<AutocompleteObject> acceptedTokens) {
		if (acceptedTokens.isEmpty()) {
			return AutocompleteService.INITIAL_STATE_S0;
		}
		return acceptedTokens.get(acceptedTokens.size() - 1).state;
	}

	private static String getLastAcceptedProperty(ArrayList<AutocompleteObject> acceptedTokens) {
		int i = acceptedTokens.size() - 1;
		while (i > 0) {
			if ((!(acceptedTokens.get(i - 1)).state.equals(AutocompleteService.ACCEPT_PROPERTY_FOR_RANK_STATE_S9)) && (acceptedTokens.get(i).tokenType).equals(AutocompleteService.PROPERTY)) {
				return acceptedTokens.get(i).labels;
			}
			i--;
		}
		return null;
	}

	@SuppressWarnings("empty-statement")
	private static String[] getOpenVariables(ArrayList<AutocompleteObject> acceptedTokens, boolean onlyLast) {
		int i = acceptedTokens.size() - 1;
		String[] res = new String[3];
		res[0] = "";
		res[1] = "";
		res[2] = "";
		int contextVariablePosition = -1;

		while (i >= 0) {
			if (acceptedTokens.get(i).tokenType.equals(AutocompleteService.PROPERTY)) {
				if (contextVariablePosition == -1 || contextVariablePosition == i) {
					//System.out.println("label: " + acceptedTokens.get(i).labels);
					String[] ll = acceptedTokens.get(i).labels.split("\\|");
					for (int k = 0; k < ll.length; k++) {
						if (res[0] != null && res[0].length() > 0) { //!!! div da null
							res[0] += ",";
							res[1] += ",";
							res[2] += ",";
						}
						res[0] += ll[k];
						res[1] += acceptedTokens.get(i).text;
						res[2] += Integer.toString(i);
					}
					//      contextVariablePosition = acceptedTokens.get(i).relatedTokenPosition;
					if (onlyLast) {
						break;
					}
				}
			} else if (acceptedTokens.get(i).tokenType.equals(AutocompleteService.CLASS)) {
				if (contextVariablePosition == -1 || contextVariablePosition == i) {
					if (res[0].length() > 0) {
						res[0] += ",";
						res[1] += ",";
						res[2] += ",";
					}
					res[0] += acceptedTokens.get(i).labels;
					res[1] += acceptedTokens.get(i).text;
					res[2] += Integer.toString(i);
					if (acceptedTokens.get(i).relatedTokenPosition != null) // fix NullPointerException
						contextVariablePosition = acceptedTokens.get(i).relatedTokenPosition;
					if (onlyLast) {
						break;
					}
				}

			} else if (acceptedTokens.get(i).tokenType.equals(AutocompleteService.QUESTION_START) && acceptedTokens.get(i).labels.contains("has")) {
				if (res[0].length() > 0) {
					res[0] += ",";
					res[1] += ",";
					res[2] += ",";
				}
				res[0] += "http://www.w3.org/2002/07/owl#Thing";
				res[1] += acceptedTokens.get(i).text;
				res[2] += Integer.toString(i);
				contextVariablePosition = 0;
				if (onlyLast) {
					break;
				}
			}
			i--;

		}
		return res;
	}

	private static String getFinalPunctuation(ArrayList<AutocompleteObject> acceptedTokens) {
		if (acceptedTokens.isEmpty()) {
			return "?";
		}
		return acceptedTokens.get(0).finalPunctuation;
	}

	@Override
	public ArrayList<String> getAnswer(String query, String answerType) {
		ArrayList<String> answers = new ArrayList<String>();
		try {

			String lastAcceptedProperty = null; //p
			String[] openVariablesUri = null; //ou //!!!
			Integer[] openVariablesPosition = null; //op  //!!!
			String currentState = "0"; //s
			String finalPunctuation = null; //f
			boolean disableContextRules = false; //crd
			boolean autoAcceptance = true; //aa
			boolean dateToNumber = false; //dtn
			boolean useKeywords = false; //k

			boolean isEmpty = false;

			ArrayList<AutocompleteObject> acceptedTokens = new AutocompleteService().getAutocompleResults(query, lastAcceptedProperty, openVariablesUri, openVariablesPosition, currentState, finalPunctuation, disableContextRules, autoAcceptance, dateToNumber, useKeywords);
			if(acceptedTokens == null) 
				isEmpty = true;
			while (!query.equals(finalPunctuation) && !isEmpty) {
				//System.out.println("nel while");
				currentState = getCurrentState(acceptedTokens);
				if (acceptedTokens.size() > 0) {
					if ((acceptedTokens.get(acceptedTokens.size() - 1).state.equals(AutocompleteService.ACCEPT_OPERATOR_OR_DIRECT_OPERAND_STATE_S4) || acceptedTokens.get(acceptedTokens.size() - 1).state.equals(AutocompleteService.ACCEPT_DIRECT_OPERAND_STATE_S5)) && (acceptedTokens.get(acceptedTokens.size() - 1).labels.equals("year=") || acceptedTokens.get(acceptedTokens.size() - 1).labels.equals("month="))) {
						dateToNumber = true;
					}
					String lastAcceptedPropertyNew = getLastAcceptedProperty(acceptedTokens);
					if (lastAcceptedPropertyNew != null) {
						lastAcceptedProperty = lastAcceptedPropertyNew;
					}
					boolean propertyHaving = acceptedTokens.size() > 2 && acceptedTokens.get(acceptedTokens.size() - 1).state.equals(AutocompleteService.ACCEPT_PROPERTY_FOR_CONSTRAINT_STATE_S3) && acceptedTokens.get(acceptedTokens.size() - 2).state.equals(AutocompleteService.ACCEPT_OPERATOR_OR_DIRECT_OPERAND_STATE_S4);
					String[] openVariables = getOpenVariables(acceptedTokens, propertyHaving);
					if (openVariables[0] != null && !openVariables[0].equals("")) { //!!!
						//System.out.println("open variables [0]:" + openVariables[0]);

						openVariablesUri = openVariables[0].split(",");
						//System.out.println("open variables [2]:" + openVariables[2]);
						if (!openVariables[2].equals("")) {

							String[] intSplit = openVariables[2].split(",");
							openVariablesPosition = new Integer[intSplit.length];
							for (int i = 0; i < intSplit.length; i++) {
								openVariablesPosition[i] = Integer.parseInt(intSplit[i]);
							}

						}
					}
					finalPunctuation = getFinalPunctuation(acceptedTokens);
					query = acceptedTokens.get(acceptedTokens.size() - 1).remainder; ///!!!
				}

				ArrayList<AutocompleteObject> newTokens = new AutocompleteService().getAutocompleResults(query, lastAcceptedProperty, openVariablesUri, openVariablesPosition, currentState, finalPunctuation, disableContextRules, autoAcceptance, dateToNumber, useKeywords);
				if (newTokens != null && newTokens.size() > 0) {
					//System.out.println("newTokens = " + newTokens.get(0).text );
					acceptedTokens.add(newTokens.get(0));
				} else
					isEmpty = true;

			}

			if (!isEmpty) {
				String endpoint = "default";
				int limit = 100000;
				boolean disableSubclass = true;
				TranslationWrapper tWrapper = new TranslationService().translateQuery(acceptedTokens, endpoint, limit, disableSubclass);
				//System.out.println(tWrapper.getQuery());

				ResultWrapper rWrapper = new QueryService().answerQuery(acceptedTokens, endpoint, limit, disableSubclass);
				//System.out.println("+++rWrapper query = \n"+ rWrapper.);
				ArrayList<ResultObject> results = rWrapper.getResults();
				if (results != null && results.size() > 0) {
					for (ResultObject result : results) {
						// System.out.println(result.getL() + " " + result.getUe() + " " + result.getU() + " " + result.getId() + " " + result.getIde());
						if (result.getId() != null) {
							answers.add(result.getId());
						} else {
							answers.add(result.getL());
						}
					}
				} else {
					answers.add(QASystem.EMPTY_RESULT);
				}
			} else {
				answers.add(QASystem.EMPTY_RESULT);
			}
		} catch (Exception e) {
			answers.add(QASystem.EMPTY_RESULT);
			System.out.println(e);
			e.printStackTrace();
		}
		return answers;
	}
}
