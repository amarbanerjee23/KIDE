package com.operation.dsl.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.operation.dsl.services.OperationGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalOperationParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'Operation'", "'('", "','", "')'", "'{'", "'execute'", "'return'", "'}'", "'DataModel'", "'primitives'", "'composites'", "'.'", "'='", "'['", "']'", "'-'", "'false'", "'true'", "'E'", "'e'", "'int'", "'boolean'", "'float'", "'string'", "'object'", "'date'"
    };
    public static final int RULE_STRING=5;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__11=11;
    public static final int T__33=33;
    public static final int T__12=12;
    public static final int T__34=34;
    public static final int T__13=13;
    public static final int T__35=35;
    public static final int T__14=14;
    public static final int T__36=36;
    public static final int EOF=-1;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int RULE_ID=4;
    public static final int RULE_WS=9;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=6;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;

    // delegates
    // delegators


        public InternalOperationParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalOperationParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalOperationParser.tokenNames; }
    public String getGrammarFileName() { return "InternalOperation.g"; }



     	private OperationGrammarAccess grammarAccess;

        public InternalOperationParser(TokenStream input, OperationGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "OperationDescriptions";
       	}

       	@Override
       	protected OperationGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleOperationDescriptions"
    // InternalOperation.g:65:1: entryRuleOperationDescriptions returns [EObject current=null] : iv_ruleOperationDescriptions= ruleOperationDescriptions EOF ;
    public final EObject entryRuleOperationDescriptions() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOperationDescriptions = null;


        try {
            // InternalOperation.g:65:62: (iv_ruleOperationDescriptions= ruleOperationDescriptions EOF )
            // InternalOperation.g:66:2: iv_ruleOperationDescriptions= ruleOperationDescriptions EOF
            {
             newCompositeNode(grammarAccess.getOperationDescriptionsRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleOperationDescriptions=ruleOperationDescriptions();

            state._fsp--;

             current =iv_ruleOperationDescriptions; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOperationDescriptions"


    // $ANTLR start "ruleOperationDescriptions"
    // InternalOperation.g:72:1: ruleOperationDescriptions returns [EObject current=null] : ( () ( ( (lv_operations_1_0= ruleOperation ) ) ( (lv_operations_2_0= ruleOperation ) )* )? ) ;
    public final EObject ruleOperationDescriptions() throws RecognitionException {
        EObject current = null;

        EObject lv_operations_1_0 = null;

        EObject lv_operations_2_0 = null;



        	enterRule();

        try {
            // InternalOperation.g:78:2: ( ( () ( ( (lv_operations_1_0= ruleOperation ) ) ( (lv_operations_2_0= ruleOperation ) )* )? ) )
            // InternalOperation.g:79:2: ( () ( ( (lv_operations_1_0= ruleOperation ) ) ( (lv_operations_2_0= ruleOperation ) )* )? )
            {
            // InternalOperation.g:79:2: ( () ( ( (lv_operations_1_0= ruleOperation ) ) ( (lv_operations_2_0= ruleOperation ) )* )? )
            // InternalOperation.g:80:3: () ( ( (lv_operations_1_0= ruleOperation ) ) ( (lv_operations_2_0= ruleOperation ) )* )?
            {
            // InternalOperation.g:80:3: ()
            // InternalOperation.g:81:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getOperationDescriptionsAccess().getOperationDescriptionsAction_0(),
            					current);
            			

            }

            // InternalOperation.g:87:3: ( ( (lv_operations_1_0= ruleOperation ) ) ( (lv_operations_2_0= ruleOperation ) )* )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==11) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // InternalOperation.g:88:4: ( (lv_operations_1_0= ruleOperation ) ) ( (lv_operations_2_0= ruleOperation ) )*
                    {
                    // InternalOperation.g:88:4: ( (lv_operations_1_0= ruleOperation ) )
                    // InternalOperation.g:89:5: (lv_operations_1_0= ruleOperation )
                    {
                    // InternalOperation.g:89:5: (lv_operations_1_0= ruleOperation )
                    // InternalOperation.g:90:6: lv_operations_1_0= ruleOperation
                    {

                    						newCompositeNode(grammarAccess.getOperationDescriptionsAccess().getOperationsOperationParserRuleCall_1_0_0());
                    					
                    pushFollow(FOLLOW_3);
                    lv_operations_1_0=ruleOperation();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getOperationDescriptionsRule());
                    						}
                    						add(
                    							current,
                    							"operations",
                    							lv_operations_1_0,
                    							"com.operation.dsl.Operation.Operation");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalOperation.g:107:4: ( (lv_operations_2_0= ruleOperation ) )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==11) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // InternalOperation.g:108:5: (lv_operations_2_0= ruleOperation )
                    	    {
                    	    // InternalOperation.g:108:5: (lv_operations_2_0= ruleOperation )
                    	    // InternalOperation.g:109:6: lv_operations_2_0= ruleOperation
                    	    {

                    	    						newCompositeNode(grammarAccess.getOperationDescriptionsAccess().getOperationsOperationParserRuleCall_1_1_0());
                    	    					
                    	    pushFollow(FOLLOW_3);
                    	    lv_operations_2_0=ruleOperation();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getOperationDescriptionsRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"operations",
                    	    							lv_operations_2_0,
                    	    							"com.operation.dsl.Operation.Operation");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOperationDescriptions"


    // $ANTLR start "entryRuleOperation"
    // InternalOperation.g:131:1: entryRuleOperation returns [EObject current=null] : iv_ruleOperation= ruleOperation EOF ;
    public final EObject entryRuleOperation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOperation = null;


        try {
            // InternalOperation.g:131:50: (iv_ruleOperation= ruleOperation EOF )
            // InternalOperation.g:132:2: iv_ruleOperation= ruleOperation EOF
            {
             newCompositeNode(grammarAccess.getOperationRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleOperation=ruleOperation();

            state._fsp--;

             current =iv_ruleOperation; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOperation"


    // $ANTLR start "ruleOperation"
    // InternalOperation.g:138:1: ruleOperation returns [EObject current=null] : ( () otherlv_1= 'Operation' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '(' ( ( (lv_inputParameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) ) )* )? otherlv_7= ')' otherlv_8= '{' (otherlv_9= 'execute' ( (lv_executableScript_10_0= ruleEString ) ) )? (otherlv_11= 'return' ( (lv_outputParameters_12_0= ruleParameter ) ) )? otherlv_13= '}' ) ;
    public final EObject ruleOperation() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_inputParameters_4_0 = null;

        EObject lv_inputParameters_6_0 = null;

        AntlrDatatypeRuleToken lv_executableScript_10_0 = null;

        EObject lv_outputParameters_12_0 = null;



        	enterRule();

        try {
            // InternalOperation.g:144:2: ( ( () otherlv_1= 'Operation' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '(' ( ( (lv_inputParameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) ) )* )? otherlv_7= ')' otherlv_8= '{' (otherlv_9= 'execute' ( (lv_executableScript_10_0= ruleEString ) ) )? (otherlv_11= 'return' ( (lv_outputParameters_12_0= ruleParameter ) ) )? otherlv_13= '}' ) )
            // InternalOperation.g:145:2: ( () otherlv_1= 'Operation' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '(' ( ( (lv_inputParameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) ) )* )? otherlv_7= ')' otherlv_8= '{' (otherlv_9= 'execute' ( (lv_executableScript_10_0= ruleEString ) ) )? (otherlv_11= 'return' ( (lv_outputParameters_12_0= ruleParameter ) ) )? otherlv_13= '}' )
            {
            // InternalOperation.g:145:2: ( () otherlv_1= 'Operation' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '(' ( ( (lv_inputParameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) ) )* )? otherlv_7= ')' otherlv_8= '{' (otherlv_9= 'execute' ( (lv_executableScript_10_0= ruleEString ) ) )? (otherlv_11= 'return' ( (lv_outputParameters_12_0= ruleParameter ) ) )? otherlv_13= '}' )
            // InternalOperation.g:146:3: () otherlv_1= 'Operation' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '(' ( ( (lv_inputParameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) ) )* )? otherlv_7= ')' otherlv_8= '{' (otherlv_9= 'execute' ( (lv_executableScript_10_0= ruleEString ) ) )? (otherlv_11= 'return' ( (lv_outputParameters_12_0= ruleParameter ) ) )? otherlv_13= '}'
            {
            // InternalOperation.g:146:3: ()
            // InternalOperation.g:147:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getOperationAccess().getOperationAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,11,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getOperationAccess().getOperationKeyword_1());
            		
            // InternalOperation.g:157:3: ( (lv_name_2_0= ruleEString ) )
            // InternalOperation.g:158:4: (lv_name_2_0= ruleEString )
            {
            // InternalOperation.g:158:4: (lv_name_2_0= ruleEString )
            // InternalOperation.g:159:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getOperationAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_5);
            lv_name_2_0=ruleEString();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getOperationRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"com.dml.dsl.Dml.EString");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_3=(Token)match(input,12,FOLLOW_6); 

            			newLeafNode(otherlv_3, grammarAccess.getOperationAccess().getLeftParenthesisKeyword_3());
            		
            // InternalOperation.g:180:3: ( ( (lv_inputParameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) ) )* )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_ID||(LA4_0>=31 && LA4_0<=36)) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // InternalOperation.g:181:4: ( (lv_inputParameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) ) )*
                    {
                    // InternalOperation.g:181:4: ( (lv_inputParameters_4_0= ruleParameter ) )
                    // InternalOperation.g:182:5: (lv_inputParameters_4_0= ruleParameter )
                    {
                    // InternalOperation.g:182:5: (lv_inputParameters_4_0= ruleParameter )
                    // InternalOperation.g:183:6: lv_inputParameters_4_0= ruleParameter
                    {

                    						newCompositeNode(grammarAccess.getOperationAccess().getInputParametersParameterParserRuleCall_4_0_0());
                    					
                    pushFollow(FOLLOW_7);
                    lv_inputParameters_4_0=ruleParameter();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getOperationRule());
                    						}
                    						add(
                    							current,
                    							"inputParameters",
                    							lv_inputParameters_4_0,
                    							"com.dml.dsl.Dml.Parameter");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalOperation.g:200:4: (otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) ) )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==13) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // InternalOperation.g:201:5: otherlv_5= ',' ( (lv_inputParameters_6_0= ruleParameter ) )
                    	    {
                    	    otherlv_5=(Token)match(input,13,FOLLOW_8); 

                    	    					newLeafNode(otherlv_5, grammarAccess.getOperationAccess().getCommaKeyword_4_1_0());
                    	    				
                    	    // InternalOperation.g:205:5: ( (lv_inputParameters_6_0= ruleParameter ) )
                    	    // InternalOperation.g:206:6: (lv_inputParameters_6_0= ruleParameter )
                    	    {
                    	    // InternalOperation.g:206:6: (lv_inputParameters_6_0= ruleParameter )
                    	    // InternalOperation.g:207:7: lv_inputParameters_6_0= ruleParameter
                    	    {

                    	    							newCompositeNode(grammarAccess.getOperationAccess().getInputParametersParameterParserRuleCall_4_1_1_0());
                    	    						
                    	    pushFollow(FOLLOW_7);
                    	    lv_inputParameters_6_0=ruleParameter();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getOperationRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"inputParameters",
                    	    								lv_inputParameters_6_0,
                    	    								"com.dml.dsl.Dml.Parameter");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_7=(Token)match(input,14,FOLLOW_9); 

            			newLeafNode(otherlv_7, grammarAccess.getOperationAccess().getRightParenthesisKeyword_5());
            		
            otherlv_8=(Token)match(input,15,FOLLOW_10); 

            			newLeafNode(otherlv_8, grammarAccess.getOperationAccess().getLeftCurlyBracketKeyword_6());
            		
            // InternalOperation.g:234:3: (otherlv_9= 'execute' ( (lv_executableScript_10_0= ruleEString ) ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==16) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalOperation.g:235:4: otherlv_9= 'execute' ( (lv_executableScript_10_0= ruleEString ) )
                    {
                    otherlv_9=(Token)match(input,16,FOLLOW_4); 

                    				newLeafNode(otherlv_9, grammarAccess.getOperationAccess().getExecuteKeyword_7_0());
                    			
                    // InternalOperation.g:239:4: ( (lv_executableScript_10_0= ruleEString ) )
                    // InternalOperation.g:240:5: (lv_executableScript_10_0= ruleEString )
                    {
                    // InternalOperation.g:240:5: (lv_executableScript_10_0= ruleEString )
                    // InternalOperation.g:241:6: lv_executableScript_10_0= ruleEString
                    {

                    						newCompositeNode(grammarAccess.getOperationAccess().getExecutableScriptEStringParserRuleCall_7_1_0());
                    					
                    pushFollow(FOLLOW_11);
                    lv_executableScript_10_0=ruleEString();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getOperationRule());
                    						}
                    						set(
                    							current,
                    							"executableScript",
                    							lv_executableScript_10_0,
                    							"com.dml.dsl.Dml.EString");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            // InternalOperation.g:259:3: (otherlv_11= 'return' ( (lv_outputParameters_12_0= ruleParameter ) ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==17) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // InternalOperation.g:260:4: otherlv_11= 'return' ( (lv_outputParameters_12_0= ruleParameter ) )
                    {
                    otherlv_11=(Token)match(input,17,FOLLOW_8); 

                    				newLeafNode(otherlv_11, grammarAccess.getOperationAccess().getReturnKeyword_8_0());
                    			
                    // InternalOperation.g:264:4: ( (lv_outputParameters_12_0= ruleParameter ) )
                    // InternalOperation.g:265:5: (lv_outputParameters_12_0= ruleParameter )
                    {
                    // InternalOperation.g:265:5: (lv_outputParameters_12_0= ruleParameter )
                    // InternalOperation.g:266:6: lv_outputParameters_12_0= ruleParameter
                    {

                    						newCompositeNode(grammarAccess.getOperationAccess().getOutputParametersParameterParserRuleCall_8_1_0());
                    					
                    pushFollow(FOLLOW_12);
                    lv_outputParameters_12_0=ruleParameter();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getOperationRule());
                    						}
                    						set(
                    							current,
                    							"outputParameters",
                    							lv_outputParameters_12_0,
                    							"com.dml.dsl.Dml.Parameter");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            otherlv_13=(Token)match(input,18,FOLLOW_2); 

            			newLeafNode(otherlv_13, grammarAccess.getOperationAccess().getRightCurlyBracketKeyword_9());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOperation"


    // $ANTLR start "entryRuleDataModel"
    // InternalOperation.g:292:1: entryRuleDataModel returns [EObject current=null] : iv_ruleDataModel= ruleDataModel EOF ;
    public final EObject entryRuleDataModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataModel = null;


        try {
            // InternalOperation.g:292:50: (iv_ruleDataModel= ruleDataModel EOF )
            // InternalOperation.g:293:2: iv_ruleDataModel= ruleDataModel EOF
            {
             newCompositeNode(grammarAccess.getDataModelRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDataModel=ruleDataModel();

            state._fsp--;

             current =iv_ruleDataModel; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDataModel"


    // $ANTLR start "ruleDataModel"
    // InternalOperation.g:299:1: ruleDataModel returns [EObject current=null] : ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) ) ;
    public final EObject ruleDataModel() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_16=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_primitives_6_0 = null;

        EObject lv_primitives_8_0 = null;



        	enterRule();

        try {
            // InternalOperation.g:305:2: ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) ) )
            // InternalOperation.g:306:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) )
            {
            // InternalOperation.g:306:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) )
            // InternalOperation.g:307:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) )
            {
            // InternalOperation.g:307:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) )
            // InternalOperation.g:308:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?)
            {
             
            			  getUnorderedGroupHelper().enter(grammarAccess.getDataModelAccess().getUnorderedGroup());
            			
            // InternalOperation.g:311:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?)
            // InternalOperation.g:312:5: ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?
            {
            // InternalOperation.g:312:5: ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+
            int cnt11=0;
            loop11:
            do {
                int alt11=3;
                int LA11_0 = input.LA(1);

                if ( LA11_0 == 19 && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
                    alt11=1;
                }
                else if ( ( LA11_0 == 18 || LA11_0 == 21 ) && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
                    alt11=2;
                }


                switch (alt11) {
            	case 1 :
            	    // InternalOperation.g:313:3: ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) )
            	    {
            	    // InternalOperation.g:313:3: ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) )
            	    // InternalOperation.g:314:4: {...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0)");
            	    }
            	    // InternalOperation.g:314:103: ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) )
            	    // InternalOperation.g:315:5: ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0);
            	    				
            	    // InternalOperation.g:318:8: ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) )
            	    // InternalOperation.g:318:9: {...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "true");
            	    }
            	    // InternalOperation.g:318:18: (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? )
            	    // InternalOperation.g:318:19: otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )?
            	    {
            	    otherlv_1=(Token)match(input,19,FOLLOW_4); 

            	    								newLeafNode(otherlv_1, grammarAccess.getDataModelAccess().getDataModelKeyword_0_0());
            	    							
            	    // InternalOperation.g:322:8: ( (lv_name_2_0= ruleEString ) )
            	    // InternalOperation.g:323:9: (lv_name_2_0= ruleEString )
            	    {
            	    // InternalOperation.g:323:9: (lv_name_2_0= ruleEString )
            	    // InternalOperation.g:324:10: lv_name_2_0= ruleEString
            	    {

            	    										newCompositeNode(grammarAccess.getDataModelAccess().getNameEStringParserRuleCall_0_1_0());
            	    									
            	    pushFollow(FOLLOW_9);
            	    lv_name_2_0=ruleEString();

            	    state._fsp--;


            	    										if (current==null) {
            	    											current = createModelElementForParent(grammarAccess.getDataModelRule());
            	    										}
            	    										set(
            	    											current,
            	    											"name",
            	    											lv_name_2_0,
            	    											"com.dml.dsl.Dml.EString");
            	    										afterParserOrEnumRuleCall();
            	    									

            	    }


            	    }

            	    otherlv_3=(Token)match(input,15,FOLLOW_13); 

            	    								newLeafNode(otherlv_3, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_2());
            	    							
            	    // InternalOperation.g:345:8: (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )?
            	    int alt8=2;
            	    int LA8_0 = input.LA(1);

            	    if ( (LA8_0==20) ) {
            	        alt8=1;
            	    }
            	    switch (alt8) {
            	        case 1 :
            	            // InternalOperation.g:346:9: otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}'
            	            {
            	            otherlv_4=(Token)match(input,20,FOLLOW_9); 

            	            									newLeafNode(otherlv_4, grammarAccess.getDataModelAccess().getPrimitivesKeyword_0_3_0());
            	            								
            	            otherlv_5=(Token)match(input,15,FOLLOW_8); 

            	            									newLeafNode(otherlv_5, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_3_1());
            	            								
            	            // InternalOperation.g:354:9: ( (lv_primitives_6_0= ruleParameter ) )
            	            // InternalOperation.g:355:10: (lv_primitives_6_0= ruleParameter )
            	            {
            	            // InternalOperation.g:355:10: (lv_primitives_6_0= ruleParameter )
            	            // InternalOperation.g:356:11: lv_primitives_6_0= ruleParameter
            	            {

            	            											newCompositeNode(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_2_0());
            	            										
            	            pushFollow(FOLLOW_14);
            	            lv_primitives_6_0=ruleParameter();

            	            state._fsp--;


            	            											if (current==null) {
            	            												current = createModelElementForParent(grammarAccess.getDataModelRule());
            	            											}
            	            											add(
            	            												current,
            	            												"primitives",
            	            												lv_primitives_6_0,
            	            												"com.dml.dsl.Dml.Parameter");
            	            											afterParserOrEnumRuleCall();
            	            										

            	            }


            	            }

            	            // InternalOperation.g:373:9: (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )*
            	            loop7:
            	            do {
            	                int alt7=2;
            	                int LA7_0 = input.LA(1);

            	                if ( (LA7_0==13) ) {
            	                    alt7=1;
            	                }


            	                switch (alt7) {
            	            	case 1 :
            	            	    // InternalOperation.g:374:10: otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) )
            	            	    {
            	            	    otherlv_7=(Token)match(input,13,FOLLOW_8); 

            	            	    										newLeafNode(otherlv_7, grammarAccess.getDataModelAccess().getCommaKeyword_0_3_3_0());
            	            	    									
            	            	    // InternalOperation.g:378:10: ( (lv_primitives_8_0= ruleParameter ) )
            	            	    // InternalOperation.g:379:11: (lv_primitives_8_0= ruleParameter )
            	            	    {
            	            	    // InternalOperation.g:379:11: (lv_primitives_8_0= ruleParameter )
            	            	    // InternalOperation.g:380:12: lv_primitives_8_0= ruleParameter
            	            	    {

            	            	    												newCompositeNode(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_3_1_0());
            	            	    											
            	            	    pushFollow(FOLLOW_14);
            	            	    lv_primitives_8_0=ruleParameter();

            	            	    state._fsp--;


            	            	    												if (current==null) {
            	            	    													current = createModelElementForParent(grammarAccess.getDataModelRule());
            	            	    												}
            	            	    												add(
            	            	    													current,
            	            	    													"primitives",
            	            	    													lv_primitives_8_0,
            	            	    													"com.dml.dsl.Dml.Parameter");
            	            	    												afterParserOrEnumRuleCall();
            	            	    											

            	            	    }


            	            	    }


            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop7;
            	                }
            	            } while (true);

            	            otherlv_9=(Token)match(input,18,FOLLOW_15); 

            	            									newLeafNode(otherlv_9, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_0_3_4());
            	            								

            	            }
            	            break;

            	    }


            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getDataModelAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalOperation.g:409:3: ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) )
            	    {
            	    // InternalOperation.g:409:3: ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) )
            	    // InternalOperation.g:410:4: {...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1)");
            	    }
            	    // InternalOperation.g:410:103: ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) )
            	    // InternalOperation.g:411:5: ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1);
            	    				
            	    // InternalOperation.g:414:8: ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) )
            	    // InternalOperation.g:414:9: {...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "true");
            	    }
            	    // InternalOperation.g:414:18: ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' )
            	    // InternalOperation.g:414:19: (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}'
            	    {
            	    // InternalOperation.g:414:19: (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )?
            	    int alt10=2;
            	    int LA10_0 = input.LA(1);

            	    if ( (LA10_0==21) ) {
            	        alt10=1;
            	    }
            	    switch (alt10) {
            	        case 1 :
            	            // InternalOperation.g:415:9: otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}'
            	            {
            	            otherlv_10=(Token)match(input,21,FOLLOW_9); 

            	            									newLeafNode(otherlv_10, grammarAccess.getDataModelAccess().getCompositesKeyword_1_0_0());
            	            								
            	            otherlv_11=(Token)match(input,15,FOLLOW_16); 

            	            									newLeafNode(otherlv_11, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_1_0_1());
            	            								
            	            // InternalOperation.g:423:9: ( (otherlv_12= RULE_ID ) )
            	            // InternalOperation.g:424:10: (otherlv_12= RULE_ID )
            	            {
            	            // InternalOperation.g:424:10: (otherlv_12= RULE_ID )
            	            // InternalOperation.g:425:11: otherlv_12= RULE_ID
            	            {

            	            											if (current==null) {
            	            												current = createModelElement(grammarAccess.getDataModelRule());
            	            											}
            	            										
            	            otherlv_12=(Token)match(input,RULE_ID,FOLLOW_14); 

            	            											newLeafNode(otherlv_12, grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_2_0());
            	            										

            	            }


            	            }

            	            // InternalOperation.g:436:9: (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )*
            	            loop9:
            	            do {
            	                int alt9=2;
            	                int LA9_0 = input.LA(1);

            	                if ( (LA9_0==13) ) {
            	                    alt9=1;
            	                }


            	                switch (alt9) {
            	            	case 1 :
            	            	    // InternalOperation.g:437:10: otherlv_13= ',' ( (otherlv_14= RULE_ID ) )
            	            	    {
            	            	    otherlv_13=(Token)match(input,13,FOLLOW_16); 

            	            	    										newLeafNode(otherlv_13, grammarAccess.getDataModelAccess().getCommaKeyword_1_0_3_0());
            	            	    									
            	            	    // InternalOperation.g:441:10: ( (otherlv_14= RULE_ID ) )
            	            	    // InternalOperation.g:442:11: (otherlv_14= RULE_ID )
            	            	    {
            	            	    // InternalOperation.g:442:11: (otherlv_14= RULE_ID )
            	            	    // InternalOperation.g:443:12: otherlv_14= RULE_ID
            	            	    {

            	            	    												if (current==null) {
            	            	    													current = createModelElement(grammarAccess.getDataModelRule());
            	            	    												}
            	            	    											
            	            	    otherlv_14=(Token)match(input,RULE_ID,FOLLOW_14); 

            	            	    												newLeafNode(otherlv_14, grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_3_1_0());
            	            	    											

            	            	    }


            	            	    }


            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop9;
            	                }
            	            } while (true);

            	            otherlv_15=(Token)match(input,18,FOLLOW_12); 

            	            									newLeafNode(otherlv_15, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_0_4());
            	            								

            	            }
            	            break;

            	    }

            	    otherlv_16=(Token)match(input,18,FOLLOW_15); 

            	    								newLeafNode(otherlv_16, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_1());
            	    							

            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getDataModelAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);

            if ( ! getUnorderedGroupHelper().canLeave(grammarAccess.getDataModelAccess().getUnorderedGroup()) ) {
                throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canLeave(grammarAccess.getDataModelAccess().getUnorderedGroup())");
            }

            }


            }

             
            			  getUnorderedGroupHelper().leave(grammarAccess.getDataModelAccess().getUnorderedGroup());
            			

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDataModel"


    // $ANTLR start "entryRuleParameter"
    // InternalOperation.g:481:1: entryRuleParameter returns [EObject current=null] : iv_ruleParameter= ruleParameter EOF ;
    public final EObject entryRuleParameter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParameter = null;


        try {
            // InternalOperation.g:481:50: (iv_ruleParameter= ruleParameter EOF )
            // InternalOperation.g:482:2: iv_ruleParameter= ruleParameter EOF
            {
             newCompositeNode(grammarAccess.getParameterRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleParameter=ruleParameter();

            state._fsp--;

             current =iv_ruleParameter; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleParameter"


    // $ANTLR start "ruleParameter"
    // InternalOperation.g:488:1: ruleParameter returns [EObject current=null] : (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType ) ;
    public final EObject ruleParameter() throws RecognitionException {
        EObject current = null;

        EObject this_SimpleType_0 = null;

        EObject this_AbstractType_1 = null;

        EObject this_ArrayType_2 = null;



        	enterRule();

        try {
            // InternalOperation.g:494:2: ( (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType ) )
            // InternalOperation.g:495:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )
            {
            // InternalOperation.g:495:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )
            int alt12=3;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // InternalOperation.g:496:3: this_SimpleType_0= ruleSimpleType
                    {

                    			newCompositeNode(grammarAccess.getParameterAccess().getSimpleTypeParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_SimpleType_0=ruleSimpleType();

                    state._fsp--;


                    			current = this_SimpleType_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalOperation.g:505:3: this_AbstractType_1= ruleAbstractType
                    {

                    			newCompositeNode(grammarAccess.getParameterAccess().getAbstractTypeParserRuleCall_1());
                    		
                    pushFollow(FOLLOW_2);
                    this_AbstractType_1=ruleAbstractType();

                    state._fsp--;


                    			current = this_AbstractType_1;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 3 :
                    // InternalOperation.g:514:3: this_ArrayType_2= ruleArrayType
                    {

                    			newCompositeNode(grammarAccess.getParameterAccess().getArrayTypeParserRuleCall_2());
                    		
                    pushFollow(FOLLOW_2);
                    this_ArrayType_2=ruleArrayType();

                    state._fsp--;


                    			current = this_ArrayType_2;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleParameter"


    // $ANTLR start "entryRuleQualifiedName"
    // InternalOperation.g:526:1: entryRuleQualifiedName returns [String current=null] : iv_ruleQualifiedName= ruleQualifiedName EOF ;
    public final String entryRuleQualifiedName() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQualifiedName = null;


        try {
            // InternalOperation.g:526:53: (iv_ruleQualifiedName= ruleQualifiedName EOF )
            // InternalOperation.g:527:2: iv_ruleQualifiedName= ruleQualifiedName EOF
            {
             newCompositeNode(grammarAccess.getQualifiedNameRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleQualifiedName=ruleQualifiedName();

            state._fsp--;

             current =iv_ruleQualifiedName.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQualifiedName"


    // $ANTLR start "ruleQualifiedName"
    // InternalOperation.g:533:1: ruleQualifiedName returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQualifiedName() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;


        	enterRule();

        try {
            // InternalOperation.g:539:2: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // InternalOperation.g:540:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // InternalOperation.g:540:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // InternalOperation.g:541:3: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_17); 

            			current.merge(this_ID_0);
            		

            			newLeafNode(this_ID_0, grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_0());
            		
            // InternalOperation.g:548:3: (kw= '.' this_ID_2= RULE_ID )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==22) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // InternalOperation.g:549:4: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,22,FOLLOW_16); 

            	    				current.merge(kw);
            	    				newLeafNode(kw, grammarAccess.getQualifiedNameAccess().getFullStopKeyword_1_0());
            	    			
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_17); 

            	    				current.merge(this_ID_2);
            	    			

            	    				newLeafNode(this_ID_2, grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_1_1());
            	    			

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQualifiedName"


    // $ANTLR start "entryRuleSimpleType"
    // InternalOperation.g:566:1: entryRuleSimpleType returns [EObject current=null] : iv_ruleSimpleType= ruleSimpleType EOF ;
    public final EObject entryRuleSimpleType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleType = null;


        try {
            // InternalOperation.g:566:51: (iv_ruleSimpleType= ruleSimpleType EOF )
            // InternalOperation.g:567:2: iv_ruleSimpleType= ruleSimpleType EOF
            {
             newCompositeNode(grammarAccess.getSimpleTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleSimpleType=ruleSimpleType();

            state._fsp--;

             current =iv_ruleSimpleType; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSimpleType"


    // $ANTLR start "ruleSimpleType"
    // InternalOperation.g:573:1: ruleSimpleType returns [EObject current=null] : ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? ) ;
    public final EObject ruleSimpleType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        Enumerator lv_type_1_0 = null;

        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalOperation.g:579:2: ( ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? ) )
            // InternalOperation.g:580:2: ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? )
            {
            // InternalOperation.g:580:2: ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? )
            // InternalOperation.g:581:3: () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )?
            {
            // InternalOperation.g:581:3: ()
            // InternalOperation.g:582:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getSimpleTypeAccess().getSimpleTypeAction_0(),
            					current);
            			

            }

            // InternalOperation.g:588:3: ( (lv_type_1_0= rulePrimitiveValueType ) )
            // InternalOperation.g:589:4: (lv_type_1_0= rulePrimitiveValueType )
            {
            // InternalOperation.g:589:4: (lv_type_1_0= rulePrimitiveValueType )
            // InternalOperation.g:590:5: lv_type_1_0= rulePrimitiveValueType
            {

            					newCompositeNode(grammarAccess.getSimpleTypeAccess().getTypePrimitiveValueTypeEnumRuleCall_1_0());
            				
            pushFollow(FOLLOW_4);
            lv_type_1_0=rulePrimitiveValueType();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getSimpleTypeRule());
            					}
            					set(
            						current,
            						"type",
            						lv_type_1_0,
            						"com.dml.dsl.Dml.PrimitiveValueType");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalOperation.g:607:3: ( (lv_name_2_0= ruleEString ) )
            // InternalOperation.g:608:4: (lv_name_2_0= ruleEString )
            {
            // InternalOperation.g:608:4: (lv_name_2_0= ruleEString )
            // InternalOperation.g:609:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getSimpleTypeAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_18);
            lv_name_2_0=ruleEString();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getSimpleTypeRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"com.dml.dsl.Dml.EString");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalOperation.g:626:3: (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==23) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // InternalOperation.g:627:4: otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) )
                    {
                    otherlv_3=(Token)match(input,23,FOLLOW_19); 

                    				newLeafNode(otherlv_3, grammarAccess.getSimpleTypeAccess().getEqualsSignKeyword_3_0());
                    			
                    // InternalOperation.g:631:4: ( (lv_value_4_0= rulePrimitiveValue ) )
                    // InternalOperation.g:632:5: (lv_value_4_0= rulePrimitiveValue )
                    {
                    // InternalOperation.g:632:5: (lv_value_4_0= rulePrimitiveValue )
                    // InternalOperation.g:633:6: lv_value_4_0= rulePrimitiveValue
                    {

                    						newCompositeNode(grammarAccess.getSimpleTypeAccess().getValuePrimitiveValueParserRuleCall_3_1_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_value_4_0=rulePrimitiveValue();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getSimpleTypeRule());
                    						}
                    						set(
                    							current,
                    							"value",
                    							lv_value_4_0,
                    							"com.dml.dsl.Dml.PrimitiveValue");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSimpleType"


    // $ANTLR start "entryRuleAbstractType"
    // InternalOperation.g:655:1: entryRuleAbstractType returns [EObject current=null] : iv_ruleAbstractType= ruleAbstractType EOF ;
    public final EObject entryRuleAbstractType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractType = null;


        try {
            // InternalOperation.g:655:53: (iv_ruleAbstractType= ruleAbstractType EOF )
            // InternalOperation.g:656:2: iv_ruleAbstractType= ruleAbstractType EOF
            {
             newCompositeNode(grammarAccess.getAbstractTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAbstractType=ruleAbstractType();

            state._fsp--;

             current =iv_ruleAbstractType; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAbstractType"


    // $ANTLR start "ruleAbstractType"
    // InternalOperation.g:662:1: ruleAbstractType returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? ) ;
    public final EObject ruleAbstractType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalOperation.g:668:2: ( ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? ) )
            // InternalOperation.g:669:2: ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? )
            {
            // InternalOperation.g:669:2: ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? )
            // InternalOperation.g:670:3: () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )?
            {
            // InternalOperation.g:670:3: ()
            // InternalOperation.g:671:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getAbstractTypeAccess().getAbstractTypeAction_0(),
            					current);
            			

            }

            // InternalOperation.g:677:3: ( ( ruleQualifiedName ) )
            // InternalOperation.g:678:4: ( ruleQualifiedName )
            {
            // InternalOperation.g:678:4: ( ruleQualifiedName )
            // InternalOperation.g:679:5: ruleQualifiedName
            {

            					if (current==null) {
            						current = createModelElement(grammarAccess.getAbstractTypeRule());
            					}
            				

            					newCompositeNode(grammarAccess.getAbstractTypeAccess().getTypeDataModelCrossReference_1_0());
            				
            pushFollow(FOLLOW_4);
            ruleQualifiedName();

            state._fsp--;


            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalOperation.g:693:3: ( (lv_name_2_0= ruleEString ) )
            // InternalOperation.g:694:4: (lv_name_2_0= ruleEString )
            {
            // InternalOperation.g:694:4: (lv_name_2_0= ruleEString )
            // InternalOperation.g:695:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getAbstractTypeAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_18);
            lv_name_2_0=ruleEString();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getAbstractTypeRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"com.dml.dsl.Dml.EString");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalOperation.g:712:3: (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==23) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // InternalOperation.g:713:4: otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) )
                    {
                    otherlv_3=(Token)match(input,23,FOLLOW_19); 

                    				newLeafNode(otherlv_3, grammarAccess.getAbstractTypeAccess().getEqualsSignKeyword_3_0());
                    			
                    // InternalOperation.g:717:4: ( (lv_value_4_0= ruleAbstractObjectValue ) )
                    // InternalOperation.g:718:5: (lv_value_4_0= ruleAbstractObjectValue )
                    {
                    // InternalOperation.g:718:5: (lv_value_4_0= ruleAbstractObjectValue )
                    // InternalOperation.g:719:6: lv_value_4_0= ruleAbstractObjectValue
                    {

                    						newCompositeNode(grammarAccess.getAbstractTypeAccess().getValueAbstractObjectValueParserRuleCall_3_1_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_value_4_0=ruleAbstractObjectValue();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getAbstractTypeRule());
                    						}
                    						set(
                    							current,
                    							"value",
                    							lv_value_4_0,
                    							"com.dml.dsl.Dml.AbstractObjectValue");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAbstractType"


    // $ANTLR start "entryRulePrimitiveValue"
    // InternalOperation.g:741:1: entryRulePrimitiveValue returns [EObject current=null] : iv_rulePrimitiveValue= rulePrimitiveValue EOF ;
    public final EObject entryRulePrimitiveValue() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimitiveValue = null;


        try {
            // InternalOperation.g:741:55: (iv_rulePrimitiveValue= rulePrimitiveValue EOF )
            // InternalOperation.g:742:2: iv_rulePrimitiveValue= rulePrimitiveValue EOF
            {
             newCompositeNode(grammarAccess.getPrimitiveValueRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePrimitiveValue=rulePrimitiveValue();

            state._fsp--;

             current =iv_rulePrimitiveValue; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePrimitiveValue"


    // $ANTLR start "rulePrimitiveValue"
    // InternalOperation.g:748:1: rulePrimitiveValue returns [EObject current=null] : ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue ) ;
    public final EObject rulePrimitiveValue() throws RecognitionException {
        EObject current = null;

        Token lv_stringValue_5_0=null;
        AntlrDatatypeRuleToken lv_intValue_1_0 = null;

        AntlrDatatypeRuleToken lv_floatValue_3_0 = null;

        AntlrDatatypeRuleToken lv_boolValue_7_0 = null;

        AntlrDatatypeRuleToken lv_dateValue_9_0 = null;

        EObject this_ArrayValues_10 = null;

        EObject this_AbstractObjectValue_11 = null;



        	enterRule();

        try {
            // InternalOperation.g:754:2: ( ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue ) )
            // InternalOperation.g:755:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )
            {
            // InternalOperation.g:755:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )
            int alt16=7;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // InternalOperation.g:756:3: ( () ( (lv_intValue_1_0= ruleEInt ) ) )
                    {
                    // InternalOperation.g:756:3: ( () ( (lv_intValue_1_0= ruleEInt ) ) )
                    // InternalOperation.g:757:4: () ( (lv_intValue_1_0= ruleEInt ) )
                    {
                    // InternalOperation.g:757:4: ()
                    // InternalOperation.g:758:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getIntValueAction_0_0(),
                    						current);
                    				

                    }

                    // InternalOperation.g:764:4: ( (lv_intValue_1_0= ruleEInt ) )
                    // InternalOperation.g:765:5: (lv_intValue_1_0= ruleEInt )
                    {
                    // InternalOperation.g:765:5: (lv_intValue_1_0= ruleEInt )
                    // InternalOperation.g:766:6: lv_intValue_1_0= ruleEInt
                    {

                    						newCompositeNode(grammarAccess.getPrimitiveValueAccess().getIntValueEIntParserRuleCall_0_1_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_intValue_1_0=ruleEInt();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPrimitiveValueRule());
                    						}
                    						set(
                    							current,
                    							"intValue",
                    							lv_intValue_1_0,
                    							"com.dml.dsl.Dml.EInt");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:785:3: ( () ( (lv_floatValue_3_0= ruleEFloat ) ) )
                    {
                    // InternalOperation.g:785:3: ( () ( (lv_floatValue_3_0= ruleEFloat ) ) )
                    // InternalOperation.g:786:4: () ( (lv_floatValue_3_0= ruleEFloat ) )
                    {
                    // InternalOperation.g:786:4: ()
                    // InternalOperation.g:787:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getFloatValueAction_1_0(),
                    						current);
                    				

                    }

                    // InternalOperation.g:793:4: ( (lv_floatValue_3_0= ruleEFloat ) )
                    // InternalOperation.g:794:5: (lv_floatValue_3_0= ruleEFloat )
                    {
                    // InternalOperation.g:794:5: (lv_floatValue_3_0= ruleEFloat )
                    // InternalOperation.g:795:6: lv_floatValue_3_0= ruleEFloat
                    {

                    						newCompositeNode(grammarAccess.getPrimitiveValueAccess().getFloatValueEFloatParserRuleCall_1_1_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_floatValue_3_0=ruleEFloat();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPrimitiveValueRule());
                    						}
                    						set(
                    							current,
                    							"floatValue",
                    							lv_floatValue_3_0,
                    							"com.dml.dsl.Dml.EFloat");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalOperation.g:814:3: ( () ( (lv_stringValue_5_0= RULE_STRING ) ) )
                    {
                    // InternalOperation.g:814:3: ( () ( (lv_stringValue_5_0= RULE_STRING ) ) )
                    // InternalOperation.g:815:4: () ( (lv_stringValue_5_0= RULE_STRING ) )
                    {
                    // InternalOperation.g:815:4: ()
                    // InternalOperation.g:816:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getStringValueAction_2_0(),
                    						current);
                    				

                    }

                    // InternalOperation.g:822:4: ( (lv_stringValue_5_0= RULE_STRING ) )
                    // InternalOperation.g:823:5: (lv_stringValue_5_0= RULE_STRING )
                    {
                    // InternalOperation.g:823:5: (lv_stringValue_5_0= RULE_STRING )
                    // InternalOperation.g:824:6: lv_stringValue_5_0= RULE_STRING
                    {
                    lv_stringValue_5_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

                    						newLeafNode(lv_stringValue_5_0, grammarAccess.getPrimitiveValueAccess().getStringValueSTRINGTerminalRuleCall_2_1_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getPrimitiveValueRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"stringValue",
                    							lv_stringValue_5_0,
                    							"org.eclipse.xtext.common.Terminals.STRING");
                    					

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalOperation.g:842:3: ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) )
                    {
                    // InternalOperation.g:842:3: ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) )
                    // InternalOperation.g:843:4: () ( (lv_boolValue_7_0= ruleEBoolean ) )
                    {
                    // InternalOperation.g:843:4: ()
                    // InternalOperation.g:844:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getBoolValueAction_3_0(),
                    						current);
                    				

                    }

                    // InternalOperation.g:850:4: ( (lv_boolValue_7_0= ruleEBoolean ) )
                    // InternalOperation.g:851:5: (lv_boolValue_7_0= ruleEBoolean )
                    {
                    // InternalOperation.g:851:5: (lv_boolValue_7_0= ruleEBoolean )
                    // InternalOperation.g:852:6: lv_boolValue_7_0= ruleEBoolean
                    {

                    						newCompositeNode(grammarAccess.getPrimitiveValueAccess().getBoolValueEBooleanParserRuleCall_3_1_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_boolValue_7_0=ruleEBoolean();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPrimitiveValueRule());
                    						}
                    						set(
                    							current,
                    							"boolValue",
                    							lv_boolValue_7_0,
                    							"com.dml.dsl.Dml.EBoolean");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }


                    }
                    break;
                case 5 :
                    // InternalOperation.g:871:3: ( () ( (lv_dateValue_9_0= ruleEDate ) ) )
                    {
                    // InternalOperation.g:871:3: ( () ( (lv_dateValue_9_0= ruleEDate ) ) )
                    // InternalOperation.g:872:4: () ( (lv_dateValue_9_0= ruleEDate ) )
                    {
                    // InternalOperation.g:872:4: ()
                    // InternalOperation.g:873:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getDateValueAction_4_0(),
                    						current);
                    				

                    }

                    // InternalOperation.g:879:4: ( (lv_dateValue_9_0= ruleEDate ) )
                    // InternalOperation.g:880:5: (lv_dateValue_9_0= ruleEDate )
                    {
                    // InternalOperation.g:880:5: (lv_dateValue_9_0= ruleEDate )
                    // InternalOperation.g:881:6: lv_dateValue_9_0= ruleEDate
                    {

                    						newCompositeNode(grammarAccess.getPrimitiveValueAccess().getDateValueEDateParserRuleCall_4_1_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_dateValue_9_0=ruleEDate();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPrimitiveValueRule());
                    						}
                    						set(
                    							current,
                    							"dateValue",
                    							lv_dateValue_9_0,
                    							"com.dml.dsl.Dml.EDate");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }


                    }
                    break;
                case 6 :
                    // InternalOperation.g:900:3: this_ArrayValues_10= ruleArrayValues
                    {

                    			newCompositeNode(grammarAccess.getPrimitiveValueAccess().getArrayValuesParserRuleCall_5());
                    		
                    pushFollow(FOLLOW_2);
                    this_ArrayValues_10=ruleArrayValues();

                    state._fsp--;


                    			current = this_ArrayValues_10;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 7 :
                    // InternalOperation.g:909:3: this_AbstractObjectValue_11= ruleAbstractObjectValue
                    {

                    			newCompositeNode(grammarAccess.getPrimitiveValueAccess().getAbstractObjectValueParserRuleCall_6());
                    		
                    pushFollow(FOLLOW_2);
                    this_AbstractObjectValue_11=ruleAbstractObjectValue();

                    state._fsp--;


                    			current = this_AbstractObjectValue_11;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePrimitiveValue"


    // $ANTLR start "entryRuleAbstractObjectValue"
    // InternalOperation.g:921:1: entryRuleAbstractObjectValue returns [EObject current=null] : iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF ;
    public final EObject entryRuleAbstractObjectValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractObjectValue = null;


        try {
            // InternalOperation.g:921:60: (iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF )
            // InternalOperation.g:922:2: iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF
            {
             newCompositeNode(grammarAccess.getAbstractObjectValueRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAbstractObjectValue=ruleAbstractObjectValue();

            state._fsp--;

             current =iv_ruleAbstractObjectValue; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAbstractObjectValue"


    // $ANTLR start "ruleAbstractObjectValue"
    // InternalOperation.g:928:1: ruleAbstractObjectValue returns [EObject current=null] : ( () ( (lv_abstractValue_1_0= RULE_ID ) ) ) ;
    public final EObject ruleAbstractObjectValue() throws RecognitionException {
        EObject current = null;

        Token lv_abstractValue_1_0=null;


        	enterRule();

        try {
            // InternalOperation.g:934:2: ( ( () ( (lv_abstractValue_1_0= RULE_ID ) ) ) )
            // InternalOperation.g:935:2: ( () ( (lv_abstractValue_1_0= RULE_ID ) ) )
            {
            // InternalOperation.g:935:2: ( () ( (lv_abstractValue_1_0= RULE_ID ) ) )
            // InternalOperation.g:936:3: () ( (lv_abstractValue_1_0= RULE_ID ) )
            {
            // InternalOperation.g:936:3: ()
            // InternalOperation.g:937:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getAbstractObjectValueAccess().getAbstractObjectValueAction_0(),
            					current);
            			

            }

            // InternalOperation.g:943:3: ( (lv_abstractValue_1_0= RULE_ID ) )
            // InternalOperation.g:944:4: (lv_abstractValue_1_0= RULE_ID )
            {
            // InternalOperation.g:944:4: (lv_abstractValue_1_0= RULE_ID )
            // InternalOperation.g:945:5: lv_abstractValue_1_0= RULE_ID
            {
            lv_abstractValue_1_0=(Token)match(input,RULE_ID,FOLLOW_2); 

            					newLeafNode(lv_abstractValue_1_0, grammarAccess.getAbstractObjectValueAccess().getAbstractValueIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getAbstractObjectValueRule());
            					}
            					setWithLastConsumed(
            						current,
            						"abstractValue",
            						lv_abstractValue_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAbstractObjectValue"


    // $ANTLR start "entryRuleArrayValues"
    // InternalOperation.g:965:1: entryRuleArrayValues returns [EObject current=null] : iv_ruleArrayValues= ruleArrayValues EOF ;
    public final EObject entryRuleArrayValues() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayValues = null;


        try {
            // InternalOperation.g:965:52: (iv_ruleArrayValues= ruleArrayValues EOF )
            // InternalOperation.g:966:2: iv_ruleArrayValues= ruleArrayValues EOF
            {
             newCompositeNode(grammarAccess.getArrayValuesRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleArrayValues=ruleArrayValues();

            state._fsp--;

             current =iv_ruleArrayValues; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleArrayValues"


    // $ANTLR start "ruleArrayValues"
    // InternalOperation.g:972:1: ruleArrayValues returns [EObject current=null] : ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' ) ;
    public final EObject ruleArrayValues() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_values_2_0 = null;

        EObject lv_values_4_0 = null;



        	enterRule();

        try {
            // InternalOperation.g:978:2: ( ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' ) )
            // InternalOperation.g:979:2: ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' )
            {
            // InternalOperation.g:979:2: ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' )
            // InternalOperation.g:980:3: () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']'
            {
            // InternalOperation.g:980:3: ()
            // InternalOperation.g:981:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getArrayValuesAccess().getArrayValuesAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,24,FOLLOW_20); 

            			newLeafNode(otherlv_1, grammarAccess.getArrayValuesAccess().getLeftSquareBracketKeyword_1());
            		
            // InternalOperation.g:991:3: ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( ((LA18_0>=RULE_ID && LA18_0<=RULE_INT)||LA18_0==22||LA18_0==24||(LA18_0>=26 && LA18_0<=28)) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // InternalOperation.g:992:4: ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )*
                    {
                    // InternalOperation.g:992:4: ( (lv_values_2_0= rulePrimitiveValue ) )
                    // InternalOperation.g:993:5: (lv_values_2_0= rulePrimitiveValue )
                    {
                    // InternalOperation.g:993:5: (lv_values_2_0= rulePrimitiveValue )
                    // InternalOperation.g:994:6: lv_values_2_0= rulePrimitiveValue
                    {

                    						newCompositeNode(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_0_0());
                    					
                    pushFollow(FOLLOW_21);
                    lv_values_2_0=rulePrimitiveValue();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getArrayValuesRule());
                    						}
                    						add(
                    							current,
                    							"values",
                    							lv_values_2_0,
                    							"com.dml.dsl.Dml.PrimitiveValue");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalOperation.g:1011:4: (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==13) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // InternalOperation.g:1012:5: otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) )
                    	    {
                    	    otherlv_3=(Token)match(input,13,FOLLOW_19); 

                    	    					newLeafNode(otherlv_3, grammarAccess.getArrayValuesAccess().getCommaKeyword_2_1_0());
                    	    				
                    	    // InternalOperation.g:1016:5: ( (lv_values_4_0= rulePrimitiveValue ) )
                    	    // InternalOperation.g:1017:6: (lv_values_4_0= rulePrimitiveValue )
                    	    {
                    	    // InternalOperation.g:1017:6: (lv_values_4_0= rulePrimitiveValue )
                    	    // InternalOperation.g:1018:7: lv_values_4_0= rulePrimitiveValue
                    	    {

                    	    							newCompositeNode(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_1_1_0());
                    	    						
                    	    pushFollow(FOLLOW_21);
                    	    lv_values_4_0=rulePrimitiveValue();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getArrayValuesRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"values",
                    	    								lv_values_4_0,
                    	    								"com.dml.dsl.Dml.PrimitiveValue");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,25,FOLLOW_2); 

            			newLeafNode(otherlv_5, grammarAccess.getArrayValuesAccess().getRightSquareBracketKeyword_3());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleArrayValues"


    // $ANTLR start "entryRuleArrayType"
    // InternalOperation.g:1045:1: entryRuleArrayType returns [EObject current=null] : iv_ruleArrayType= ruleArrayType EOF ;
    public final EObject entryRuleArrayType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayType = null;


        try {
            // InternalOperation.g:1045:50: (iv_ruleArrayType= ruleArrayType EOF )
            // InternalOperation.g:1046:2: iv_ruleArrayType= ruleArrayType EOF
            {
             newCompositeNode(grammarAccess.getArrayTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleArrayType=ruleArrayType();

            state._fsp--;

             current =iv_ruleArrayType; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleArrayType"


    // $ANTLR start "ruleArrayType"
    // InternalOperation.g:1052:1: ruleArrayType returns [EObject current=null] : ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? ) ;
    public final EObject ruleArrayType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Enumerator lv_primitiveType_1_0 = null;

        AntlrDatatypeRuleToken lv_name_5_0 = null;

        EObject lv_values_8_0 = null;

        EObject lv_values_10_0 = null;



        	enterRule();

        try {
            // InternalOperation.g:1058:2: ( ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? ) )
            // InternalOperation.g:1059:2: ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? )
            {
            // InternalOperation.g:1059:2: ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? )
            // InternalOperation.g:1060:3: () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )?
            {
            // InternalOperation.g:1060:3: ()
            // InternalOperation.g:1061:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getArrayTypeAccess().getArrayTypeAction_0(),
            					current);
            			

            }

            // InternalOperation.g:1067:3: ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>=31 && LA19_0<=36)) ) {
                alt19=1;
            }
            else if ( (LA19_0==RULE_ID) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // InternalOperation.g:1068:4: ( (lv_primitiveType_1_0= rulePrimitiveValueType ) )
                    {
                    // InternalOperation.g:1068:4: ( (lv_primitiveType_1_0= rulePrimitiveValueType ) )
                    // InternalOperation.g:1069:5: (lv_primitiveType_1_0= rulePrimitiveValueType )
                    {
                    // InternalOperation.g:1069:5: (lv_primitiveType_1_0= rulePrimitiveValueType )
                    // InternalOperation.g:1070:6: lv_primitiveType_1_0= rulePrimitiveValueType
                    {

                    						newCompositeNode(grammarAccess.getArrayTypeAccess().getPrimitiveTypePrimitiveValueTypeEnumRuleCall_1_0_0());
                    					
                    pushFollow(FOLLOW_22);
                    lv_primitiveType_1_0=rulePrimitiveValueType();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getArrayTypeRule());
                    						}
                    						set(
                    							current,
                    							"primitiveType",
                    							lv_primitiveType_1_0,
                    							"com.dml.dsl.Dml.PrimitiveValueType");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:1088:4: ( ( ruleQualifiedName ) )
                    {
                    // InternalOperation.g:1088:4: ( ( ruleQualifiedName ) )
                    // InternalOperation.g:1089:5: ( ruleQualifiedName )
                    {
                    // InternalOperation.g:1089:5: ( ruleQualifiedName )
                    // InternalOperation.g:1090:6: ruleQualifiedName
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getArrayTypeRule());
                    						}
                    					

                    						newCompositeNode(grammarAccess.getArrayTypeAccess().getDataModelTypeDataModelCrossReference_1_1_0());
                    					
                    pushFollow(FOLLOW_22);
                    ruleQualifiedName();

                    state._fsp--;


                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            otherlv_3=(Token)match(input,24,FOLLOW_23); 

            			newLeafNode(otherlv_3, grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_2());
            		
            otherlv_4=(Token)match(input,25,FOLLOW_4); 

            			newLeafNode(otherlv_4, grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_3());
            		
            // InternalOperation.g:1113:3: ( (lv_name_5_0= ruleEString ) )
            // InternalOperation.g:1114:4: (lv_name_5_0= ruleEString )
            {
            // InternalOperation.g:1114:4: (lv_name_5_0= ruleEString )
            // InternalOperation.g:1115:5: lv_name_5_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getArrayTypeAccess().getNameEStringParserRuleCall_4_0());
            				
            pushFollow(FOLLOW_18);
            lv_name_5_0=ruleEString();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getArrayTypeRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_5_0,
            						"com.dml.dsl.Dml.EString");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalOperation.g:1132:3: (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==23) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // InternalOperation.g:1133:4: otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']'
                    {
                    otherlv_6=(Token)match(input,23,FOLLOW_22); 

                    				newLeafNode(otherlv_6, grammarAccess.getArrayTypeAccess().getEqualsSignKeyword_5_0());
                    			
                    otherlv_7=(Token)match(input,24,FOLLOW_20); 

                    				newLeafNode(otherlv_7, grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_5_1());
                    			
                    // InternalOperation.g:1141:4: ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( ((LA21_0>=RULE_ID && LA21_0<=RULE_INT)||LA21_0==22||LA21_0==24||(LA21_0>=26 && LA21_0<=28)) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // InternalOperation.g:1142:5: ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )*
                            {
                            // InternalOperation.g:1142:5: ( (lv_values_8_0= rulePrimitiveValue ) )
                            // InternalOperation.g:1143:6: (lv_values_8_0= rulePrimitiveValue )
                            {
                            // InternalOperation.g:1143:6: (lv_values_8_0= rulePrimitiveValue )
                            // InternalOperation.g:1144:7: lv_values_8_0= rulePrimitiveValue
                            {

                            							newCompositeNode(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_0_0());
                            						
                            pushFollow(FOLLOW_21);
                            lv_values_8_0=rulePrimitiveValue();

                            state._fsp--;


                            							if (current==null) {
                            								current = createModelElementForParent(grammarAccess.getArrayTypeRule());
                            							}
                            							add(
                            								current,
                            								"values",
                            								lv_values_8_0,
                            								"com.dml.dsl.Dml.PrimitiveValue");
                            							afterParserOrEnumRuleCall();
                            						

                            }


                            }

                            // InternalOperation.g:1161:5: (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )*
                            loop20:
                            do {
                                int alt20=2;
                                int LA20_0 = input.LA(1);

                                if ( (LA20_0==13) ) {
                                    alt20=1;
                                }


                                switch (alt20) {
                            	case 1 :
                            	    // InternalOperation.g:1162:6: otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) )
                            	    {
                            	    otherlv_9=(Token)match(input,13,FOLLOW_19); 

                            	    						newLeafNode(otherlv_9, grammarAccess.getArrayTypeAccess().getCommaKeyword_5_2_1_0());
                            	    					
                            	    // InternalOperation.g:1166:6: ( (lv_values_10_0= rulePrimitiveValue ) )
                            	    // InternalOperation.g:1167:7: (lv_values_10_0= rulePrimitiveValue )
                            	    {
                            	    // InternalOperation.g:1167:7: (lv_values_10_0= rulePrimitiveValue )
                            	    // InternalOperation.g:1168:8: lv_values_10_0= rulePrimitiveValue
                            	    {

                            	    								newCompositeNode(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_1_1_0());
                            	    							
                            	    pushFollow(FOLLOW_21);
                            	    lv_values_10_0=rulePrimitiveValue();

                            	    state._fsp--;


                            	    								if (current==null) {
                            	    									current = createModelElementForParent(grammarAccess.getArrayTypeRule());
                            	    								}
                            	    								add(
                            	    									current,
                            	    									"values",
                            	    									lv_values_10_0,
                            	    									"com.dml.dsl.Dml.PrimitiveValue");
                            	    								afterParserOrEnumRuleCall();
                            	    							

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop20;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_11=(Token)match(input,25,FOLLOW_2); 

                    				newLeafNode(otherlv_11, grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_5_3());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleArrayType"


    // $ANTLR start "entryRuleEString"
    // InternalOperation.g:1196:1: entryRuleEString returns [String current=null] : iv_ruleEString= ruleEString EOF ;
    public final String entryRuleEString() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEString = null;


        try {
            // InternalOperation.g:1196:47: (iv_ruleEString= ruleEString EOF )
            // InternalOperation.g:1197:2: iv_ruleEString= ruleEString EOF
            {
             newCompositeNode(grammarAccess.getEStringRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEString=ruleEString();

            state._fsp--;

             current =iv_ruleEString.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEString"


    // $ANTLR start "ruleEString"
    // InternalOperation.g:1203:1: ruleEString returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleEString() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;
        Token this_ID_1=null;


        	enterRule();

        try {
            // InternalOperation.g:1209:2: ( (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID ) )
            // InternalOperation.g:1210:2: (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID )
            {
            // InternalOperation.g:1210:2: (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==RULE_STRING) ) {
                alt23=1;
            }
            else if ( (LA23_0==RULE_ID) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // InternalOperation.g:1211:3: this_STRING_0= RULE_STRING
                    {
                    this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

                    			current.merge(this_STRING_0);
                    		

                    			newLeafNode(this_STRING_0, grammarAccess.getEStringAccess().getSTRINGTerminalRuleCall_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalOperation.g:1219:3: this_ID_1= RULE_ID
                    {
                    this_ID_1=(Token)match(input,RULE_ID,FOLLOW_2); 

                    			current.merge(this_ID_1);
                    		

                    			newLeafNode(this_ID_1, grammarAccess.getEStringAccess().getIDTerminalRuleCall_1());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEString"


    // $ANTLR start "entryRuleEInt"
    // InternalOperation.g:1230:1: entryRuleEInt returns [String current=null] : iv_ruleEInt= ruleEInt EOF ;
    public final String entryRuleEInt() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEInt = null;


        try {
            // InternalOperation.g:1230:44: (iv_ruleEInt= ruleEInt EOF )
            // InternalOperation.g:1231:2: iv_ruleEInt= ruleEInt EOF
            {
             newCompositeNode(grammarAccess.getEIntRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEInt=ruleEInt();

            state._fsp--;

             current =iv_ruleEInt.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEInt"


    // $ANTLR start "ruleEInt"
    // InternalOperation.g:1237:1: ruleEInt returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? this_INT_1= RULE_INT ) ;
    public final AntlrDatatypeRuleToken ruleEInt() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;


        	enterRule();

        try {
            // InternalOperation.g:1243:2: ( ( (kw= '-' )? this_INT_1= RULE_INT ) )
            // InternalOperation.g:1244:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            {
            // InternalOperation.g:1244:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            // InternalOperation.g:1245:3: (kw= '-' )? this_INT_1= RULE_INT
            {
            // InternalOperation.g:1245:3: (kw= '-' )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==26) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // InternalOperation.g:1246:4: kw= '-'
                    {
                    kw=(Token)match(input,26,FOLLOW_24); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getEIntAccess().getHyphenMinusKeyword_0());
                    			

                    }
                    break;

            }

            this_INT_1=(Token)match(input,RULE_INT,FOLLOW_2); 

            			current.merge(this_INT_1);
            		

            			newLeafNode(this_INT_1, grammarAccess.getEIntAccess().getINTTerminalRuleCall_1());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEInt"


    // $ANTLR start "entryRuleEBoolean"
    // InternalOperation.g:1263:1: entryRuleEBoolean returns [String current=null] : iv_ruleEBoolean= ruleEBoolean EOF ;
    public final String entryRuleEBoolean() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEBoolean = null;


        try {
            // InternalOperation.g:1263:48: (iv_ruleEBoolean= ruleEBoolean EOF )
            // InternalOperation.g:1264:2: iv_ruleEBoolean= ruleEBoolean EOF
            {
             newCompositeNode(grammarAccess.getEBooleanRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEBoolean=ruleEBoolean();

            state._fsp--;

             current =iv_ruleEBoolean.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEBoolean"


    // $ANTLR start "ruleEBoolean"
    // InternalOperation.g:1270:1: ruleEBoolean returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'false' | kw= 'true' ) ;
    public final AntlrDatatypeRuleToken ruleEBoolean() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalOperation.g:1276:2: ( (kw= 'false' | kw= 'true' ) )
            // InternalOperation.g:1277:2: (kw= 'false' | kw= 'true' )
            {
            // InternalOperation.g:1277:2: (kw= 'false' | kw= 'true' )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==27) ) {
                alt25=1;
            }
            else if ( (LA25_0==28) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // InternalOperation.g:1278:3: kw= 'false'
                    {
                    kw=(Token)match(input,27,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getEBooleanAccess().getFalseKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalOperation.g:1284:3: kw= 'true'
                    {
                    kw=(Token)match(input,28,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getEBooleanAccess().getTrueKeyword_1());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEBoolean"


    // $ANTLR start "entryRuleEFloat"
    // InternalOperation.g:1293:1: entryRuleEFloat returns [String current=null] : iv_ruleEFloat= ruleEFloat EOF ;
    public final String entryRuleEFloat() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEFloat = null;


        try {
            // InternalOperation.g:1293:46: (iv_ruleEFloat= ruleEFloat EOF )
            // InternalOperation.g:1294:2: iv_ruleEFloat= ruleEFloat EOF
            {
             newCompositeNode(grammarAccess.getEFloatRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEFloat=ruleEFloat();

            state._fsp--;

             current =iv_ruleEFloat.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEFloat"


    // $ANTLR start "ruleEFloat"
    // InternalOperation.g:1300:1: ruleEFloat returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? ) ;
    public final AntlrDatatypeRuleToken ruleEFloat() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;
        Token this_INT_3=null;
        Token this_INT_7=null;


        	enterRule();

        try {
            // InternalOperation.g:1306:2: ( ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? ) )
            // InternalOperation.g:1307:2: ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? )
            {
            // InternalOperation.g:1307:2: ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? )
            // InternalOperation.g:1308:3: (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )?
            {
            // InternalOperation.g:1308:3: (kw= '-' )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==26) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // InternalOperation.g:1309:4: kw= '-'
                    {
                    kw=(Token)match(input,26,FOLLOW_25); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getEFloatAccess().getHyphenMinusKeyword_0());
                    			

                    }
                    break;

            }

            // InternalOperation.g:1315:3: (this_INT_1= RULE_INT )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==RULE_INT) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // InternalOperation.g:1316:4: this_INT_1= RULE_INT
                    {
                    this_INT_1=(Token)match(input,RULE_INT,FOLLOW_26); 

                    				current.merge(this_INT_1);
                    			

                    				newLeafNode(this_INT_1, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_1());
                    			

                    }
                    break;

            }

            kw=(Token)match(input,22,FOLLOW_24); 

            			current.merge(kw);
            			newLeafNode(kw, grammarAccess.getEFloatAccess().getFullStopKeyword_2());
            		
            this_INT_3=(Token)match(input,RULE_INT,FOLLOW_27); 

            			current.merge(this_INT_3);
            		

            			newLeafNode(this_INT_3, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_3());
            		
            // InternalOperation.g:1336:3: ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( ((LA30_0>=29 && LA30_0<=30)) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // InternalOperation.g:1337:4: (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT
                    {
                    // InternalOperation.g:1337:4: (kw= 'E' | kw= 'e' )
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==29) ) {
                        alt28=1;
                    }
                    else if ( (LA28_0==30) ) {
                        alt28=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 28, 0, input);

                        throw nvae;
                    }
                    switch (alt28) {
                        case 1 :
                            // InternalOperation.g:1338:5: kw= 'E'
                            {
                            kw=(Token)match(input,29,FOLLOW_28); 

                            					current.merge(kw);
                            					newLeafNode(kw, grammarAccess.getEFloatAccess().getEKeyword_4_0_0());
                            				

                            }
                            break;
                        case 2 :
                            // InternalOperation.g:1344:5: kw= 'e'
                            {
                            kw=(Token)match(input,30,FOLLOW_28); 

                            					current.merge(kw);
                            					newLeafNode(kw, grammarAccess.getEFloatAccess().getEKeyword_4_0_1());
                            				

                            }
                            break;

                    }

                    // InternalOperation.g:1350:4: (kw= '-' )?
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0==26) ) {
                        alt29=1;
                    }
                    switch (alt29) {
                        case 1 :
                            // InternalOperation.g:1351:5: kw= '-'
                            {
                            kw=(Token)match(input,26,FOLLOW_24); 

                            					current.merge(kw);
                            					newLeafNode(kw, grammarAccess.getEFloatAccess().getHyphenMinusKeyword_4_1());
                            				

                            }
                            break;

                    }

                    this_INT_7=(Token)match(input,RULE_INT,FOLLOW_2); 

                    				current.merge(this_INT_7);
                    			

                    				newLeafNode(this_INT_7, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_4_2());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEFloat"


    // $ANTLR start "entryRuleEDate"
    // InternalOperation.g:1369:1: entryRuleEDate returns [String current=null] : iv_ruleEDate= ruleEDate EOF ;
    public final String entryRuleEDate() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEDate = null;


        try {
            // InternalOperation.g:1369:45: (iv_ruleEDate= ruleEDate EOF )
            // InternalOperation.g:1370:2: iv_ruleEDate= ruleEDate EOF
            {
             newCompositeNode(grammarAccess.getEDateRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEDate=ruleEDate();

            state._fsp--;

             current =iv_ruleEDate.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEDate"


    // $ANTLR start "ruleEDate"
    // InternalOperation.g:1376:1: ruleEDate returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear ) ;
    public final AntlrDatatypeRuleToken ruleEDate() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_Day_0 = null;

        AntlrDatatypeRuleToken this_Month_2 = null;

        AntlrDatatypeRuleToken this_Year_4 = null;



        	enterRule();

        try {
            // InternalOperation.g:1382:2: ( (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear ) )
            // InternalOperation.g:1383:2: (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear )
            {
            // InternalOperation.g:1383:2: (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear )
            // InternalOperation.g:1384:3: this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear
            {

            			newCompositeNode(grammarAccess.getEDateAccess().getDayParserRuleCall_0());
            		
            pushFollow(FOLLOW_29);
            this_Day_0=ruleDay();

            state._fsp--;


            			current.merge(this_Day_0);
            		

            			afterParserOrEnumRuleCall();
            		
            kw=(Token)match(input,26,FOLLOW_24); 

            			current.merge(kw);
            			newLeafNode(kw, grammarAccess.getEDateAccess().getHyphenMinusKeyword_1());
            		

            			newCompositeNode(grammarAccess.getEDateAccess().getMonthParserRuleCall_2());
            		
            pushFollow(FOLLOW_29);
            this_Month_2=ruleMonth();

            state._fsp--;


            			current.merge(this_Month_2);
            		

            			afterParserOrEnumRuleCall();
            		
            kw=(Token)match(input,26,FOLLOW_24); 

            			current.merge(kw);
            			newLeafNode(kw, grammarAccess.getEDateAccess().getHyphenMinusKeyword_3());
            		

            			newCompositeNode(grammarAccess.getEDateAccess().getYearParserRuleCall_4());
            		
            pushFollow(FOLLOW_2);
            this_Year_4=ruleYear();

            state._fsp--;


            			current.merge(this_Year_4);
            		

            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEDate"


    // $ANTLR start "entryRuleDay"
    // InternalOperation.g:1428:1: entryRuleDay returns [String current=null] : iv_ruleDay= ruleDay EOF ;
    public final String entryRuleDay() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDay = null;


        try {
            // InternalOperation.g:1428:43: (iv_ruleDay= ruleDay EOF )
            // InternalOperation.g:1429:2: iv_ruleDay= ruleDay EOF
            {
             newCompositeNode(grammarAccess.getDayRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDay=ruleDay();

            state._fsp--;

             current =iv_ruleDay.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDay"


    // $ANTLR start "ruleDay"
    // InternalOperation.g:1435:1: ruleDay returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleDay() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalOperation.g:1441:2: (this_INT_0= RULE_INT )
            // InternalOperation.g:1442:2: this_INT_0= RULE_INT
            {
            this_INT_0=(Token)match(input,RULE_INT,FOLLOW_2); 

            		current.merge(this_INT_0);
            	

            		newLeafNode(this_INT_0, grammarAccess.getDayAccess().getINTTerminalRuleCall());
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDay"


    // $ANTLR start "entryRuleMonth"
    // InternalOperation.g:1452:1: entryRuleMonth returns [String current=null] : iv_ruleMonth= ruleMonth EOF ;
    public final String entryRuleMonth() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleMonth = null;


        try {
            // InternalOperation.g:1452:45: (iv_ruleMonth= ruleMonth EOF )
            // InternalOperation.g:1453:2: iv_ruleMonth= ruleMonth EOF
            {
             newCompositeNode(grammarAccess.getMonthRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleMonth=ruleMonth();

            state._fsp--;

             current =iv_ruleMonth.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleMonth"


    // $ANTLR start "ruleMonth"
    // InternalOperation.g:1459:1: ruleMonth returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleMonth() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalOperation.g:1465:2: (this_INT_0= RULE_INT )
            // InternalOperation.g:1466:2: this_INT_0= RULE_INT
            {
            this_INT_0=(Token)match(input,RULE_INT,FOLLOW_2); 

            		current.merge(this_INT_0);
            	

            		newLeafNode(this_INT_0, grammarAccess.getMonthAccess().getINTTerminalRuleCall());
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleMonth"


    // $ANTLR start "entryRuleYear"
    // InternalOperation.g:1476:1: entryRuleYear returns [String current=null] : iv_ruleYear= ruleYear EOF ;
    public final String entryRuleYear() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleYear = null;


        try {
            // InternalOperation.g:1476:44: (iv_ruleYear= ruleYear EOF )
            // InternalOperation.g:1477:2: iv_ruleYear= ruleYear EOF
            {
             newCompositeNode(grammarAccess.getYearRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleYear=ruleYear();

            state._fsp--;

             current =iv_ruleYear.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleYear"


    // $ANTLR start "ruleYear"
    // InternalOperation.g:1483:1: ruleYear returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleYear() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalOperation.g:1489:2: (this_INT_0= RULE_INT )
            // InternalOperation.g:1490:2: this_INT_0= RULE_INT
            {
            this_INT_0=(Token)match(input,RULE_INT,FOLLOW_2); 

            		current.merge(this_INT_0);
            	

            		newLeafNode(this_INT_0, grammarAccess.getYearAccess().getINTTerminalRuleCall());
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleYear"


    // $ANTLR start "rulePrimitiveValueType"
    // InternalOperation.g:1500:1: rulePrimitiveValueType returns [Enumerator current=null] : ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) ) ;
    public final Enumerator rulePrimitiveValueType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;
        Token enumLiteral_4=null;
        Token enumLiteral_5=null;


        	enterRule();

        try {
            // InternalOperation.g:1506:2: ( ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) ) )
            // InternalOperation.g:1507:2: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) )
            {
            // InternalOperation.g:1507:2: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) )
            int alt31=6;
            switch ( input.LA(1) ) {
            case 31:
                {
                alt31=1;
                }
                break;
            case 32:
                {
                alt31=2;
                }
                break;
            case 33:
                {
                alt31=3;
                }
                break;
            case 34:
                {
                alt31=4;
                }
                break;
            case 35:
                {
                alt31=5;
                }
                break;
            case 36:
                {
                alt31=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // InternalOperation.g:1508:3: (enumLiteral_0= 'int' )
                    {
                    // InternalOperation.g:1508:3: (enumLiteral_0= 'int' )
                    // InternalOperation.g:1509:4: enumLiteral_0= 'int'
                    {
                    enumLiteral_0=(Token)match(input,31,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:1516:3: (enumLiteral_1= 'boolean' )
                    {
                    // InternalOperation.g:1516:3: (enumLiteral_1= 'boolean' )
                    // InternalOperation.g:1517:4: enumLiteral_1= 'boolean'
                    {
                    enumLiteral_1=(Token)match(input,32,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalOperation.g:1524:3: (enumLiteral_2= 'float' )
                    {
                    // InternalOperation.g:1524:3: (enumLiteral_2= 'float' )
                    // InternalOperation.g:1525:4: enumLiteral_2= 'float'
                    {
                    enumLiteral_2=(Token)match(input,33,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;
                case 4 :
                    // InternalOperation.g:1532:3: (enumLiteral_3= 'string' )
                    {
                    // InternalOperation.g:1532:3: (enumLiteral_3= 'string' )
                    // InternalOperation.g:1533:4: enumLiteral_3= 'string'
                    {
                    enumLiteral_3=(Token)match(input,34,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_3, grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3());
                    			

                    }


                    }
                    break;
                case 5 :
                    // InternalOperation.g:1540:3: (enumLiteral_4= 'object' )
                    {
                    // InternalOperation.g:1540:3: (enumLiteral_4= 'object' )
                    // InternalOperation.g:1541:4: enumLiteral_4= 'object'
                    {
                    enumLiteral_4=(Token)match(input,35,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_4, grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4());
                    			

                    }


                    }
                    break;
                case 6 :
                    // InternalOperation.g:1548:3: (enumLiteral_5= 'date' )
                    {
                    // InternalOperation.g:1548:3: (enumLiteral_5= 'date' )
                    // InternalOperation.g:1549:4: enumLiteral_5= 'date'
                    {
                    enumLiteral_5=(Token)match(input,36,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getDateEnumLiteralDeclaration_5().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_5, grammarAccess.getPrimitiveValueTypeAccess().getDateEnumLiteralDeclaration_5());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePrimitiveValueType"

    // Delegated rules


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA16 dfa16 = new DFA16(this);
    static final String dfa_1s = "\15\uffff";
    static final String dfa_2s = "\10\4\2\uffff\1\4\1\uffff\1\4";
    static final String dfa_3s = "\1\44\7\30\2\uffff\1\4\1\uffff\1\30";
    static final String dfa_4s = "\10\uffff\1\3\1\1\1\uffff\1\2\1\uffff";
    static final String dfa_5s = "\15\uffff}>";
    static final String[] dfa_6s = {
            "\1\7\32\uffff\1\1\1\2\1\3\1\4\1\5\1\6",
            "\2\11\22\uffff\1\10",
            "\2\11\22\uffff\1\10",
            "\2\11\22\uffff\1\10",
            "\2\11\22\uffff\1\10",
            "\2\11\22\uffff\1\10",
            "\2\11\22\uffff\1\10",
            "\2\13\20\uffff\1\12\1\uffff\1\10",
            "",
            "",
            "\1\14",
            "",
            "\2\13\20\uffff\1\12\1\uffff\1\10"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "495:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )";
        }
    }
    static final String dfa_7s = "\13\uffff";
    static final String dfa_8s = "\2\uffff\1\11\5\uffff\1\11\2\uffff";
    static final String dfa_9s = "\1\4\1\6\1\15\5\uffff\1\15\2\uffff";
    static final String dfa_10s = "\1\34\1\26\1\32\5\uffff\1\31\2\uffff";
    static final String dfa_11s = "\3\uffff\1\2\1\3\1\4\1\6\1\7\1\uffff\1\1\1\5";
    static final String dfa_12s = "\13\uffff}>";
    static final String[] dfa_13s = {
            "\1\7\1\4\1\2\17\uffff\1\3\1\uffff\1\6\1\uffff\1\1\2\5",
            "\1\10\17\uffff\1\3",
            "\2\11\3\uffff\1\11\3\uffff\1\3\2\uffff\1\11\1\12",
            "",
            "",
            "",
            "",
            "",
            "\2\11\3\uffff\1\11\3\uffff\1\3\2\uffff\1\11",
            "",
            ""
    };

    static final short[] dfa_7 = DFA.unpackEncodedString(dfa_7s);
    static final short[] dfa_8 = DFA.unpackEncodedString(dfa_8s);
    static final char[] dfa_9 = DFA.unpackEncodedStringToUnsignedChars(dfa_9s);
    static final char[] dfa_10 = DFA.unpackEncodedStringToUnsignedChars(dfa_10s);
    static final short[] dfa_11 = DFA.unpackEncodedString(dfa_11s);
    static final short[] dfa_12 = DFA.unpackEncodedString(dfa_12s);
    static final short[][] dfa_13 = unpackEncodedStringArray(dfa_13s);

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_9;
            this.max = dfa_10;
            this.accept = dfa_11;
            this.special = dfa_12;
            this.transition = dfa_13;
        }
        public String getDescription() {
            return "755:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )";
        }
    }
 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000001F80004010L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000001F80000010L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000070000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x00000000003C0002L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000042000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x00000000002C0002L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x000000001D400070L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x000000001F400070L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000002002000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000000400040L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000060000002L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000000004000040L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000000004000000L});

}
