package com.dml.dsl.parser.antlr.internal;

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
import com.dml.dsl.services.DmlGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalDmlParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'Package'", "'DataModel'", "'{'", "'primitives'", "','", "'}'", "'composites'", "'.'", "'='", "'['", "']'", "'-'", "'false'", "'true'", "'E'", "'e'", "'int'", "'boolean'", "'float'", "'string'", "'object'", "'date'"
    };
    public static final int RULE_STRING=5;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__11=11;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
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


        public InternalDmlParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalDmlParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalDmlParser.tokenNames; }
    public String getGrammarFileName() { return "InternalDml.g"; }



     	private DmlGrammarAccess grammarAccess;

        public InternalDmlParser(TokenStream input, DmlGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "DataPackage";
       	}

       	@Override
       	protected DmlGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleDataPackage"
    // InternalDml.g:65:1: entryRuleDataPackage returns [EObject current=null] : iv_ruleDataPackage= ruleDataPackage EOF ;
    public final EObject entryRuleDataPackage() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataPackage = null;


        try {
            // InternalDml.g:65:52: (iv_ruleDataPackage= ruleDataPackage EOF )
            // InternalDml.g:66:2: iv_ruleDataPackage= ruleDataPackage EOF
            {
             newCompositeNode(grammarAccess.getDataPackageRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDataPackage=ruleDataPackage();

            state._fsp--;

             current =iv_ruleDataPackage; 
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
    // $ANTLR end "entryRuleDataPackage"


    // $ANTLR start "ruleDataPackage"
    // InternalDml.g:72:1: ruleDataPackage returns [EObject current=null] : ( (otherlv_0= 'Package' ( (lv_name_1_0= ruleEString ) ) )? ( (lv_dataModelCollections_2_0= ruleDataModel ) )* ) ;
    public final EObject ruleDataPackage() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_dataModelCollections_2_0 = null;



        	enterRule();

        try {
            // InternalDml.g:78:2: ( ( (otherlv_0= 'Package' ( (lv_name_1_0= ruleEString ) ) )? ( (lv_dataModelCollections_2_0= ruleDataModel ) )* ) )
            // InternalDml.g:79:2: ( (otherlv_0= 'Package' ( (lv_name_1_0= ruleEString ) ) )? ( (lv_dataModelCollections_2_0= ruleDataModel ) )* )
            {
            // InternalDml.g:79:2: ( (otherlv_0= 'Package' ( (lv_name_1_0= ruleEString ) ) )? ( (lv_dataModelCollections_2_0= ruleDataModel ) )* )
            // InternalDml.g:80:3: (otherlv_0= 'Package' ( (lv_name_1_0= ruleEString ) ) )? ( (lv_dataModelCollections_2_0= ruleDataModel ) )*
            {
            // InternalDml.g:80:3: (otherlv_0= 'Package' ( (lv_name_1_0= ruleEString ) ) )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==11) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // InternalDml.g:81:4: otherlv_0= 'Package' ( (lv_name_1_0= ruleEString ) )
                    {
                    otherlv_0=(Token)match(input,11,FOLLOW_3); 

                    				newLeafNode(otherlv_0, grammarAccess.getDataPackageAccess().getPackageKeyword_0_0());
                    			
                    // InternalDml.g:85:4: ( (lv_name_1_0= ruleEString ) )
                    // InternalDml.g:86:5: (lv_name_1_0= ruleEString )
                    {
                    // InternalDml.g:86:5: (lv_name_1_0= ruleEString )
                    // InternalDml.g:87:6: lv_name_1_0= ruleEString
                    {

                    						newCompositeNode(grammarAccess.getDataPackageAccess().getNameEStringParserRuleCall_0_1_0());
                    					
                    pushFollow(FOLLOW_4);
                    lv_name_1_0=ruleEString();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getDataPackageRule());
                    						}
                    						set(
                    							current,
                    							"name",
                    							lv_name_1_0,
                    							"com.dml.dsl.Dml.EString");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            // InternalDml.g:105:3: ( (lv_dataModelCollections_2_0= ruleDataModel ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==12||(LA2_0>=16 && LA2_0<=17)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // InternalDml.g:106:4: (lv_dataModelCollections_2_0= ruleDataModel )
            	    {
            	    // InternalDml.g:106:4: (lv_dataModelCollections_2_0= ruleDataModel )
            	    // InternalDml.g:107:5: lv_dataModelCollections_2_0= ruleDataModel
            	    {

            	    					newCompositeNode(grammarAccess.getDataPackageAccess().getDataModelCollectionsDataModelParserRuleCall_1_0());
            	    				
            	    pushFollow(FOLLOW_4);
            	    lv_dataModelCollections_2_0=ruleDataModel();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getDataPackageRule());
            	    					}
            	    					add(
            	    						current,
            	    						"dataModelCollections",
            	    						lv_dataModelCollections_2_0,
            	    						"com.dml.dsl.Dml.DataModel");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
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
    // $ANTLR end "ruleDataPackage"


    // $ANTLR start "entryRuleDataModel"
    // InternalDml.g:128:1: entryRuleDataModel returns [EObject current=null] : iv_ruleDataModel= ruleDataModel EOF ;
    public final EObject entryRuleDataModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataModel = null;


        try {
            // InternalDml.g:128:50: (iv_ruleDataModel= ruleDataModel EOF )
            // InternalDml.g:129:2: iv_ruleDataModel= ruleDataModel EOF
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
    // InternalDml.g:135:1: ruleDataModel returns [EObject current=null] : ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) ) ;
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
            // InternalDml.g:141:2: ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) ) )
            // InternalDml.g:142:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) )
            {
            // InternalDml.g:142:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) )
            // InternalDml.g:143:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) )
            {
            // InternalDml.g:143:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) )
            // InternalDml.g:144:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?)
            {
             
            			  getUnorderedGroupHelper().enter(grammarAccess.getDataModelAccess().getUnorderedGroup());
            			
            // InternalDml.g:147:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?)
            // InternalDml.g:148:5: ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?
            {
            // InternalDml.g:148:5: ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+
            int cnt7=0;
            loop7:
            do {
                int alt7=3;
                switch ( input.LA(1) ) {
                case 12:
                    {
                    int LA7_2 = input.LA(2);

                    if ( getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
                        alt7=1;
                    }


                    }
                    break;
                case 17:
                    {
                    int LA7_3 = input.LA(2);

                    if ( getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
                        alt7=2;
                    }


                    }
                    break;
                case 16:
                    {
                    int LA7_4 = input.LA(2);

                    if ( getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
                        alt7=2;
                    }


                    }
                    break;

                }

                switch (alt7) {
            	case 1 :
            	    // InternalDml.g:149:3: ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) )
            	    {
            	    // InternalDml.g:149:3: ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) )
            	    // InternalDml.g:150:4: {...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0)");
            	    }
            	    // InternalDml.g:150:103: ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) )
            	    // InternalDml.g:151:5: ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0);
            	    				
            	    // InternalDml.g:154:8: ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) )
            	    // InternalDml.g:154:9: {...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "true");
            	    }
            	    // InternalDml.g:154:18: (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? )
            	    // InternalDml.g:154:19: otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )?
            	    {
            	    otherlv_1=(Token)match(input,12,FOLLOW_3); 

            	    								newLeafNode(otherlv_1, grammarAccess.getDataModelAccess().getDataModelKeyword_0_0());
            	    							
            	    // InternalDml.g:158:8: ( (lv_name_2_0= ruleEString ) )
            	    // InternalDml.g:159:9: (lv_name_2_0= ruleEString )
            	    {
            	    // InternalDml.g:159:9: (lv_name_2_0= ruleEString )
            	    // InternalDml.g:160:10: lv_name_2_0= ruleEString
            	    {

            	    										newCompositeNode(grammarAccess.getDataModelAccess().getNameEStringParserRuleCall_0_1_0());
            	    									
            	    pushFollow(FOLLOW_5);
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

            	    otherlv_3=(Token)match(input,13,FOLLOW_6); 

            	    								newLeafNode(otherlv_3, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_2());
            	    							
            	    // InternalDml.g:181:8: (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )?
            	    int alt4=2;
            	    int LA4_0 = input.LA(1);

            	    if ( (LA4_0==14) ) {
            	        alt4=1;
            	    }
            	    switch (alt4) {
            	        case 1 :
            	            // InternalDml.g:182:9: otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}'
            	            {
            	            otherlv_4=(Token)match(input,14,FOLLOW_5); 

            	            									newLeafNode(otherlv_4, grammarAccess.getDataModelAccess().getPrimitivesKeyword_0_3_0());
            	            								
            	            otherlv_5=(Token)match(input,13,FOLLOW_7); 

            	            									newLeafNode(otherlv_5, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_3_1());
            	            								
            	            // InternalDml.g:190:9: ( (lv_primitives_6_0= ruleParameter ) )
            	            // InternalDml.g:191:10: (lv_primitives_6_0= ruleParameter )
            	            {
            	            // InternalDml.g:191:10: (lv_primitives_6_0= ruleParameter )
            	            // InternalDml.g:192:11: lv_primitives_6_0= ruleParameter
            	            {

            	            											newCompositeNode(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_2_0());
            	            										
            	            pushFollow(FOLLOW_8);
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

            	            // InternalDml.g:209:9: (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )*
            	            loop3:
            	            do {
            	                int alt3=2;
            	                int LA3_0 = input.LA(1);

            	                if ( (LA3_0==15) ) {
            	                    alt3=1;
            	                }


            	                switch (alt3) {
            	            	case 1 :
            	            	    // InternalDml.g:210:10: otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) )
            	            	    {
            	            	    otherlv_7=(Token)match(input,15,FOLLOW_7); 

            	            	    										newLeafNode(otherlv_7, grammarAccess.getDataModelAccess().getCommaKeyword_0_3_3_0());
            	            	    									
            	            	    // InternalDml.g:214:10: ( (lv_primitives_8_0= ruleParameter ) )
            	            	    // InternalDml.g:215:11: (lv_primitives_8_0= ruleParameter )
            	            	    {
            	            	    // InternalDml.g:215:11: (lv_primitives_8_0= ruleParameter )
            	            	    // InternalDml.g:216:12: lv_primitives_8_0= ruleParameter
            	            	    {

            	            	    												newCompositeNode(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_3_1_0());
            	            	    											
            	            	    pushFollow(FOLLOW_8);
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
            	            	    break loop3;
            	                }
            	            } while (true);

            	            otherlv_9=(Token)match(input,16,FOLLOW_4); 

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
            	    // InternalDml.g:245:3: ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) )
            	    {
            	    // InternalDml.g:245:3: ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) )
            	    // InternalDml.g:246:4: {...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1)");
            	    }
            	    // InternalDml.g:246:103: ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) )
            	    // InternalDml.g:247:5: ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1);
            	    				
            	    // InternalDml.g:250:8: ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) )
            	    // InternalDml.g:250:9: {...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "true");
            	    }
            	    // InternalDml.g:250:18: ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' )
            	    // InternalDml.g:250:19: (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}'
            	    {
            	    // InternalDml.g:250:19: (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )?
            	    int alt6=2;
            	    int LA6_0 = input.LA(1);

            	    if ( (LA6_0==17) ) {
            	        alt6=1;
            	    }
            	    switch (alt6) {
            	        case 1 :
            	            // InternalDml.g:251:9: otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}'
            	            {
            	            otherlv_10=(Token)match(input,17,FOLLOW_5); 

            	            									newLeafNode(otherlv_10, grammarAccess.getDataModelAccess().getCompositesKeyword_1_0_0());
            	            								
            	            otherlv_11=(Token)match(input,13,FOLLOW_9); 

            	            									newLeafNode(otherlv_11, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_1_0_1());
            	            								
            	            // InternalDml.g:259:9: ( (otherlv_12= RULE_ID ) )
            	            // InternalDml.g:260:10: (otherlv_12= RULE_ID )
            	            {
            	            // InternalDml.g:260:10: (otherlv_12= RULE_ID )
            	            // InternalDml.g:261:11: otherlv_12= RULE_ID
            	            {

            	            											if (current==null) {
            	            												current = createModelElement(grammarAccess.getDataModelRule());
            	            											}
            	            										
            	            otherlv_12=(Token)match(input,RULE_ID,FOLLOW_8); 

            	            											newLeafNode(otherlv_12, grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_2_0());
            	            										

            	            }


            	            }

            	            // InternalDml.g:272:9: (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )*
            	            loop5:
            	            do {
            	                int alt5=2;
            	                int LA5_0 = input.LA(1);

            	                if ( (LA5_0==15) ) {
            	                    alt5=1;
            	                }


            	                switch (alt5) {
            	            	case 1 :
            	            	    // InternalDml.g:273:10: otherlv_13= ',' ( (otherlv_14= RULE_ID ) )
            	            	    {
            	            	    otherlv_13=(Token)match(input,15,FOLLOW_9); 

            	            	    										newLeafNode(otherlv_13, grammarAccess.getDataModelAccess().getCommaKeyword_1_0_3_0());
            	            	    									
            	            	    // InternalDml.g:277:10: ( (otherlv_14= RULE_ID ) )
            	            	    // InternalDml.g:278:11: (otherlv_14= RULE_ID )
            	            	    {
            	            	    // InternalDml.g:278:11: (otherlv_14= RULE_ID )
            	            	    // InternalDml.g:279:12: otherlv_14= RULE_ID
            	            	    {

            	            	    												if (current==null) {
            	            	    													current = createModelElement(grammarAccess.getDataModelRule());
            	            	    												}
            	            	    											
            	            	    otherlv_14=(Token)match(input,RULE_ID,FOLLOW_8); 

            	            	    												newLeafNode(otherlv_14, grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_3_1_0());
            	            	    											

            	            	    }


            	            	    }


            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop5;
            	                }
            	            } while (true);

            	            otherlv_15=(Token)match(input,16,FOLLOW_10); 

            	            									newLeafNode(otherlv_15, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_0_4());
            	            								

            	            }
            	            break;

            	    }

            	    otherlv_16=(Token)match(input,16,FOLLOW_4); 

            	    								newLeafNode(otherlv_16, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_1());
            	    							

            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getDataModelAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
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
    // InternalDml.g:317:1: entryRuleParameter returns [EObject current=null] : iv_ruleParameter= ruleParameter EOF ;
    public final EObject entryRuleParameter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParameter = null;


        try {
            // InternalDml.g:317:50: (iv_ruleParameter= ruleParameter EOF )
            // InternalDml.g:318:2: iv_ruleParameter= ruleParameter EOF
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
    // InternalDml.g:324:1: ruleParameter returns [EObject current=null] : (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType ) ;
    public final EObject ruleParameter() throws RecognitionException {
        EObject current = null;

        EObject this_SimpleType_0 = null;

        EObject this_AbstractType_1 = null;

        EObject this_ArrayType_2 = null;



        	enterRule();

        try {
            // InternalDml.g:330:2: ( (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType ) )
            // InternalDml.g:331:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )
            {
            // InternalDml.g:331:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )
            int alt8=3;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // InternalDml.g:332:3: this_SimpleType_0= ruleSimpleType
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
                    // InternalDml.g:341:3: this_AbstractType_1= ruleAbstractType
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
                    // InternalDml.g:350:3: this_ArrayType_2= ruleArrayType
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
    // InternalDml.g:362:1: entryRuleQualifiedName returns [String current=null] : iv_ruleQualifiedName= ruleQualifiedName EOF ;
    public final String entryRuleQualifiedName() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQualifiedName = null;


        try {
            // InternalDml.g:362:53: (iv_ruleQualifiedName= ruleQualifiedName EOF )
            // InternalDml.g:363:2: iv_ruleQualifiedName= ruleQualifiedName EOF
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
    // InternalDml.g:369:1: ruleQualifiedName returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQualifiedName() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;


        	enterRule();

        try {
            // InternalDml.g:375:2: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // InternalDml.g:376:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // InternalDml.g:376:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // InternalDml.g:377:3: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_11); 

            			current.merge(this_ID_0);
            		

            			newLeafNode(this_ID_0, grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_0());
            		
            // InternalDml.g:384:3: (kw= '.' this_ID_2= RULE_ID )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==18) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // InternalDml.g:385:4: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,18,FOLLOW_9); 

            	    				current.merge(kw);
            	    				newLeafNode(kw, grammarAccess.getQualifiedNameAccess().getFullStopKeyword_1_0());
            	    			
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_11); 

            	    				current.merge(this_ID_2);
            	    			

            	    				newLeafNode(this_ID_2, grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_1_1());
            	    			

            	    }
            	    break;

            	default :
            	    break loop9;
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
    // InternalDml.g:402:1: entryRuleSimpleType returns [EObject current=null] : iv_ruleSimpleType= ruleSimpleType EOF ;
    public final EObject entryRuleSimpleType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleType = null;


        try {
            // InternalDml.g:402:51: (iv_ruleSimpleType= ruleSimpleType EOF )
            // InternalDml.g:403:2: iv_ruleSimpleType= ruleSimpleType EOF
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
    // InternalDml.g:409:1: ruleSimpleType returns [EObject current=null] : ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? ) ;
    public final EObject ruleSimpleType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        Enumerator lv_type_1_0 = null;

        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalDml.g:415:2: ( ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? ) )
            // InternalDml.g:416:2: ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? )
            {
            // InternalDml.g:416:2: ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? )
            // InternalDml.g:417:3: () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )?
            {
            // InternalDml.g:417:3: ()
            // InternalDml.g:418:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getSimpleTypeAccess().getSimpleTypeAction_0(),
            					current);
            			

            }

            // InternalDml.g:424:3: ( (lv_type_1_0= rulePrimitiveValueType ) )
            // InternalDml.g:425:4: (lv_type_1_0= rulePrimitiveValueType )
            {
            // InternalDml.g:425:4: (lv_type_1_0= rulePrimitiveValueType )
            // InternalDml.g:426:5: lv_type_1_0= rulePrimitiveValueType
            {

            					newCompositeNode(grammarAccess.getSimpleTypeAccess().getTypePrimitiveValueTypeEnumRuleCall_1_0());
            				
            pushFollow(FOLLOW_3);
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

            // InternalDml.g:443:3: ( (lv_name_2_0= ruleEString ) )
            // InternalDml.g:444:4: (lv_name_2_0= ruleEString )
            {
            // InternalDml.g:444:4: (lv_name_2_0= ruleEString )
            // InternalDml.g:445:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getSimpleTypeAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_12);
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

            // InternalDml.g:462:3: (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==19) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // InternalDml.g:463:4: otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) )
                    {
                    otherlv_3=(Token)match(input,19,FOLLOW_13); 

                    				newLeafNode(otherlv_3, grammarAccess.getSimpleTypeAccess().getEqualsSignKeyword_3_0());
                    			
                    // InternalDml.g:467:4: ( (lv_value_4_0= rulePrimitiveValue ) )
                    // InternalDml.g:468:5: (lv_value_4_0= rulePrimitiveValue )
                    {
                    // InternalDml.g:468:5: (lv_value_4_0= rulePrimitiveValue )
                    // InternalDml.g:469:6: lv_value_4_0= rulePrimitiveValue
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
    // InternalDml.g:491:1: entryRuleAbstractType returns [EObject current=null] : iv_ruleAbstractType= ruleAbstractType EOF ;
    public final EObject entryRuleAbstractType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractType = null;


        try {
            // InternalDml.g:491:53: (iv_ruleAbstractType= ruleAbstractType EOF )
            // InternalDml.g:492:2: iv_ruleAbstractType= ruleAbstractType EOF
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
    // InternalDml.g:498:1: ruleAbstractType returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? ) ;
    public final EObject ruleAbstractType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalDml.g:504:2: ( ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? ) )
            // InternalDml.g:505:2: ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? )
            {
            // InternalDml.g:505:2: ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? )
            // InternalDml.g:506:3: () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )?
            {
            // InternalDml.g:506:3: ()
            // InternalDml.g:507:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getAbstractTypeAccess().getAbstractTypeAction_0(),
            					current);
            			

            }

            // InternalDml.g:513:3: ( ( ruleQualifiedName ) )
            // InternalDml.g:514:4: ( ruleQualifiedName )
            {
            // InternalDml.g:514:4: ( ruleQualifiedName )
            // InternalDml.g:515:5: ruleQualifiedName
            {

            					if (current==null) {
            						current = createModelElement(grammarAccess.getAbstractTypeRule());
            					}
            				

            					newCompositeNode(grammarAccess.getAbstractTypeAccess().getTypeDataModelCrossReference_1_0());
            				
            pushFollow(FOLLOW_3);
            ruleQualifiedName();

            state._fsp--;


            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalDml.g:529:3: ( (lv_name_2_0= ruleEString ) )
            // InternalDml.g:530:4: (lv_name_2_0= ruleEString )
            {
            // InternalDml.g:530:4: (lv_name_2_0= ruleEString )
            // InternalDml.g:531:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getAbstractTypeAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_12);
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

            // InternalDml.g:548:3: (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==19) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalDml.g:549:4: otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) )
                    {
                    otherlv_3=(Token)match(input,19,FOLLOW_13); 

                    				newLeafNode(otherlv_3, grammarAccess.getAbstractTypeAccess().getEqualsSignKeyword_3_0());
                    			
                    // InternalDml.g:553:4: ( (lv_value_4_0= ruleAbstractObjectValue ) )
                    // InternalDml.g:554:5: (lv_value_4_0= ruleAbstractObjectValue )
                    {
                    // InternalDml.g:554:5: (lv_value_4_0= ruleAbstractObjectValue )
                    // InternalDml.g:555:6: lv_value_4_0= ruleAbstractObjectValue
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
    // InternalDml.g:577:1: entryRulePrimitiveValue returns [EObject current=null] : iv_rulePrimitiveValue= rulePrimitiveValue EOF ;
    public final EObject entryRulePrimitiveValue() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimitiveValue = null;


        try {
            // InternalDml.g:577:55: (iv_rulePrimitiveValue= rulePrimitiveValue EOF )
            // InternalDml.g:578:2: iv_rulePrimitiveValue= rulePrimitiveValue EOF
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
    // InternalDml.g:584:1: rulePrimitiveValue returns [EObject current=null] : ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue ) ;
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
            // InternalDml.g:590:2: ( ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue ) )
            // InternalDml.g:591:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )
            {
            // InternalDml.g:591:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )
            int alt12=7;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // InternalDml.g:592:3: ( () ( (lv_intValue_1_0= ruleEInt ) ) )
                    {
                    // InternalDml.g:592:3: ( () ( (lv_intValue_1_0= ruleEInt ) ) )
                    // InternalDml.g:593:4: () ( (lv_intValue_1_0= ruleEInt ) )
                    {
                    // InternalDml.g:593:4: ()
                    // InternalDml.g:594:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getIntValueAction_0_0(),
                    						current);
                    				

                    }

                    // InternalDml.g:600:4: ( (lv_intValue_1_0= ruleEInt ) )
                    // InternalDml.g:601:5: (lv_intValue_1_0= ruleEInt )
                    {
                    // InternalDml.g:601:5: (lv_intValue_1_0= ruleEInt )
                    // InternalDml.g:602:6: lv_intValue_1_0= ruleEInt
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
                    // InternalDml.g:621:3: ( () ( (lv_floatValue_3_0= ruleEFloat ) ) )
                    {
                    // InternalDml.g:621:3: ( () ( (lv_floatValue_3_0= ruleEFloat ) ) )
                    // InternalDml.g:622:4: () ( (lv_floatValue_3_0= ruleEFloat ) )
                    {
                    // InternalDml.g:622:4: ()
                    // InternalDml.g:623:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getFloatValueAction_1_0(),
                    						current);
                    				

                    }

                    // InternalDml.g:629:4: ( (lv_floatValue_3_0= ruleEFloat ) )
                    // InternalDml.g:630:5: (lv_floatValue_3_0= ruleEFloat )
                    {
                    // InternalDml.g:630:5: (lv_floatValue_3_0= ruleEFloat )
                    // InternalDml.g:631:6: lv_floatValue_3_0= ruleEFloat
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
                    // InternalDml.g:650:3: ( () ( (lv_stringValue_5_0= RULE_STRING ) ) )
                    {
                    // InternalDml.g:650:3: ( () ( (lv_stringValue_5_0= RULE_STRING ) ) )
                    // InternalDml.g:651:4: () ( (lv_stringValue_5_0= RULE_STRING ) )
                    {
                    // InternalDml.g:651:4: ()
                    // InternalDml.g:652:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getStringValueAction_2_0(),
                    						current);
                    				

                    }

                    // InternalDml.g:658:4: ( (lv_stringValue_5_0= RULE_STRING ) )
                    // InternalDml.g:659:5: (lv_stringValue_5_0= RULE_STRING )
                    {
                    // InternalDml.g:659:5: (lv_stringValue_5_0= RULE_STRING )
                    // InternalDml.g:660:6: lv_stringValue_5_0= RULE_STRING
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
                    // InternalDml.g:678:3: ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) )
                    {
                    // InternalDml.g:678:3: ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) )
                    // InternalDml.g:679:4: () ( (lv_boolValue_7_0= ruleEBoolean ) )
                    {
                    // InternalDml.g:679:4: ()
                    // InternalDml.g:680:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getBoolValueAction_3_0(),
                    						current);
                    				

                    }

                    // InternalDml.g:686:4: ( (lv_boolValue_7_0= ruleEBoolean ) )
                    // InternalDml.g:687:5: (lv_boolValue_7_0= ruleEBoolean )
                    {
                    // InternalDml.g:687:5: (lv_boolValue_7_0= ruleEBoolean )
                    // InternalDml.g:688:6: lv_boolValue_7_0= ruleEBoolean
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
                    // InternalDml.g:707:3: ( () ( (lv_dateValue_9_0= ruleEDate ) ) )
                    {
                    // InternalDml.g:707:3: ( () ( (lv_dateValue_9_0= ruleEDate ) ) )
                    // InternalDml.g:708:4: () ( (lv_dateValue_9_0= ruleEDate ) )
                    {
                    // InternalDml.g:708:4: ()
                    // InternalDml.g:709:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getDateValueAction_4_0(),
                    						current);
                    				

                    }

                    // InternalDml.g:715:4: ( (lv_dateValue_9_0= ruleEDate ) )
                    // InternalDml.g:716:5: (lv_dateValue_9_0= ruleEDate )
                    {
                    // InternalDml.g:716:5: (lv_dateValue_9_0= ruleEDate )
                    // InternalDml.g:717:6: lv_dateValue_9_0= ruleEDate
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
                    // InternalDml.g:736:3: this_ArrayValues_10= ruleArrayValues
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
                    // InternalDml.g:745:3: this_AbstractObjectValue_11= ruleAbstractObjectValue
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
    // InternalDml.g:757:1: entryRuleAbstractObjectValue returns [EObject current=null] : iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF ;
    public final EObject entryRuleAbstractObjectValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractObjectValue = null;


        try {
            // InternalDml.g:757:60: (iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF )
            // InternalDml.g:758:2: iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF
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
    // InternalDml.g:764:1: ruleAbstractObjectValue returns [EObject current=null] : ( () ( (lv_abstractValue_1_0= RULE_ID ) ) ) ;
    public final EObject ruleAbstractObjectValue() throws RecognitionException {
        EObject current = null;

        Token lv_abstractValue_1_0=null;


        	enterRule();

        try {
            // InternalDml.g:770:2: ( ( () ( (lv_abstractValue_1_0= RULE_ID ) ) ) )
            // InternalDml.g:771:2: ( () ( (lv_abstractValue_1_0= RULE_ID ) ) )
            {
            // InternalDml.g:771:2: ( () ( (lv_abstractValue_1_0= RULE_ID ) ) )
            // InternalDml.g:772:3: () ( (lv_abstractValue_1_0= RULE_ID ) )
            {
            // InternalDml.g:772:3: ()
            // InternalDml.g:773:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getAbstractObjectValueAccess().getAbstractObjectValueAction_0(),
            					current);
            			

            }

            // InternalDml.g:779:3: ( (lv_abstractValue_1_0= RULE_ID ) )
            // InternalDml.g:780:4: (lv_abstractValue_1_0= RULE_ID )
            {
            // InternalDml.g:780:4: (lv_abstractValue_1_0= RULE_ID )
            // InternalDml.g:781:5: lv_abstractValue_1_0= RULE_ID
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
    // InternalDml.g:801:1: entryRuleArrayValues returns [EObject current=null] : iv_ruleArrayValues= ruleArrayValues EOF ;
    public final EObject entryRuleArrayValues() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayValues = null;


        try {
            // InternalDml.g:801:52: (iv_ruleArrayValues= ruleArrayValues EOF )
            // InternalDml.g:802:2: iv_ruleArrayValues= ruleArrayValues EOF
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
    // InternalDml.g:808:1: ruleArrayValues returns [EObject current=null] : ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' ) ;
    public final EObject ruleArrayValues() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_values_2_0 = null;

        EObject lv_values_4_0 = null;



        	enterRule();

        try {
            // InternalDml.g:814:2: ( ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' ) )
            // InternalDml.g:815:2: ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' )
            {
            // InternalDml.g:815:2: ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' )
            // InternalDml.g:816:3: () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']'
            {
            // InternalDml.g:816:3: ()
            // InternalDml.g:817:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getArrayValuesAccess().getArrayValuesAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,20,FOLLOW_14); 

            			newLeafNode(otherlv_1, grammarAccess.getArrayValuesAccess().getLeftSquareBracketKeyword_1());
            		
            // InternalDml.g:827:3: ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=RULE_ID && LA14_0<=RULE_INT)||LA14_0==18||LA14_0==20||(LA14_0>=22 && LA14_0<=24)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // InternalDml.g:828:4: ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )*
                    {
                    // InternalDml.g:828:4: ( (lv_values_2_0= rulePrimitiveValue ) )
                    // InternalDml.g:829:5: (lv_values_2_0= rulePrimitiveValue )
                    {
                    // InternalDml.g:829:5: (lv_values_2_0= rulePrimitiveValue )
                    // InternalDml.g:830:6: lv_values_2_0= rulePrimitiveValue
                    {

                    						newCompositeNode(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_0_0());
                    					
                    pushFollow(FOLLOW_15);
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

                    // InternalDml.g:847:4: (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==15) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // InternalDml.g:848:5: otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) )
                    	    {
                    	    otherlv_3=(Token)match(input,15,FOLLOW_13); 

                    	    					newLeafNode(otherlv_3, grammarAccess.getArrayValuesAccess().getCommaKeyword_2_1_0());
                    	    				
                    	    // InternalDml.g:852:5: ( (lv_values_4_0= rulePrimitiveValue ) )
                    	    // InternalDml.g:853:6: (lv_values_4_0= rulePrimitiveValue )
                    	    {
                    	    // InternalDml.g:853:6: (lv_values_4_0= rulePrimitiveValue )
                    	    // InternalDml.g:854:7: lv_values_4_0= rulePrimitiveValue
                    	    {

                    	    							newCompositeNode(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_1_1_0());
                    	    						
                    	    pushFollow(FOLLOW_15);
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
                    	    break loop13;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,21,FOLLOW_2); 

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
    // InternalDml.g:881:1: entryRuleArrayType returns [EObject current=null] : iv_ruleArrayType= ruleArrayType EOF ;
    public final EObject entryRuleArrayType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayType = null;


        try {
            // InternalDml.g:881:50: (iv_ruleArrayType= ruleArrayType EOF )
            // InternalDml.g:882:2: iv_ruleArrayType= ruleArrayType EOF
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
    // InternalDml.g:888:1: ruleArrayType returns [EObject current=null] : ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? ) ;
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
            // InternalDml.g:894:2: ( ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? ) )
            // InternalDml.g:895:2: ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? )
            {
            // InternalDml.g:895:2: ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? )
            // InternalDml.g:896:3: () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )?
            {
            // InternalDml.g:896:3: ()
            // InternalDml.g:897:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getArrayTypeAccess().getArrayTypeAction_0(),
            					current);
            			

            }

            // InternalDml.g:903:3: ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>=27 && LA15_0<=32)) ) {
                alt15=1;
            }
            else if ( (LA15_0==RULE_ID) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // InternalDml.g:904:4: ( (lv_primitiveType_1_0= rulePrimitiveValueType ) )
                    {
                    // InternalDml.g:904:4: ( (lv_primitiveType_1_0= rulePrimitiveValueType ) )
                    // InternalDml.g:905:5: (lv_primitiveType_1_0= rulePrimitiveValueType )
                    {
                    // InternalDml.g:905:5: (lv_primitiveType_1_0= rulePrimitiveValueType )
                    // InternalDml.g:906:6: lv_primitiveType_1_0= rulePrimitiveValueType
                    {

                    						newCompositeNode(grammarAccess.getArrayTypeAccess().getPrimitiveTypePrimitiveValueTypeEnumRuleCall_1_0_0());
                    					
                    pushFollow(FOLLOW_16);
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
                    // InternalDml.g:924:4: ( ( ruleQualifiedName ) )
                    {
                    // InternalDml.g:924:4: ( ( ruleQualifiedName ) )
                    // InternalDml.g:925:5: ( ruleQualifiedName )
                    {
                    // InternalDml.g:925:5: ( ruleQualifiedName )
                    // InternalDml.g:926:6: ruleQualifiedName
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getArrayTypeRule());
                    						}
                    					

                    						newCompositeNode(grammarAccess.getArrayTypeAccess().getDataModelTypeDataModelCrossReference_1_1_0());
                    					
                    pushFollow(FOLLOW_16);
                    ruleQualifiedName();

                    state._fsp--;


                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            otherlv_3=(Token)match(input,20,FOLLOW_17); 

            			newLeafNode(otherlv_3, grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_2());
            		
            otherlv_4=(Token)match(input,21,FOLLOW_3); 

            			newLeafNode(otherlv_4, grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_3());
            		
            // InternalDml.g:949:3: ( (lv_name_5_0= ruleEString ) )
            // InternalDml.g:950:4: (lv_name_5_0= ruleEString )
            {
            // InternalDml.g:950:4: (lv_name_5_0= ruleEString )
            // InternalDml.g:951:5: lv_name_5_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getArrayTypeAccess().getNameEStringParserRuleCall_4_0());
            				
            pushFollow(FOLLOW_12);
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

            // InternalDml.g:968:3: (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==19) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // InternalDml.g:969:4: otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']'
                    {
                    otherlv_6=(Token)match(input,19,FOLLOW_16); 

                    				newLeafNode(otherlv_6, grammarAccess.getArrayTypeAccess().getEqualsSignKeyword_5_0());
                    			
                    otherlv_7=(Token)match(input,20,FOLLOW_14); 

                    				newLeafNode(otherlv_7, grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_5_1());
                    			
                    // InternalDml.g:977:4: ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( ((LA17_0>=RULE_ID && LA17_0<=RULE_INT)||LA17_0==18||LA17_0==20||(LA17_0>=22 && LA17_0<=24)) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // InternalDml.g:978:5: ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )*
                            {
                            // InternalDml.g:978:5: ( (lv_values_8_0= rulePrimitiveValue ) )
                            // InternalDml.g:979:6: (lv_values_8_0= rulePrimitiveValue )
                            {
                            // InternalDml.g:979:6: (lv_values_8_0= rulePrimitiveValue )
                            // InternalDml.g:980:7: lv_values_8_0= rulePrimitiveValue
                            {

                            							newCompositeNode(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_0_0());
                            						
                            pushFollow(FOLLOW_15);
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

                            // InternalDml.g:997:5: (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )*
                            loop16:
                            do {
                                int alt16=2;
                                int LA16_0 = input.LA(1);

                                if ( (LA16_0==15) ) {
                                    alt16=1;
                                }


                                switch (alt16) {
                            	case 1 :
                            	    // InternalDml.g:998:6: otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) )
                            	    {
                            	    otherlv_9=(Token)match(input,15,FOLLOW_13); 

                            	    						newLeafNode(otherlv_9, grammarAccess.getArrayTypeAccess().getCommaKeyword_5_2_1_0());
                            	    					
                            	    // InternalDml.g:1002:6: ( (lv_values_10_0= rulePrimitiveValue ) )
                            	    // InternalDml.g:1003:7: (lv_values_10_0= rulePrimitiveValue )
                            	    {
                            	    // InternalDml.g:1003:7: (lv_values_10_0= rulePrimitiveValue )
                            	    // InternalDml.g:1004:8: lv_values_10_0= rulePrimitiveValue
                            	    {

                            	    								newCompositeNode(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_1_1_0());
                            	    							
                            	    pushFollow(FOLLOW_15);
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
                            	    break loop16;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_11=(Token)match(input,21,FOLLOW_2); 

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
    // InternalDml.g:1032:1: entryRuleEString returns [String current=null] : iv_ruleEString= ruleEString EOF ;
    public final String entryRuleEString() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEString = null;


        try {
            // InternalDml.g:1032:47: (iv_ruleEString= ruleEString EOF )
            // InternalDml.g:1033:2: iv_ruleEString= ruleEString EOF
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
    // InternalDml.g:1039:1: ruleEString returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleEString() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;
        Token this_ID_1=null;


        	enterRule();

        try {
            // InternalDml.g:1045:2: ( (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID ) )
            // InternalDml.g:1046:2: (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID )
            {
            // InternalDml.g:1046:2: (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==RULE_STRING) ) {
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
                    // InternalDml.g:1047:3: this_STRING_0= RULE_STRING
                    {
                    this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

                    			current.merge(this_STRING_0);
                    		

                    			newLeafNode(this_STRING_0, grammarAccess.getEStringAccess().getSTRINGTerminalRuleCall_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalDml.g:1055:3: this_ID_1= RULE_ID
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
    // InternalDml.g:1066:1: entryRuleEInt returns [String current=null] : iv_ruleEInt= ruleEInt EOF ;
    public final String entryRuleEInt() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEInt = null;


        try {
            // InternalDml.g:1066:44: (iv_ruleEInt= ruleEInt EOF )
            // InternalDml.g:1067:2: iv_ruleEInt= ruleEInt EOF
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
    // InternalDml.g:1073:1: ruleEInt returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? this_INT_1= RULE_INT ) ;
    public final AntlrDatatypeRuleToken ruleEInt() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;


        	enterRule();

        try {
            // InternalDml.g:1079:2: ( ( (kw= '-' )? this_INT_1= RULE_INT ) )
            // InternalDml.g:1080:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            {
            // InternalDml.g:1080:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            // InternalDml.g:1081:3: (kw= '-' )? this_INT_1= RULE_INT
            {
            // InternalDml.g:1081:3: (kw= '-' )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==22) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // InternalDml.g:1082:4: kw= '-'
                    {
                    kw=(Token)match(input,22,FOLLOW_18); 

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
    // InternalDml.g:1099:1: entryRuleEBoolean returns [String current=null] : iv_ruleEBoolean= ruleEBoolean EOF ;
    public final String entryRuleEBoolean() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEBoolean = null;


        try {
            // InternalDml.g:1099:48: (iv_ruleEBoolean= ruleEBoolean EOF )
            // InternalDml.g:1100:2: iv_ruleEBoolean= ruleEBoolean EOF
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
    // InternalDml.g:1106:1: ruleEBoolean returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'false' | kw= 'true' ) ;
    public final AntlrDatatypeRuleToken ruleEBoolean() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalDml.g:1112:2: ( (kw= 'false' | kw= 'true' ) )
            // InternalDml.g:1113:2: (kw= 'false' | kw= 'true' )
            {
            // InternalDml.g:1113:2: (kw= 'false' | kw= 'true' )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==23) ) {
                alt21=1;
            }
            else if ( (LA21_0==24) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // InternalDml.g:1114:3: kw= 'false'
                    {
                    kw=(Token)match(input,23,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getEBooleanAccess().getFalseKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalDml.g:1120:3: kw= 'true'
                    {
                    kw=(Token)match(input,24,FOLLOW_2); 

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
    // InternalDml.g:1129:1: entryRuleEFloat returns [String current=null] : iv_ruleEFloat= ruleEFloat EOF ;
    public final String entryRuleEFloat() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEFloat = null;


        try {
            // InternalDml.g:1129:46: (iv_ruleEFloat= ruleEFloat EOF )
            // InternalDml.g:1130:2: iv_ruleEFloat= ruleEFloat EOF
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
    // InternalDml.g:1136:1: ruleEFloat returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? ) ;
    public final AntlrDatatypeRuleToken ruleEFloat() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;
        Token this_INT_3=null;
        Token this_INT_7=null;


        	enterRule();

        try {
            // InternalDml.g:1142:2: ( ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? ) )
            // InternalDml.g:1143:2: ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? )
            {
            // InternalDml.g:1143:2: ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? )
            // InternalDml.g:1144:3: (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )?
            {
            // InternalDml.g:1144:3: (kw= '-' )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==22) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // InternalDml.g:1145:4: kw= '-'
                    {
                    kw=(Token)match(input,22,FOLLOW_19); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getEFloatAccess().getHyphenMinusKeyword_0());
                    			

                    }
                    break;

            }

            // InternalDml.g:1151:3: (this_INT_1= RULE_INT )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==RULE_INT) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // InternalDml.g:1152:4: this_INT_1= RULE_INT
                    {
                    this_INT_1=(Token)match(input,RULE_INT,FOLLOW_20); 

                    				current.merge(this_INT_1);
                    			

                    				newLeafNode(this_INT_1, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_1());
                    			

                    }
                    break;

            }

            kw=(Token)match(input,18,FOLLOW_18); 

            			current.merge(kw);
            			newLeafNode(kw, grammarAccess.getEFloatAccess().getFullStopKeyword_2());
            		
            this_INT_3=(Token)match(input,RULE_INT,FOLLOW_21); 

            			current.merge(this_INT_3);
            		

            			newLeafNode(this_INT_3, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_3());
            		
            // InternalDml.g:1172:3: ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( ((LA26_0>=25 && LA26_0<=26)) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // InternalDml.g:1173:4: (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT
                    {
                    // InternalDml.g:1173:4: (kw= 'E' | kw= 'e' )
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==25) ) {
                        alt24=1;
                    }
                    else if ( (LA24_0==26) ) {
                        alt24=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 24, 0, input);

                        throw nvae;
                    }
                    switch (alt24) {
                        case 1 :
                            // InternalDml.g:1174:5: kw= 'E'
                            {
                            kw=(Token)match(input,25,FOLLOW_22); 

                            					current.merge(kw);
                            					newLeafNode(kw, grammarAccess.getEFloatAccess().getEKeyword_4_0_0());
                            				

                            }
                            break;
                        case 2 :
                            // InternalDml.g:1180:5: kw= 'e'
                            {
                            kw=(Token)match(input,26,FOLLOW_22); 

                            					current.merge(kw);
                            					newLeafNode(kw, grammarAccess.getEFloatAccess().getEKeyword_4_0_1());
                            				

                            }
                            break;

                    }

                    // InternalDml.g:1186:4: (kw= '-' )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==22) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // InternalDml.g:1187:5: kw= '-'
                            {
                            kw=(Token)match(input,22,FOLLOW_18); 

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
    // InternalDml.g:1205:1: entryRuleEDate returns [String current=null] : iv_ruleEDate= ruleEDate EOF ;
    public final String entryRuleEDate() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEDate = null;


        try {
            // InternalDml.g:1205:45: (iv_ruleEDate= ruleEDate EOF )
            // InternalDml.g:1206:2: iv_ruleEDate= ruleEDate EOF
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
    // InternalDml.g:1212:1: ruleEDate returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear ) ;
    public final AntlrDatatypeRuleToken ruleEDate() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_Day_0 = null;

        AntlrDatatypeRuleToken this_Month_2 = null;

        AntlrDatatypeRuleToken this_Year_4 = null;



        	enterRule();

        try {
            // InternalDml.g:1218:2: ( (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear ) )
            // InternalDml.g:1219:2: (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear )
            {
            // InternalDml.g:1219:2: (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear )
            // InternalDml.g:1220:3: this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear
            {

            			newCompositeNode(grammarAccess.getEDateAccess().getDayParserRuleCall_0());
            		
            pushFollow(FOLLOW_23);
            this_Day_0=ruleDay();

            state._fsp--;


            			current.merge(this_Day_0);
            		

            			afterParserOrEnumRuleCall();
            		
            kw=(Token)match(input,22,FOLLOW_18); 

            			current.merge(kw);
            			newLeafNode(kw, grammarAccess.getEDateAccess().getHyphenMinusKeyword_1());
            		

            			newCompositeNode(grammarAccess.getEDateAccess().getMonthParserRuleCall_2());
            		
            pushFollow(FOLLOW_23);
            this_Month_2=ruleMonth();

            state._fsp--;


            			current.merge(this_Month_2);
            		

            			afterParserOrEnumRuleCall();
            		
            kw=(Token)match(input,22,FOLLOW_18); 

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
    // InternalDml.g:1264:1: entryRuleDay returns [String current=null] : iv_ruleDay= ruleDay EOF ;
    public final String entryRuleDay() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDay = null;


        try {
            // InternalDml.g:1264:43: (iv_ruleDay= ruleDay EOF )
            // InternalDml.g:1265:2: iv_ruleDay= ruleDay EOF
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
    // InternalDml.g:1271:1: ruleDay returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleDay() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalDml.g:1277:2: (this_INT_0= RULE_INT )
            // InternalDml.g:1278:2: this_INT_0= RULE_INT
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
    // InternalDml.g:1288:1: entryRuleMonth returns [String current=null] : iv_ruleMonth= ruleMonth EOF ;
    public final String entryRuleMonth() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleMonth = null;


        try {
            // InternalDml.g:1288:45: (iv_ruleMonth= ruleMonth EOF )
            // InternalDml.g:1289:2: iv_ruleMonth= ruleMonth EOF
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
    // InternalDml.g:1295:1: ruleMonth returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleMonth() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalDml.g:1301:2: (this_INT_0= RULE_INT )
            // InternalDml.g:1302:2: this_INT_0= RULE_INT
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
    // InternalDml.g:1312:1: entryRuleYear returns [String current=null] : iv_ruleYear= ruleYear EOF ;
    public final String entryRuleYear() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleYear = null;


        try {
            // InternalDml.g:1312:44: (iv_ruleYear= ruleYear EOF )
            // InternalDml.g:1313:2: iv_ruleYear= ruleYear EOF
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
    // InternalDml.g:1319:1: ruleYear returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleYear() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalDml.g:1325:2: (this_INT_0= RULE_INT )
            // InternalDml.g:1326:2: this_INT_0= RULE_INT
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
    // InternalDml.g:1336:1: rulePrimitiveValueType returns [Enumerator current=null] : ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) ) ;
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
            // InternalDml.g:1342:2: ( ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) ) )
            // InternalDml.g:1343:2: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) )
            {
            // InternalDml.g:1343:2: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) )
            int alt27=6;
            switch ( input.LA(1) ) {
            case 27:
                {
                alt27=1;
                }
                break;
            case 28:
                {
                alt27=2;
                }
                break;
            case 29:
                {
                alt27=3;
                }
                break;
            case 30:
                {
                alt27=4;
                }
                break;
            case 31:
                {
                alt27=5;
                }
                break;
            case 32:
                {
                alt27=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // InternalDml.g:1344:3: (enumLiteral_0= 'int' )
                    {
                    // InternalDml.g:1344:3: (enumLiteral_0= 'int' )
                    // InternalDml.g:1345:4: enumLiteral_0= 'int'
                    {
                    enumLiteral_0=(Token)match(input,27,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalDml.g:1352:3: (enumLiteral_1= 'boolean' )
                    {
                    // InternalDml.g:1352:3: (enumLiteral_1= 'boolean' )
                    // InternalDml.g:1353:4: enumLiteral_1= 'boolean'
                    {
                    enumLiteral_1=(Token)match(input,28,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalDml.g:1360:3: (enumLiteral_2= 'float' )
                    {
                    // InternalDml.g:1360:3: (enumLiteral_2= 'float' )
                    // InternalDml.g:1361:4: enumLiteral_2= 'float'
                    {
                    enumLiteral_2=(Token)match(input,29,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;
                case 4 :
                    // InternalDml.g:1368:3: (enumLiteral_3= 'string' )
                    {
                    // InternalDml.g:1368:3: (enumLiteral_3= 'string' )
                    // InternalDml.g:1369:4: enumLiteral_3= 'string'
                    {
                    enumLiteral_3=(Token)match(input,30,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_3, grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3());
                    			

                    }


                    }
                    break;
                case 5 :
                    // InternalDml.g:1376:3: (enumLiteral_4= 'object' )
                    {
                    // InternalDml.g:1376:3: (enumLiteral_4= 'object' )
                    // InternalDml.g:1377:4: enumLiteral_4= 'object'
                    {
                    enumLiteral_4=(Token)match(input,31,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_4, grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4());
                    			

                    }


                    }
                    break;
                case 6 :
                    // InternalDml.g:1384:3: (enumLiteral_5= 'date' )
                    {
                    // InternalDml.g:1384:3: (enumLiteral_5= 'date' )
                    // InternalDml.g:1385:4: enumLiteral_5= 'date'
                    {
                    enumLiteral_5=(Token)match(input,32,FOLLOW_2); 

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


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA12 dfa12 = new DFA12(this);
    static final String dfa_1s = "\15\uffff";
    static final String dfa_2s = "\10\4\2\uffff\1\4\1\uffff\1\4";
    static final String dfa_3s = "\1\40\7\24\2\uffff\1\4\1\uffff\1\24";
    static final String dfa_4s = "\10\uffff\1\3\1\1\1\uffff\1\2\1\uffff";
    static final String dfa_5s = "\15\uffff}>";
    static final String[] dfa_6s = {
            "\1\7\26\uffff\1\1\1\2\1\3\1\4\1\5\1\6",
            "\2\11\16\uffff\1\10",
            "\2\11\16\uffff\1\10",
            "\2\11\16\uffff\1\10",
            "\2\11\16\uffff\1\10",
            "\2\11\16\uffff\1\10",
            "\2\11\16\uffff\1\10",
            "\2\13\14\uffff\1\12\1\uffff\1\10",
            "",
            "",
            "\1\14",
            "",
            "\2\13\14\uffff\1\12\1\uffff\1\10"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "331:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )";
        }
    }
    static final String dfa_7s = "\13\uffff";
    static final String dfa_8s = "\2\uffff\1\12\5\uffff\1\12\2\uffff";
    static final String dfa_9s = "\1\4\1\6\1\17\5\uffff\1\17\2\uffff";
    static final String dfa_10s = "\1\30\1\22\1\26\5\uffff\1\25\2\uffff";
    static final String dfa_11s = "\3\uffff\1\2\1\3\1\4\1\6\1\7\1\uffff\1\5\1\1";
    static final String dfa_12s = "\13\uffff}>";
    static final String[] dfa_13s = {
            "\1\7\1\4\1\2\13\uffff\1\3\1\uffff\1\6\1\uffff\1\1\2\5",
            "\1\10\13\uffff\1\3",
            "\2\12\1\uffff\1\3\2\uffff\1\12\1\11",
            "",
            "",
            "",
            "",
            "",
            "\2\12\1\uffff\1\3\2\uffff\1\12",
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

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_9;
            this.max = dfa_10;
            this.accept = dfa_11;
            this.special = dfa_12;
            this.transition = dfa_13;
        }
        public String getDescription() {
            return "591:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )";
        }
    }
 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000031002L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000035002L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x00000001F8000010L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000018000L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000001D40070L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000001F40070L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000208000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000040040L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000006000002L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000000400040L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000000400000L});

}
