package com.smr.activity.dsl.parser.antlr.internal;

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
import com.smr.activity.dsl.services.ActivityDiagramGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalActivityDiagramParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'ActivityDiagram'", "'uses'", "'Objects'", "'['", "','", "']'", "'on'", "'context'", "'physical'", "'contexts'", "'produces'", "'results'", "'('", "')'", "'has'", "'activities'", "'{'", "'}'", "'Activity'", "'description'", "':'", "'inputData'", "'requireCapability'", "'requireOperation'", "'childActivityDiagram'", "'conditions'", "'nextActivity'", "'nextActivityDiagram'", "'time'", "'interruptedBy'", "'interrupts'", "'=>'", "'final'", "'result'", "'and'", "'or'", "'from'", "'if'", "'outcome'", "'is'", "'>'", "'<'", "'='", "'DataModel'", "'primitives'", "'composites'", "'.'", "'-'", "'false'", "'true'", "'E'", "'e'", "'secs'", "'mins'", "'hrs'", "'days'", "'int'", "'boolean'", "'float'", "'string'", "'object'", "'date'"
    };
    public static final int T__50=50;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__59=59;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__11=11;
    public static final int T__55=55;
    public static final int T__12=12;
    public static final int T__56=56;
    public static final int T__13=13;
    public static final int T__57=57;
    public static final int T__14=14;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__60=60;
    public static final int T__61=61;
    public static final int RULE_ID=5;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=6;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int T__66=66;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__23=23;
    public static final int T__67=67;
    public static final int T__24=24;
    public static final int T__68=68;
    public static final int T__25=25;
    public static final int T__69=69;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__20=20;
    public static final int T__64=64;
    public static final int T__21=21;
    public static final int T__65=65;
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int RULE_STRING=4;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int EOF=-1;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int RULE_WS=9;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;

    // delegates
    // delegators


        public InternalActivityDiagramParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalActivityDiagramParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalActivityDiagramParser.tokenNames; }
    public String getGrammarFileName() { return "InternalActivityDiagram.g"; }



     	private ActivityDiagramGrammarAccess grammarAccess;

        public InternalActivityDiagramParser(TokenStream input, ActivityDiagramGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "ActivityDiagram";
       	}

       	@Override
       	protected ActivityDiagramGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleActivityDiagram"
    // InternalActivityDiagram.g:65:1: entryRuleActivityDiagram returns [EObject current=null] : iv_ruleActivityDiagram= ruleActivityDiagram EOF ;
    public final EObject entryRuleActivityDiagram() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActivityDiagram = null;


        try {
            // InternalActivityDiagram.g:65:56: (iv_ruleActivityDiagram= ruleActivityDiagram EOF )
            // InternalActivityDiagram.g:66:2: iv_ruleActivityDiagram= ruleActivityDiagram EOF
            {
             newCompositeNode(grammarAccess.getActivityDiagramRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleActivityDiagram=ruleActivityDiagram();

            state._fsp--;

             current =iv_ruleActivityDiagram; 
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
    // $ANTLR end "entryRuleActivityDiagram"


    // $ANTLR start "ruleActivityDiagram"
    // InternalActivityDiagram.g:72:1: ruleActivityDiagram returns [EObject current=null] : ( () otherlv_1= 'ActivityDiagram' ( (lv_name_2_0= ruleEString ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?) ) )? (otherlv_11= 'on' otherlv_12= 'context' ( ( ruleQualifiedName ) ) (otherlv_14= ',' ( ( ruleQualifiedName ) )* )? )? (otherlv_16= 'physical' otherlv_17= 'contexts' ( ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )* ) )? (otherlv_21= 'produces' otherlv_22= 'results' otherlv_23= '(' ( (lv_results_24_0= ruleParameter ) ) (otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) ) )* otherlv_27= ')' )? (otherlv_28= 'has' otherlv_29= 'activities' otherlv_30= '{' ( (lv_activities_31_0= ruleActivity ) ) (otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) ) )* otherlv_34= '}' )? ) ;
    public final EObject ruleActivityDiagram() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token otherlv_16=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        Token otherlv_21=null;
        Token otherlv_22=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token otherlv_27=null;
        Token otherlv_28=null;
        Token otherlv_29=null;
        Token otherlv_30=null;
        Token otherlv_32=null;
        Token otherlv_34=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        AntlrDatatypeRuleToken lv_physicalContext_18_0 = null;

        AntlrDatatypeRuleToken lv_physicalContext_20_0 = null;

        EObject lv_results_24_0 = null;

        EObject lv_results_26_0 = null;

        EObject lv_activities_31_0 = null;

        EObject lv_activities_33_0 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:78:2: ( ( () otherlv_1= 'ActivityDiagram' ( (lv_name_2_0= ruleEString ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?) ) )? (otherlv_11= 'on' otherlv_12= 'context' ( ( ruleQualifiedName ) ) (otherlv_14= ',' ( ( ruleQualifiedName ) )* )? )? (otherlv_16= 'physical' otherlv_17= 'contexts' ( ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )* ) )? (otherlv_21= 'produces' otherlv_22= 'results' otherlv_23= '(' ( (lv_results_24_0= ruleParameter ) ) (otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) ) )* otherlv_27= ')' )? (otherlv_28= 'has' otherlv_29= 'activities' otherlv_30= '{' ( (lv_activities_31_0= ruleActivity ) ) (otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) ) )* otherlv_34= '}' )? ) )
            // InternalActivityDiagram.g:79:2: ( () otherlv_1= 'ActivityDiagram' ( (lv_name_2_0= ruleEString ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?) ) )? (otherlv_11= 'on' otherlv_12= 'context' ( ( ruleQualifiedName ) ) (otherlv_14= ',' ( ( ruleQualifiedName ) )* )? )? (otherlv_16= 'physical' otherlv_17= 'contexts' ( ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )* ) )? (otherlv_21= 'produces' otherlv_22= 'results' otherlv_23= '(' ( (lv_results_24_0= ruleParameter ) ) (otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) ) )* otherlv_27= ')' )? (otherlv_28= 'has' otherlv_29= 'activities' otherlv_30= '{' ( (lv_activities_31_0= ruleActivity ) ) (otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) ) )* otherlv_34= '}' )? )
            {
            // InternalActivityDiagram.g:79:2: ( () otherlv_1= 'ActivityDiagram' ( (lv_name_2_0= ruleEString ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?) ) )? (otherlv_11= 'on' otherlv_12= 'context' ( ( ruleQualifiedName ) ) (otherlv_14= ',' ( ( ruleQualifiedName ) )* )? )? (otherlv_16= 'physical' otherlv_17= 'contexts' ( ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )* ) )? (otherlv_21= 'produces' otherlv_22= 'results' otherlv_23= '(' ( (lv_results_24_0= ruleParameter ) ) (otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) ) )* otherlv_27= ')' )? (otherlv_28= 'has' otherlv_29= 'activities' otherlv_30= '{' ( (lv_activities_31_0= ruleActivity ) ) (otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) ) )* otherlv_34= '}' )? )
            // InternalActivityDiagram.g:80:3: () otherlv_1= 'ActivityDiagram' ( (lv_name_2_0= ruleEString ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?) ) )? (otherlv_11= 'on' otherlv_12= 'context' ( ( ruleQualifiedName ) ) (otherlv_14= ',' ( ( ruleQualifiedName ) )* )? )? (otherlv_16= 'physical' otherlv_17= 'contexts' ( ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )* ) )? (otherlv_21= 'produces' otherlv_22= 'results' otherlv_23= '(' ( (lv_results_24_0= ruleParameter ) ) (otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) ) )* otherlv_27= ')' )? (otherlv_28= 'has' otherlv_29= 'activities' otherlv_30= '{' ( (lv_activities_31_0= ruleActivity ) ) (otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) ) )* otherlv_34= '}' )?
            {
            // InternalActivityDiagram.g:80:3: ()
            // InternalActivityDiagram.g:81:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getActivityDiagramAccess().getActivityDiagramAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,11,FOLLOW_3); 

            			newLeafNode(otherlv_1, grammarAccess.getActivityDiagramAccess().getActivityDiagramKeyword_1());
            		
            // InternalActivityDiagram.g:91:3: ( (lv_name_2_0= ruleEString ) )
            // InternalActivityDiagram.g:92:4: (lv_name_2_0= ruleEString )
            {
            // InternalActivityDiagram.g:92:4: (lv_name_2_0= ruleEString )
            // InternalActivityDiagram.g:93:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getActivityDiagramAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_4);
            lv_name_2_0=ruleEString();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getActivityDiagramRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"com.dml.dsl.Dml.EString");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalActivityDiagram.g:110:3: ( ( ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?) ) )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==12||LA4_0==16) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // InternalActivityDiagram.g:111:4: ( ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?) )
                    {
                    // InternalActivityDiagram.g:111:4: ( ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?) )
                    // InternalActivityDiagram.g:112:5: ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?)
                    {
                     
                    				  getUnorderedGroupHelper().enter(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3());
                    				
                    // InternalActivityDiagram.g:115:5: ( ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?)
                    // InternalActivityDiagram.g:116:6: ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+ {...}?
                    {
                    // InternalActivityDiagram.g:116:6: ( ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) ) )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=3;
                        int LA3_0 = input.LA(1);

                        if ( LA3_0 == 12 && getUnorderedGroupHelper().canSelect(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3(), 0) ) {
                            alt3=1;
                        }
                        else if ( LA3_0 == 16 && getUnorderedGroupHelper().canSelect(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3(), 1) ) {
                            alt3=2;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // InternalActivityDiagram.g:117:4: ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) )
                    	    {
                    	    // InternalActivityDiagram.g:117:4: ({...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) ) )
                    	    // InternalActivityDiagram.g:118:5: {...}? => ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) )
                    	    {
                    	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3(), 0) ) {
                    	        throw new FailedPredicateException(input, "ruleActivityDiagram", "getUnorderedGroupHelper().canSelect(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3(), 0)");
                    	    }
                    	    // InternalActivityDiagram.g:118:112: ( ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) ) )
                    	    // InternalActivityDiagram.g:119:6: ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) )
                    	    {

                    	    						getUnorderedGroupHelper().select(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3(), 0);
                    	    					
                    	    // InternalActivityDiagram.g:122:9: ({...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? ) )
                    	    // InternalActivityDiagram.g:122:10: {...}? => (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? )
                    	    {
                    	    if ( !((true)) ) {
                    	        throw new FailedPredicateException(input, "ruleActivityDiagram", "true");
                    	    }
                    	    // InternalActivityDiagram.g:122:19: (otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )? )
                    	    // InternalActivityDiagram.g:122:20: otherlv_4= 'uses' otherlv_5= 'Objects' otherlv_6= '[' ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )?
                    	    {
                    	    otherlv_4=(Token)match(input,12,FOLLOW_5); 

                    	    									newLeafNode(otherlv_4, grammarAccess.getActivityDiagramAccess().getUsesKeyword_3_0_0());
                    	    								
                    	    otherlv_5=(Token)match(input,13,FOLLOW_6); 

                    	    									newLeafNode(otherlv_5, grammarAccess.getActivityDiagramAccess().getObjectsKeyword_3_0_1());
                    	    								
                    	    otherlv_6=(Token)match(input,14,FOLLOW_7); 

                    	    									newLeafNode(otherlv_6, grammarAccess.getActivityDiagramAccess().getLeftSquareBracketKeyword_3_0_2());
                    	    								
                    	    // InternalActivityDiagram.g:134:9: ( ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )* )?
                    	    int alt2=2;
                    	    int LA2_0 = input.LA(1);

                    	    if ( (LA2_0==RULE_ID) ) {
                    	        alt2=1;
                    	    }
                    	    switch (alt2) {
                    	        case 1 :
                    	            // InternalActivityDiagram.g:135:10: ( ( ruleQualifiedName ) ) (otherlv_8= ',' ( ( ruleQualifiedName ) ) )*
                    	            {
                    	            // InternalActivityDiagram.g:135:10: ( ( ruleQualifiedName ) )
                    	            // InternalActivityDiagram.g:136:11: ( ruleQualifiedName )
                    	            {
                    	            // InternalActivityDiagram.g:136:11: ( ruleQualifiedName )
                    	            // InternalActivityDiagram.g:137:12: ruleQualifiedName
                    	            {

                    	            												if (current==null) {
                    	            													current = createModelElement(grammarAccess.getActivityDiagramRule());
                    	            												}
                    	            											

                    	            												newCompositeNode(grammarAccess.getActivityDiagramAccess().getDataObjectsParameterCrossReference_3_0_3_0_0());
                    	            											
                    	            pushFollow(FOLLOW_8);
                    	            ruleQualifiedName();

                    	            state._fsp--;


                    	            												afterParserOrEnumRuleCall();
                    	            											

                    	            }


                    	            }

                    	            // InternalActivityDiagram.g:151:10: (otherlv_8= ',' ( ( ruleQualifiedName ) ) )*
                    	            loop1:
                    	            do {
                    	                int alt1=2;
                    	                int LA1_0 = input.LA(1);

                    	                if ( (LA1_0==15) ) {
                    	                    alt1=1;
                    	                }


                    	                switch (alt1) {
                    	            	case 1 :
                    	            	    // InternalActivityDiagram.g:152:11: otherlv_8= ',' ( ( ruleQualifiedName ) )
                    	            	    {
                    	            	    otherlv_8=(Token)match(input,15,FOLLOW_9); 

                    	            	    											newLeafNode(otherlv_8, grammarAccess.getActivityDiagramAccess().getCommaKeyword_3_0_3_1_0());
                    	            	    										
                    	            	    // InternalActivityDiagram.g:156:11: ( ( ruleQualifiedName ) )
                    	            	    // InternalActivityDiagram.g:157:12: ( ruleQualifiedName )
                    	            	    {
                    	            	    // InternalActivityDiagram.g:157:12: ( ruleQualifiedName )
                    	            	    // InternalActivityDiagram.g:158:13: ruleQualifiedName
                    	            	    {

                    	            	    													if (current==null) {
                    	            	    														current = createModelElement(grammarAccess.getActivityDiagramRule());
                    	            	    													}
                    	            	    												

                    	            	    													newCompositeNode(grammarAccess.getActivityDiagramAccess().getDataObjectsParameterCrossReference_3_0_3_1_1_0());
                    	            	    												
                    	            	    pushFollow(FOLLOW_8);
                    	            	    ruleQualifiedName();

                    	            	    state._fsp--;


                    	            	    													afterParserOrEnumRuleCall();
                    	            	    												

                    	            	    }


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

                    	     
                    	    						getUnorderedGroupHelper().returnFromSelection(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3());
                    	    					

                    	    }


                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalActivityDiagram.g:180:4: ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) )
                    	    {
                    	    // InternalActivityDiagram.g:180:4: ({...}? => ( ({...}? => (otherlv_10= ']' ) ) ) )
                    	    // InternalActivityDiagram.g:181:5: {...}? => ( ({...}? => (otherlv_10= ']' ) ) )
                    	    {
                    	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3(), 1) ) {
                    	        throw new FailedPredicateException(input, "ruleActivityDiagram", "getUnorderedGroupHelper().canSelect(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3(), 1)");
                    	    }
                    	    // InternalActivityDiagram.g:181:112: ( ({...}? => (otherlv_10= ']' ) ) )
                    	    // InternalActivityDiagram.g:182:6: ({...}? => (otherlv_10= ']' ) )
                    	    {

                    	    						getUnorderedGroupHelper().select(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3(), 1);
                    	    					
                    	    // InternalActivityDiagram.g:185:9: ({...}? => (otherlv_10= ']' ) )
                    	    // InternalActivityDiagram.g:185:10: {...}? => (otherlv_10= ']' )
                    	    {
                    	    if ( !((true)) ) {
                    	        throw new FailedPredicateException(input, "ruleActivityDiagram", "true");
                    	    }
                    	    // InternalActivityDiagram.g:185:19: (otherlv_10= ']' )
                    	    // InternalActivityDiagram.g:185:20: otherlv_10= ']'
                    	    {
                    	    otherlv_10=(Token)match(input,16,FOLLOW_4); 

                    	    									newLeafNode(otherlv_10, grammarAccess.getActivityDiagramAccess().getRightSquareBracketKeyword_3_1());
                    	    								

                    	    }


                    	    }

                    	     
                    	    						getUnorderedGroupHelper().returnFromSelection(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3());
                    	    					

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);

                    if ( ! getUnorderedGroupHelper().canLeave(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3()) ) {
                        throw new FailedPredicateException(input, "ruleActivityDiagram", "getUnorderedGroupHelper().canLeave(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3())");
                    }

                    }


                    }

                     
                    				  getUnorderedGroupHelper().leave(grammarAccess.getActivityDiagramAccess().getUnorderedGroup_3());
                    				

                    }
                    break;

            }

            // InternalActivityDiagram.g:203:3: (otherlv_11= 'on' otherlv_12= 'context' ( ( ruleQualifiedName ) ) (otherlv_14= ',' ( ( ruleQualifiedName ) )* )? )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==17) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // InternalActivityDiagram.g:204:4: otherlv_11= 'on' otherlv_12= 'context' ( ( ruleQualifiedName ) ) (otherlv_14= ',' ( ( ruleQualifiedName ) )* )?
                    {
                    otherlv_11=(Token)match(input,17,FOLLOW_10); 

                    				newLeafNode(otherlv_11, grammarAccess.getActivityDiagramAccess().getOnKeyword_4_0());
                    			
                    otherlv_12=(Token)match(input,18,FOLLOW_9); 

                    				newLeafNode(otherlv_12, grammarAccess.getActivityDiagramAccess().getContextKeyword_4_1());
                    			
                    // InternalActivityDiagram.g:212:4: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:213:5: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:213:5: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:214:6: ruleQualifiedName
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getActivityDiagramRule());
                    						}
                    					

                    						newCompositeNode(grammarAccess.getActivityDiagramAccess().getContextDataModelDataModelCrossReference_4_2_0());
                    					
                    pushFollow(FOLLOW_11);
                    ruleQualifiedName();

                    state._fsp--;


                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalActivityDiagram.g:228:4: (otherlv_14= ',' ( ( ruleQualifiedName ) )* )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==15) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // InternalActivityDiagram.g:229:5: otherlv_14= ',' ( ( ruleQualifiedName ) )*
                            {
                            otherlv_14=(Token)match(input,15,FOLLOW_12); 

                            					newLeafNode(otherlv_14, grammarAccess.getActivityDiagramAccess().getCommaKeyword_4_3_0());
                            				
                            // InternalActivityDiagram.g:233:5: ( ( ruleQualifiedName ) )*
                            loop5:
                            do {
                                int alt5=2;
                                int LA5_0 = input.LA(1);

                                if ( (LA5_0==RULE_ID) ) {
                                    alt5=1;
                                }


                                switch (alt5) {
                            	case 1 :
                            	    // InternalActivityDiagram.g:234:6: ( ruleQualifiedName )
                            	    {
                            	    // InternalActivityDiagram.g:234:6: ( ruleQualifiedName )
                            	    // InternalActivityDiagram.g:235:7: ruleQualifiedName
                            	    {

                            	    							if (current==null) {
                            	    								current = createModelElement(grammarAccess.getActivityDiagramRule());
                            	    							}
                            	    						

                            	    							newCompositeNode(grammarAccess.getActivityDiagramAccess().getContextDataModelDataModelCrossReference_4_3_1_0());
                            	    						
                            	    pushFollow(FOLLOW_12);
                            	    ruleQualifiedName();

                            	    state._fsp--;


                            	    							afterParserOrEnumRuleCall();
                            	    						

                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop5;
                                }
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;

            }

            // InternalActivityDiagram.g:251:3: (otherlv_16= 'physical' otherlv_17= 'contexts' ( ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )* ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==19) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // InternalActivityDiagram.g:252:4: otherlv_16= 'physical' otherlv_17= 'contexts' ( ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )* )
                    {
                    otherlv_16=(Token)match(input,19,FOLLOW_13); 

                    				newLeafNode(otherlv_16, grammarAccess.getActivityDiagramAccess().getPhysicalKeyword_5_0());
                    			
                    otherlv_17=(Token)match(input,20,FOLLOW_14); 

                    				newLeafNode(otherlv_17, grammarAccess.getActivityDiagramAccess().getContextsKeyword_5_1());
                    			
                    // InternalActivityDiagram.g:260:4: ( ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )* )
                    // InternalActivityDiagram.g:261:5: ( (lv_physicalContext_18_0= rulePhysicalContext ) ) (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )*
                    {
                    // InternalActivityDiagram.g:261:5: ( (lv_physicalContext_18_0= rulePhysicalContext ) )
                    // InternalActivityDiagram.g:262:6: (lv_physicalContext_18_0= rulePhysicalContext )
                    {
                    // InternalActivityDiagram.g:262:6: (lv_physicalContext_18_0= rulePhysicalContext )
                    // InternalActivityDiagram.g:263:7: lv_physicalContext_18_0= rulePhysicalContext
                    {

                    							newCompositeNode(grammarAccess.getActivityDiagramAccess().getPhysicalContextPhysicalContextParserRuleCall_5_2_0_0());
                    						
                    pushFollow(FOLLOW_15);
                    lv_physicalContext_18_0=rulePhysicalContext();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getActivityDiagramRule());
                    							}
                    							add(
                    								current,
                    								"physicalContext",
                    								lv_physicalContext_18_0,
                    								"com.smr.activity.dsl.ActivityDiagram.PhysicalContext");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalActivityDiagram.g:280:5: (otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) ) )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==15) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // InternalActivityDiagram.g:281:6: otherlv_19= ',' ( (lv_physicalContext_20_0= rulePhysicalContext ) )
                    	    {
                    	    otherlv_19=(Token)match(input,15,FOLLOW_14); 

                    	    						newLeafNode(otherlv_19, grammarAccess.getActivityDiagramAccess().getCommaKeyword_5_2_1_0());
                    	    					
                    	    // InternalActivityDiagram.g:285:6: ( (lv_physicalContext_20_0= rulePhysicalContext ) )
                    	    // InternalActivityDiagram.g:286:7: (lv_physicalContext_20_0= rulePhysicalContext )
                    	    {
                    	    // InternalActivityDiagram.g:286:7: (lv_physicalContext_20_0= rulePhysicalContext )
                    	    // InternalActivityDiagram.g:287:8: lv_physicalContext_20_0= rulePhysicalContext
                    	    {

                    	    								newCompositeNode(grammarAccess.getActivityDiagramAccess().getPhysicalContextPhysicalContextParserRuleCall_5_2_1_1_0());
                    	    							
                    	    pushFollow(FOLLOW_15);
                    	    lv_physicalContext_20_0=rulePhysicalContext();

                    	    state._fsp--;


                    	    								if (current==null) {
                    	    									current = createModelElementForParent(grammarAccess.getActivityDiagramRule());
                    	    								}
                    	    								add(
                    	    									current,
                    	    									"physicalContext",
                    	    									lv_physicalContext_20_0,
                    	    									"com.smr.activity.dsl.ActivityDiagram.PhysicalContext");
                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    }


                    }
                    break;

            }

            // InternalActivityDiagram.g:307:3: (otherlv_21= 'produces' otherlv_22= 'results' otherlv_23= '(' ( (lv_results_24_0= ruleParameter ) ) (otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) ) )* otherlv_27= ')' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==21) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalActivityDiagram.g:308:4: otherlv_21= 'produces' otherlv_22= 'results' otherlv_23= '(' ( (lv_results_24_0= ruleParameter ) ) (otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) ) )* otherlv_27= ')'
                    {
                    otherlv_21=(Token)match(input,21,FOLLOW_16); 

                    				newLeafNode(otherlv_21, grammarAccess.getActivityDiagramAccess().getProducesKeyword_6_0());
                    			
                    otherlv_22=(Token)match(input,22,FOLLOW_17); 

                    				newLeafNode(otherlv_22, grammarAccess.getActivityDiagramAccess().getResultsKeyword_6_1());
                    			
                    otherlv_23=(Token)match(input,23,FOLLOW_18); 

                    				newLeafNode(otherlv_23, grammarAccess.getActivityDiagramAccess().getLeftParenthesisKeyword_6_2());
                    			
                    // InternalActivityDiagram.g:320:4: ( (lv_results_24_0= ruleParameter ) )
                    // InternalActivityDiagram.g:321:5: (lv_results_24_0= ruleParameter )
                    {
                    // InternalActivityDiagram.g:321:5: (lv_results_24_0= ruleParameter )
                    // InternalActivityDiagram.g:322:6: lv_results_24_0= ruleParameter
                    {

                    						newCompositeNode(grammarAccess.getActivityDiagramAccess().getResultsParameterParserRuleCall_6_3_0());
                    					
                    pushFollow(FOLLOW_19);
                    lv_results_24_0=ruleParameter();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getActivityDiagramRule());
                    						}
                    						add(
                    							current,
                    							"results",
                    							lv_results_24_0,
                    							"com.dml.dsl.Dml.Parameter");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalActivityDiagram.g:339:4: (otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) ) )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==15) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // InternalActivityDiagram.g:340:5: otherlv_25= ',' ( (lv_results_26_0= ruleParameter ) )
                    	    {
                    	    otherlv_25=(Token)match(input,15,FOLLOW_18); 

                    	    					newLeafNode(otherlv_25, grammarAccess.getActivityDiagramAccess().getCommaKeyword_6_4_0());
                    	    				
                    	    // InternalActivityDiagram.g:344:5: ( (lv_results_26_0= ruleParameter ) )
                    	    // InternalActivityDiagram.g:345:6: (lv_results_26_0= ruleParameter )
                    	    {
                    	    // InternalActivityDiagram.g:345:6: (lv_results_26_0= ruleParameter )
                    	    // InternalActivityDiagram.g:346:7: lv_results_26_0= ruleParameter
                    	    {

                    	    							newCompositeNode(grammarAccess.getActivityDiagramAccess().getResultsParameterParserRuleCall_6_4_1_0());
                    	    						
                    	    pushFollow(FOLLOW_19);
                    	    lv_results_26_0=ruleParameter();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getActivityDiagramRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"results",
                    	    								lv_results_26_0,
                    	    								"com.dml.dsl.Dml.Parameter");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    otherlv_27=(Token)match(input,24,FOLLOW_20); 

                    				newLeafNode(otherlv_27, grammarAccess.getActivityDiagramAccess().getRightParenthesisKeyword_6_5());
                    			

                    }
                    break;

            }

            // InternalActivityDiagram.g:369:3: (otherlv_28= 'has' otherlv_29= 'activities' otherlv_30= '{' ( (lv_activities_31_0= ruleActivity ) ) (otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) ) )* otherlv_34= '}' )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==25) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // InternalActivityDiagram.g:370:4: otherlv_28= 'has' otherlv_29= 'activities' otherlv_30= '{' ( (lv_activities_31_0= ruleActivity ) ) (otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) ) )* otherlv_34= '}'
                    {
                    otherlv_28=(Token)match(input,25,FOLLOW_21); 

                    				newLeafNode(otherlv_28, grammarAccess.getActivityDiagramAccess().getHasKeyword_7_0());
                    			
                    otherlv_29=(Token)match(input,26,FOLLOW_22); 

                    				newLeafNode(otherlv_29, grammarAccess.getActivityDiagramAccess().getActivitiesKeyword_7_1());
                    			
                    otherlv_30=(Token)match(input,27,FOLLOW_23); 

                    				newLeafNode(otherlv_30, grammarAccess.getActivityDiagramAccess().getLeftCurlyBracketKeyword_7_2());
                    			
                    // InternalActivityDiagram.g:382:4: ( (lv_activities_31_0= ruleActivity ) )
                    // InternalActivityDiagram.g:383:5: (lv_activities_31_0= ruleActivity )
                    {
                    // InternalActivityDiagram.g:383:5: (lv_activities_31_0= ruleActivity )
                    // InternalActivityDiagram.g:384:6: lv_activities_31_0= ruleActivity
                    {

                    						newCompositeNode(grammarAccess.getActivityDiagramAccess().getActivitiesActivityParserRuleCall_7_3_0());
                    					
                    pushFollow(FOLLOW_24);
                    lv_activities_31_0=ruleActivity();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getActivityDiagramRule());
                    						}
                    						add(
                    							current,
                    							"activities",
                    							lv_activities_31_0,
                    							"com.smr.activity.dsl.ActivityDiagram.Activity");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalActivityDiagram.g:401:4: (otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) ) )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==15) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // InternalActivityDiagram.g:402:5: otherlv_32= ',' ( (lv_activities_33_0= ruleActivity ) )
                    	    {
                    	    otherlv_32=(Token)match(input,15,FOLLOW_23); 

                    	    					newLeafNode(otherlv_32, grammarAccess.getActivityDiagramAccess().getCommaKeyword_7_4_0());
                    	    				
                    	    // InternalActivityDiagram.g:406:5: ( (lv_activities_33_0= ruleActivity ) )
                    	    // InternalActivityDiagram.g:407:6: (lv_activities_33_0= ruleActivity )
                    	    {
                    	    // InternalActivityDiagram.g:407:6: (lv_activities_33_0= ruleActivity )
                    	    // InternalActivityDiagram.g:408:7: lv_activities_33_0= ruleActivity
                    	    {

                    	    							newCompositeNode(grammarAccess.getActivityDiagramAccess().getActivitiesActivityParserRuleCall_7_4_1_0());
                    	    						
                    	    pushFollow(FOLLOW_24);
                    	    lv_activities_33_0=ruleActivity();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getActivityDiagramRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"activities",
                    	    								lv_activities_33_0,
                    	    								"com.smr.activity.dsl.ActivityDiagram.Activity");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);

                    otherlv_34=(Token)match(input,28,FOLLOW_2); 

                    				newLeafNode(otherlv_34, grammarAccess.getActivityDiagramAccess().getRightCurlyBracketKeyword_7_5());
                    			

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
    // $ANTLR end "ruleActivityDiagram"


    // $ANTLR start "entryRulePhysicalContext"
    // InternalActivityDiagram.g:435:1: entryRulePhysicalContext returns [String current=null] : iv_rulePhysicalContext= rulePhysicalContext EOF ;
    public final String entryRulePhysicalContext() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePhysicalContext = null;


        try {
            // InternalActivityDiagram.g:435:55: (iv_rulePhysicalContext= rulePhysicalContext EOF )
            // InternalActivityDiagram.g:436:2: iv_rulePhysicalContext= rulePhysicalContext EOF
            {
             newCompositeNode(grammarAccess.getPhysicalContextRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePhysicalContext=rulePhysicalContext();

            state._fsp--;

             current =iv_rulePhysicalContext.getText(); 
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
    // $ANTLR end "entryRulePhysicalContext"


    // $ANTLR start "rulePhysicalContext"
    // InternalActivityDiagram.g:442:1: rulePhysicalContext returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePhysicalContext() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:448:2: (this_STRING_0= RULE_STRING )
            // InternalActivityDiagram.g:449:2: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            		current.merge(this_STRING_0);
            	

            		newLeafNode(this_STRING_0, grammarAccess.getPhysicalContextAccess().getSTRINGTerminalRuleCall());
            	

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
    // $ANTLR end "rulePhysicalContext"


    // $ANTLR start "entryRuleActivity"
    // InternalActivityDiagram.g:459:1: entryRuleActivity returns [EObject current=null] : iv_ruleActivity= ruleActivity EOF ;
    public final EObject entryRuleActivity() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActivity = null;


        try {
            // InternalActivityDiagram.g:459:49: (iv_ruleActivity= ruleActivity EOF )
            // InternalActivityDiagram.g:460:2: iv_ruleActivity= ruleActivity EOF
            {
             newCompositeNode(grammarAccess.getActivityRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleActivity=ruleActivity();

            state._fsp--;

             current =iv_ruleActivity; 
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
    // $ANTLR end "entryRuleActivity"


    // $ANTLR start "ruleActivity"
    // InternalActivityDiagram.g:466:1: ruleActivity returns [EObject current=null] : ( () otherlv_1= 'Activity' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'description' otherlv_5= ':' ( (lv_description_6_0= ruleEString ) ) )? (otherlv_7= 'inputData' otherlv_8= '{' ( ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_12= '}' )? ( (otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )? ) | (otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')' ) | (otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) ) ) ) ( (otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}' )? | (otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) ) ) ) (otherlv_43= 'time' otherlv_44= ':' ( (lv_time_45_0= ruleEFloat ) ) ( (lv_unit_46_0= ruleUnitTime ) ) )? (otherlv_47= 'interruptedBy' otherlv_48= '(' ( ( ruleQualifiedName ) ) (otherlv_50= ',' ( ( ruleQualifiedName ) ) )* otherlv_52= ')' )? (otherlv_53= 'interrupts' otherlv_54= '(' ( ( ruleQualifiedName ) ) (otherlv_56= ',' ( ( ruleQualifiedName ) ) )* otherlv_58= ')' )? otherlv_59= '}' ) ;
    public final EObject ruleActivity() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_12=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token lv_requiredCapability_15_0=null;
        Token otherlv_16=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        Token otherlv_21=null;
        Token otherlv_22=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token otherlv_27=null;
        Token otherlv_28=null;
        Token otherlv_29=null;
        Token otherlv_31=null;
        Token otherlv_32=null;
        Token otherlv_34=null;
        Token otherlv_36=null;
        Token otherlv_37=null;
        Token otherlv_38=null;
        Token otherlv_40=null;
        Token otherlv_41=null;
        Token otherlv_43=null;
        Token otherlv_44=null;
        Token otherlv_47=null;
        Token otherlv_48=null;
        Token otherlv_50=null;
        Token otherlv_52=null;
        Token otherlv_53=null;
        Token otherlv_54=null;
        Token otherlv_56=null;
        Token otherlv_58=null;
        Token otherlv_59=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        AntlrDatatypeRuleToken lv_description_6_0 = null;

        EObject lv_conditionalActivity_33_0 = null;

        EObject lv_conditionalActivity_35_0 = null;

        AntlrDatatypeRuleToken lv_time_45_0 = null;

        Enumerator lv_unit_46_0 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:472:2: ( ( () otherlv_1= 'Activity' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'description' otherlv_5= ':' ( (lv_description_6_0= ruleEString ) ) )? (otherlv_7= 'inputData' otherlv_8= '{' ( ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_12= '}' )? ( (otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )? ) | (otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')' ) | (otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) ) ) ) ( (otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}' )? | (otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) ) ) ) (otherlv_43= 'time' otherlv_44= ':' ( (lv_time_45_0= ruleEFloat ) ) ( (lv_unit_46_0= ruleUnitTime ) ) )? (otherlv_47= 'interruptedBy' otherlv_48= '(' ( ( ruleQualifiedName ) ) (otherlv_50= ',' ( ( ruleQualifiedName ) ) )* otherlv_52= ')' )? (otherlv_53= 'interrupts' otherlv_54= '(' ( ( ruleQualifiedName ) ) (otherlv_56= ',' ( ( ruleQualifiedName ) ) )* otherlv_58= ')' )? otherlv_59= '}' ) )
            // InternalActivityDiagram.g:473:2: ( () otherlv_1= 'Activity' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'description' otherlv_5= ':' ( (lv_description_6_0= ruleEString ) ) )? (otherlv_7= 'inputData' otherlv_8= '{' ( ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_12= '}' )? ( (otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )? ) | (otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')' ) | (otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) ) ) ) ( (otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}' )? | (otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) ) ) ) (otherlv_43= 'time' otherlv_44= ':' ( (lv_time_45_0= ruleEFloat ) ) ( (lv_unit_46_0= ruleUnitTime ) ) )? (otherlv_47= 'interruptedBy' otherlv_48= '(' ( ( ruleQualifiedName ) ) (otherlv_50= ',' ( ( ruleQualifiedName ) ) )* otherlv_52= ')' )? (otherlv_53= 'interrupts' otherlv_54= '(' ( ( ruleQualifiedName ) ) (otherlv_56= ',' ( ( ruleQualifiedName ) ) )* otherlv_58= ')' )? otherlv_59= '}' )
            {
            // InternalActivityDiagram.g:473:2: ( () otherlv_1= 'Activity' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'description' otherlv_5= ':' ( (lv_description_6_0= ruleEString ) ) )? (otherlv_7= 'inputData' otherlv_8= '{' ( ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_12= '}' )? ( (otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )? ) | (otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')' ) | (otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) ) ) ) ( (otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}' )? | (otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) ) ) ) (otherlv_43= 'time' otherlv_44= ':' ( (lv_time_45_0= ruleEFloat ) ) ( (lv_unit_46_0= ruleUnitTime ) ) )? (otherlv_47= 'interruptedBy' otherlv_48= '(' ( ( ruleQualifiedName ) ) (otherlv_50= ',' ( ( ruleQualifiedName ) ) )* otherlv_52= ')' )? (otherlv_53= 'interrupts' otherlv_54= '(' ( ( ruleQualifiedName ) ) (otherlv_56= ',' ( ( ruleQualifiedName ) ) )* otherlv_58= ')' )? otherlv_59= '}' )
            // InternalActivityDiagram.g:474:3: () otherlv_1= 'Activity' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'description' otherlv_5= ':' ( (lv_description_6_0= ruleEString ) ) )? (otherlv_7= 'inputData' otherlv_8= '{' ( ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_12= '}' )? ( (otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )? ) | (otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')' ) | (otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) ) ) ) ( (otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}' )? | (otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) ) ) ) (otherlv_43= 'time' otherlv_44= ':' ( (lv_time_45_0= ruleEFloat ) ) ( (lv_unit_46_0= ruleUnitTime ) ) )? (otherlv_47= 'interruptedBy' otherlv_48= '(' ( ( ruleQualifiedName ) ) (otherlv_50= ',' ( ( ruleQualifiedName ) ) )* otherlv_52= ')' )? (otherlv_53= 'interrupts' otherlv_54= '(' ( ( ruleQualifiedName ) ) (otherlv_56= ',' ( ( ruleQualifiedName ) ) )* otherlv_58= ')' )? otherlv_59= '}'
            {
            // InternalActivityDiagram.g:474:3: ()
            // InternalActivityDiagram.g:475:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getActivityAccess().getActivityAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,29,FOLLOW_3); 

            			newLeafNode(otherlv_1, grammarAccess.getActivityAccess().getActivityKeyword_1());
            		
            // InternalActivityDiagram.g:485:3: ( (lv_name_2_0= ruleEString ) )
            // InternalActivityDiagram.g:486:4: (lv_name_2_0= ruleEString )
            {
            // InternalActivityDiagram.g:486:4: (lv_name_2_0= ruleEString )
            // InternalActivityDiagram.g:487:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getActivityAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_22);
            lv_name_2_0=ruleEString();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getActivityRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"com.dml.dsl.Dml.EString");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_3=(Token)match(input,27,FOLLOW_25); 

            			newLeafNode(otherlv_3, grammarAccess.getActivityAccess().getLeftCurlyBracketKeyword_3());
            		
            // InternalActivityDiagram.g:508:3: (otherlv_4= 'description' otherlv_5= ':' ( (lv_description_6_0= ruleEString ) ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==30) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // InternalActivityDiagram.g:509:4: otherlv_4= 'description' otherlv_5= ':' ( (lv_description_6_0= ruleEString ) )
                    {
                    otherlv_4=(Token)match(input,30,FOLLOW_26); 

                    				newLeafNode(otherlv_4, grammarAccess.getActivityAccess().getDescriptionKeyword_4_0());
                    			
                    otherlv_5=(Token)match(input,31,FOLLOW_3); 

                    				newLeafNode(otherlv_5, grammarAccess.getActivityAccess().getColonKeyword_4_1());
                    			
                    // InternalActivityDiagram.g:517:4: ( (lv_description_6_0= ruleEString ) )
                    // InternalActivityDiagram.g:518:5: (lv_description_6_0= ruleEString )
                    {
                    // InternalActivityDiagram.g:518:5: (lv_description_6_0= ruleEString )
                    // InternalActivityDiagram.g:519:6: lv_description_6_0= ruleEString
                    {

                    						newCompositeNode(grammarAccess.getActivityAccess().getDescriptionEStringParserRuleCall_4_2_0());
                    					
                    pushFollow(FOLLOW_27);
                    lv_description_6_0=ruleEString();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getActivityRule());
                    						}
                    						set(
                    							current,
                    							"description",
                    							lv_description_6_0,
                    							"com.dml.dsl.Dml.EString");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            // InternalActivityDiagram.g:537:3: (otherlv_7= 'inputData' otherlv_8= '{' ( ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_12= '}' )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==32) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalActivityDiagram.g:538:4: otherlv_7= 'inputData' otherlv_8= '{' ( ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_12= '}'
                    {
                    otherlv_7=(Token)match(input,32,FOLLOW_22); 

                    				newLeafNode(otherlv_7, grammarAccess.getActivityAccess().getInputDataKeyword_5_0());
                    			
                    otherlv_8=(Token)match(input,27,FOLLOW_9); 

                    				newLeafNode(otherlv_8, grammarAccess.getActivityAccess().getLeftCurlyBracketKeyword_5_1());
                    			
                    // InternalActivityDiagram.g:546:4: ( ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* )
                    // InternalActivityDiagram.g:547:5: ( ( ruleQualifiedName ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )*
                    {
                    // InternalActivityDiagram.g:547:5: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:548:6: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:548:6: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:549:7: ruleQualifiedName
                    {

                    							if (current==null) {
                    								current = createModelElement(grammarAccess.getActivityRule());
                    							}
                    						

                    							newCompositeNode(grammarAccess.getActivityAccess().getInputParametersParameterCrossReference_5_2_0_0());
                    						
                    pushFollow(FOLLOW_24);
                    ruleQualifiedName();

                    state._fsp--;


                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalActivityDiagram.g:563:5: (otherlv_10= ',' ( ( ruleQualifiedName ) ) )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==15) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // InternalActivityDiagram.g:564:6: otherlv_10= ',' ( ( ruleQualifiedName ) )
                    	    {
                    	    otherlv_10=(Token)match(input,15,FOLLOW_9); 

                    	    						newLeafNode(otherlv_10, grammarAccess.getActivityAccess().getCommaKeyword_5_2_1_0());
                    	    					
                    	    // InternalActivityDiagram.g:568:6: ( ( ruleQualifiedName ) )
                    	    // InternalActivityDiagram.g:569:7: ( ruleQualifiedName )
                    	    {
                    	    // InternalActivityDiagram.g:569:7: ( ruleQualifiedName )
                    	    // InternalActivityDiagram.g:570:8: ruleQualifiedName
                    	    {

                    	    								if (current==null) {
                    	    									current = createModelElement(grammarAccess.getActivityRule());
                    	    								}
                    	    							

                    	    								newCompositeNode(grammarAccess.getActivityAccess().getInputParametersParameterCrossReference_5_2_1_1_0());
                    	    							
                    	    pushFollow(FOLLOW_24);
                    	    ruleQualifiedName();

                    	    state._fsp--;


                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }

                    otherlv_12=(Token)match(input,28,FOLLOW_28); 

                    				newLeafNode(otherlv_12, grammarAccess.getActivityAccess().getRightCurlyBracketKeyword_5_3());
                    			

                    }
                    break;

            }

            // InternalActivityDiagram.g:591:3: ( (otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )? ) | (otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')' ) | (otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) ) ) )
            int alt22=3;
            switch ( input.LA(1) ) {
            case 33:
                {
                alt22=1;
                }
                break;
            case 34:
                {
                alt22=2;
                }
                break;
            case 35:
                {
                alt22=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // InternalActivityDiagram.g:592:4: (otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )? )
                    {
                    // InternalActivityDiagram.g:592:4: (otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )? )
                    // InternalActivityDiagram.g:593:5: otherlv_13= 'requireCapability' otherlv_14= ':' ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) ) (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )?
                    {
                    otherlv_13=(Token)match(input,33,FOLLOW_26); 

                    					newLeafNode(otherlv_13, grammarAccess.getActivityAccess().getRequireCapabilityKeyword_6_0_0());
                    				
                    otherlv_14=(Token)match(input,31,FOLLOW_3); 

                    					newLeafNode(otherlv_14, grammarAccess.getActivityAccess().getColonKeyword_6_0_1());
                    				
                    // InternalActivityDiagram.g:601:5: ( ( (lv_requiredCapability_15_0= RULE_STRING ) ) | ( (otherlv_16= RULE_ID ) ) )
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==RULE_STRING) ) {
                        alt17=1;
                    }
                    else if ( (LA17_0==RULE_ID) ) {
                        alt17=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 17, 0, input);

                        throw nvae;
                    }
                    switch (alt17) {
                        case 1 :
                            // InternalActivityDiagram.g:602:6: ( (lv_requiredCapability_15_0= RULE_STRING ) )
                            {
                            // InternalActivityDiagram.g:602:6: ( (lv_requiredCapability_15_0= RULE_STRING ) )
                            // InternalActivityDiagram.g:603:7: (lv_requiredCapability_15_0= RULE_STRING )
                            {
                            // InternalActivityDiagram.g:603:7: (lv_requiredCapability_15_0= RULE_STRING )
                            // InternalActivityDiagram.g:604:8: lv_requiredCapability_15_0= RULE_STRING
                            {
                            lv_requiredCapability_15_0=(Token)match(input,RULE_STRING,FOLLOW_29); 

                            								newLeafNode(lv_requiredCapability_15_0, grammarAccess.getActivityAccess().getRequiredCapabilitySTRINGTerminalRuleCall_6_0_2_0_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getActivityRule());
                            								}
                            								setWithLastConsumed(
                            									current,
                            									"requiredCapability",
                            									lv_requiredCapability_15_0,
                            									"org.eclipse.xtext.common.Terminals.STRING");
                            							

                            }


                            }


                            }
                            break;
                        case 2 :
                            // InternalActivityDiagram.g:621:6: ( (otherlv_16= RULE_ID ) )
                            {
                            // InternalActivityDiagram.g:621:6: ( (otherlv_16= RULE_ID ) )
                            // InternalActivityDiagram.g:622:7: (otherlv_16= RULE_ID )
                            {
                            // InternalActivityDiagram.g:622:7: (otherlv_16= RULE_ID )
                            // InternalActivityDiagram.g:623:8: otherlv_16= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getActivityRule());
                            								}
                            							
                            otherlv_16=(Token)match(input,RULE_ID,FOLLOW_29); 

                            								newLeafNode(otherlv_16, grammarAccess.getActivityAccess().getBindCapabilityCapabilityCrossReference_6_0_2_1_0());
                            							

                            }


                            }


                            }
                            break;

                    }

                    // InternalActivityDiagram.g:635:5: (otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}' )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==27) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // InternalActivityDiagram.g:636:6: otherlv_17= '{' ( ( ruleQualifiedName ) ) (otherlv_19= ',' ( ( ruleQualifiedName ) ) )* otherlv_21= '}'
                            {
                            otherlv_17=(Token)match(input,27,FOLLOW_9); 

                            						newLeafNode(otherlv_17, grammarAccess.getActivityAccess().getLeftCurlyBracketKeyword_6_0_3_0());
                            					
                            // InternalActivityDiagram.g:640:6: ( ( ruleQualifiedName ) )
                            // InternalActivityDiagram.g:641:7: ( ruleQualifiedName )
                            {
                            // InternalActivityDiagram.g:641:7: ( ruleQualifiedName )
                            // InternalActivityDiagram.g:642:8: ruleQualifiedName
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getActivityRule());
                            								}
                            							

                            								newCompositeNode(grammarAccess.getActivityAccess().getUseControlCapabilitiesAbstractInterfaceItemsCrossReference_6_0_3_1_0());
                            							
                            pushFollow(FOLLOW_24);
                            ruleQualifiedName();

                            state._fsp--;


                            								afterParserOrEnumRuleCall();
                            							

                            }


                            }

                            // InternalActivityDiagram.g:656:6: (otherlv_19= ',' ( ( ruleQualifiedName ) ) )*
                            loop18:
                            do {
                                int alt18=2;
                                int LA18_0 = input.LA(1);

                                if ( (LA18_0==15) ) {
                                    alt18=1;
                                }


                                switch (alt18) {
                            	case 1 :
                            	    // InternalActivityDiagram.g:657:7: otherlv_19= ',' ( ( ruleQualifiedName ) )
                            	    {
                            	    otherlv_19=(Token)match(input,15,FOLLOW_9); 

                            	    							newLeafNode(otherlv_19, grammarAccess.getActivityAccess().getCommaKeyword_6_0_3_2_0());
                            	    						
                            	    // InternalActivityDiagram.g:661:7: ( ( ruleQualifiedName ) )
                            	    // InternalActivityDiagram.g:662:8: ( ruleQualifiedName )
                            	    {
                            	    // InternalActivityDiagram.g:662:8: ( ruleQualifiedName )
                            	    // InternalActivityDiagram.g:663:9: ruleQualifiedName
                            	    {

                            	    									if (current==null) {
                            	    										current = createModelElement(grammarAccess.getActivityRule());
                            	    									}
                            	    								

                            	    									newCompositeNode(grammarAccess.getActivityAccess().getUseControlCapabilitiesAbstractInterfaceItemsCrossReference_6_0_3_2_1_0());
                            	    								
                            	    pushFollow(FOLLOW_24);
                            	    ruleQualifiedName();

                            	    state._fsp--;


                            	    									afterParserOrEnumRuleCall();
                            	    								

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop18;
                                }
                            } while (true);

                            otherlv_21=(Token)match(input,28,FOLLOW_30); 

                            						newLeafNode(otherlv_21, grammarAccess.getActivityAccess().getRightCurlyBracketKeyword_6_0_3_3());
                            					

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalActivityDiagram.g:685:4: (otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')' )
                    {
                    // InternalActivityDiagram.g:685:4: (otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')' )
                    // InternalActivityDiagram.g:686:5: otherlv_22= 'requireOperation' otherlv_23= '(' ( ( ruleQualifiedName ) ) (otherlv_25= ',' ( ( ruleQualifiedName ) )* )? otherlv_27= ')'
                    {
                    otherlv_22=(Token)match(input,34,FOLLOW_17); 

                    					newLeafNode(otherlv_22, grammarAccess.getActivityAccess().getRequireOperationKeyword_6_1_0());
                    				
                    otherlv_23=(Token)match(input,23,FOLLOW_9); 

                    					newLeafNode(otherlv_23, grammarAccess.getActivityAccess().getLeftParenthesisKeyword_6_1_1());
                    				
                    // InternalActivityDiagram.g:694:5: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:695:6: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:695:6: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:696:7: ruleQualifiedName
                    {

                    							if (current==null) {
                    								current = createModelElement(grammarAccess.getActivityRule());
                    							}
                    						

                    							newCompositeNode(grammarAccess.getActivityAccess().getRequiresOperationOperationCrossReference_6_1_2_0());
                    						
                    pushFollow(FOLLOW_19);
                    ruleQualifiedName();

                    state._fsp--;


                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalActivityDiagram.g:710:5: (otherlv_25= ',' ( ( ruleQualifiedName ) )* )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==15) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // InternalActivityDiagram.g:711:6: otherlv_25= ',' ( ( ruleQualifiedName ) )*
                            {
                            otherlv_25=(Token)match(input,15,FOLLOW_31); 

                            						newLeafNode(otherlv_25, grammarAccess.getActivityAccess().getCommaKeyword_6_1_3_0());
                            					
                            // InternalActivityDiagram.g:715:6: ( ( ruleQualifiedName ) )*
                            loop20:
                            do {
                                int alt20=2;
                                int LA20_0 = input.LA(1);

                                if ( (LA20_0==RULE_ID) ) {
                                    alt20=1;
                                }


                                switch (alt20) {
                            	case 1 :
                            	    // InternalActivityDiagram.g:716:7: ( ruleQualifiedName )
                            	    {
                            	    // InternalActivityDiagram.g:716:7: ( ruleQualifiedName )
                            	    // InternalActivityDiagram.g:717:8: ruleQualifiedName
                            	    {

                            	    								if (current==null) {
                            	    									current = createModelElement(grammarAccess.getActivityRule());
                            	    								}
                            	    							

                            	    								newCompositeNode(grammarAccess.getActivityAccess().getRequiresOperationOperationCrossReference_6_1_3_1_0());
                            	    							
                            	    pushFollow(FOLLOW_31);
                            	    ruleQualifiedName();

                            	    state._fsp--;


                            	    								afterParserOrEnumRuleCall();
                            	    							

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

                    otherlv_27=(Token)match(input,24,FOLLOW_30); 

                    					newLeafNode(otherlv_27, grammarAccess.getActivityAccess().getRightParenthesisKeyword_6_1_4());
                    				

                    }


                    }
                    break;
                case 3 :
                    // InternalActivityDiagram.g:738:4: (otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) ) )
                    {
                    // InternalActivityDiagram.g:738:4: (otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) ) )
                    // InternalActivityDiagram.g:739:5: otherlv_28= 'childActivityDiagram' otherlv_29= ':' ( ( ruleQualifiedName ) )
                    {
                    otherlv_28=(Token)match(input,35,FOLLOW_26); 

                    					newLeafNode(otherlv_28, grammarAccess.getActivityAccess().getChildActivityDiagramKeyword_6_2_0());
                    				
                    otherlv_29=(Token)match(input,31,FOLLOW_9); 

                    					newLeafNode(otherlv_29, grammarAccess.getActivityAccess().getColonKeyword_6_2_1());
                    				
                    // InternalActivityDiagram.g:747:5: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:748:6: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:748:6: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:749:7: ruleQualifiedName
                    {

                    							if (current==null) {
                    								current = createModelElement(grammarAccess.getActivityRule());
                    							}
                    						

                    							newCompositeNode(grammarAccess.getActivityAccess().getChildActivityDiagramActivityDiagramCrossReference_6_2_2_0());
                    						
                    pushFollow(FOLLOW_30);
                    ruleQualifiedName();

                    state._fsp--;


                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }
                    break;

            }

            // InternalActivityDiagram.g:765:3: ( (otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}' )? | (otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) ) ) )
            int alt25=3;
            switch ( input.LA(1) ) {
            case 28:
            case 36:
            case 39:
            case 40:
            case 41:
                {
                alt25=1;
                }
                break;
            case 37:
                {
                alt25=2;
                }
                break;
            case 38:
                {
                alt25=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // InternalActivityDiagram.g:766:4: (otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}' )?
                    {
                    // InternalActivityDiagram.g:766:4: (otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}' )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==36) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // InternalActivityDiagram.g:767:5: otherlv_31= 'conditions' otherlv_32= '{' ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* ) otherlv_36= '}'
                            {
                            otherlv_31=(Token)match(input,36,FOLLOW_22); 

                            					newLeafNode(otherlv_31, grammarAccess.getActivityAccess().getConditionsKeyword_7_0_0());
                            				
                            otherlv_32=(Token)match(input,27,FOLLOW_32); 

                            					newLeafNode(otherlv_32, grammarAccess.getActivityAccess().getLeftCurlyBracketKeyword_7_0_1());
                            				
                            // InternalActivityDiagram.g:775:5: ( ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )* )
                            // InternalActivityDiagram.g:776:6: ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) ) (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )*
                            {
                            // InternalActivityDiagram.g:776:6: ( (lv_conditionalActivity_33_0= ruleConditionalActivity ) )
                            // InternalActivityDiagram.g:777:7: (lv_conditionalActivity_33_0= ruleConditionalActivity )
                            {
                            // InternalActivityDiagram.g:777:7: (lv_conditionalActivity_33_0= ruleConditionalActivity )
                            // InternalActivityDiagram.g:778:8: lv_conditionalActivity_33_0= ruleConditionalActivity
                            {

                            								newCompositeNode(grammarAccess.getActivityAccess().getConditionalActivityConditionalActivityParserRuleCall_7_0_2_0_0());
                            							
                            pushFollow(FOLLOW_24);
                            lv_conditionalActivity_33_0=ruleConditionalActivity();

                            state._fsp--;


                            								if (current==null) {
                            									current = createModelElementForParent(grammarAccess.getActivityRule());
                            								}
                            								add(
                            									current,
                            									"conditionalActivity",
                            									lv_conditionalActivity_33_0,
                            									"com.smr.activity.dsl.ActivityDiagram.ConditionalActivity");
                            								afterParserOrEnumRuleCall();
                            							

                            }


                            }

                            // InternalActivityDiagram.g:795:6: (otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) ) )*
                            loop23:
                            do {
                                int alt23=2;
                                int LA23_0 = input.LA(1);

                                if ( (LA23_0==15) ) {
                                    alt23=1;
                                }


                                switch (alt23) {
                            	case 1 :
                            	    // InternalActivityDiagram.g:796:7: otherlv_34= ',' ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) )
                            	    {
                            	    otherlv_34=(Token)match(input,15,FOLLOW_32); 

                            	    							newLeafNode(otherlv_34, grammarAccess.getActivityAccess().getCommaKeyword_7_0_2_1_0());
                            	    						
                            	    // InternalActivityDiagram.g:800:7: ( (lv_conditionalActivity_35_0= ruleConditionalActivity ) )
                            	    // InternalActivityDiagram.g:801:8: (lv_conditionalActivity_35_0= ruleConditionalActivity )
                            	    {
                            	    // InternalActivityDiagram.g:801:8: (lv_conditionalActivity_35_0= ruleConditionalActivity )
                            	    // InternalActivityDiagram.g:802:9: lv_conditionalActivity_35_0= ruleConditionalActivity
                            	    {

                            	    									newCompositeNode(grammarAccess.getActivityAccess().getConditionalActivityConditionalActivityParserRuleCall_7_0_2_1_1_0());
                            	    								
                            	    pushFollow(FOLLOW_24);
                            	    lv_conditionalActivity_35_0=ruleConditionalActivity();

                            	    state._fsp--;


                            	    									if (current==null) {
                            	    										current = createModelElementForParent(grammarAccess.getActivityRule());
                            	    									}
                            	    									add(
                            	    										current,
                            	    										"conditionalActivity",
                            	    										lv_conditionalActivity_35_0,
                            	    										"com.smr.activity.dsl.ActivityDiagram.ConditionalActivity");
                            	    									afterParserOrEnumRuleCall();
                            	    								

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop23;
                                }
                            } while (true);


                            }

                            otherlv_36=(Token)match(input,28,FOLLOW_33); 

                            					newLeafNode(otherlv_36, grammarAccess.getActivityAccess().getRightCurlyBracketKeyword_7_0_3());
                            				

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // InternalActivityDiagram.g:827:4: (otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) ) )
                    {
                    // InternalActivityDiagram.g:827:4: (otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) ) )
                    // InternalActivityDiagram.g:828:5: otherlv_37= 'nextActivity' otherlv_38= ':' ( ( ruleQualifiedName ) )
                    {
                    otherlv_37=(Token)match(input,37,FOLLOW_26); 

                    					newLeafNode(otherlv_37, grammarAccess.getActivityAccess().getNextActivityKeyword_7_1_0());
                    				
                    otherlv_38=(Token)match(input,31,FOLLOW_9); 

                    					newLeafNode(otherlv_38, grammarAccess.getActivityAccess().getColonKeyword_7_1_1());
                    				
                    // InternalActivityDiagram.g:836:5: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:837:6: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:837:6: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:838:7: ruleQualifiedName
                    {

                    							if (current==null) {
                    								current = createModelElement(grammarAccess.getActivityRule());
                    							}
                    						

                    							newCompositeNode(grammarAccess.getActivityAccess().getNextActivityActivityCrossReference_7_1_2_0());
                    						
                    pushFollow(FOLLOW_33);
                    ruleQualifiedName();

                    state._fsp--;


                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalActivityDiagram.g:854:4: (otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) ) )
                    {
                    // InternalActivityDiagram.g:854:4: (otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) ) )
                    // InternalActivityDiagram.g:855:5: otherlv_40= 'nextActivityDiagram' otherlv_41= ':' ( ( ruleQualifiedName ) )
                    {
                    otherlv_40=(Token)match(input,38,FOLLOW_26); 

                    					newLeafNode(otherlv_40, grammarAccess.getActivityAccess().getNextActivityDiagramKeyword_7_2_0());
                    				
                    otherlv_41=(Token)match(input,31,FOLLOW_9); 

                    					newLeafNode(otherlv_41, grammarAccess.getActivityAccess().getColonKeyword_7_2_1());
                    				
                    // InternalActivityDiagram.g:863:5: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:864:6: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:864:6: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:865:7: ruleQualifiedName
                    {

                    							if (current==null) {
                    								current = createModelElement(grammarAccess.getActivityRule());
                    							}
                    						

                    							newCompositeNode(grammarAccess.getActivityAccess().getNextActivityDiagramActivityDiagramCrossReference_7_2_2_0());
                    						
                    pushFollow(FOLLOW_33);
                    ruleQualifiedName();

                    state._fsp--;


                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }
                    break;

            }

            // InternalActivityDiagram.g:881:3: (otherlv_43= 'time' otherlv_44= ':' ( (lv_time_45_0= ruleEFloat ) ) ( (lv_unit_46_0= ruleUnitTime ) ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==39) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // InternalActivityDiagram.g:882:4: otherlv_43= 'time' otherlv_44= ':' ( (lv_time_45_0= ruleEFloat ) ) ( (lv_unit_46_0= ruleUnitTime ) )
                    {
                    otherlv_43=(Token)match(input,39,FOLLOW_26); 

                    				newLeafNode(otherlv_43, grammarAccess.getActivityAccess().getTimeKeyword_8_0());
                    			
                    otherlv_44=(Token)match(input,31,FOLLOW_34); 

                    				newLeafNode(otherlv_44, grammarAccess.getActivityAccess().getColonKeyword_8_1());
                    			
                    // InternalActivityDiagram.g:890:4: ( (lv_time_45_0= ruleEFloat ) )
                    // InternalActivityDiagram.g:891:5: (lv_time_45_0= ruleEFloat )
                    {
                    // InternalActivityDiagram.g:891:5: (lv_time_45_0= ruleEFloat )
                    // InternalActivityDiagram.g:892:6: lv_time_45_0= ruleEFloat
                    {

                    						newCompositeNode(grammarAccess.getActivityAccess().getTimeEFloatParserRuleCall_8_2_0());
                    					
                    pushFollow(FOLLOW_35);
                    lv_time_45_0=ruleEFloat();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getActivityRule());
                    						}
                    						set(
                    							current,
                    							"time",
                    							lv_time_45_0,
                    							"com.dml.dsl.Dml.EFloat");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalActivityDiagram.g:909:4: ( (lv_unit_46_0= ruleUnitTime ) )
                    // InternalActivityDiagram.g:910:5: (lv_unit_46_0= ruleUnitTime )
                    {
                    // InternalActivityDiagram.g:910:5: (lv_unit_46_0= ruleUnitTime )
                    // InternalActivityDiagram.g:911:6: lv_unit_46_0= ruleUnitTime
                    {

                    						newCompositeNode(grammarAccess.getActivityAccess().getUnitUnitTimeEnumRuleCall_8_3_0());
                    					
                    pushFollow(FOLLOW_36);
                    lv_unit_46_0=ruleUnitTime();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getActivityRule());
                    						}
                    						set(
                    							current,
                    							"unit",
                    							lv_unit_46_0,
                    							"com.smr.activity.dsl.ActivityDiagram.UnitTime");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            // InternalActivityDiagram.g:929:3: (otherlv_47= 'interruptedBy' otherlv_48= '(' ( ( ruleQualifiedName ) ) (otherlv_50= ',' ( ( ruleQualifiedName ) ) )* otherlv_52= ')' )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==40) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // InternalActivityDiagram.g:930:4: otherlv_47= 'interruptedBy' otherlv_48= '(' ( ( ruleQualifiedName ) ) (otherlv_50= ',' ( ( ruleQualifiedName ) ) )* otherlv_52= ')'
                    {
                    otherlv_47=(Token)match(input,40,FOLLOW_17); 

                    				newLeafNode(otherlv_47, grammarAccess.getActivityAccess().getInterruptedByKeyword_9_0());
                    			
                    otherlv_48=(Token)match(input,23,FOLLOW_9); 

                    				newLeafNode(otherlv_48, grammarAccess.getActivityAccess().getLeftParenthesisKeyword_9_1());
                    			
                    // InternalActivityDiagram.g:938:4: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:939:5: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:939:5: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:940:6: ruleQualifiedName
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getActivityRule());
                    						}
                    					

                    						newCompositeNode(grammarAccess.getActivityAccess().getInterruptedByActivityCrossReference_9_2_0());
                    					
                    pushFollow(FOLLOW_19);
                    ruleQualifiedName();

                    state._fsp--;


                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalActivityDiagram.g:954:4: (otherlv_50= ',' ( ( ruleQualifiedName ) ) )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==15) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // InternalActivityDiagram.g:955:5: otherlv_50= ',' ( ( ruleQualifiedName ) )
                    	    {
                    	    otherlv_50=(Token)match(input,15,FOLLOW_9); 

                    	    					newLeafNode(otherlv_50, grammarAccess.getActivityAccess().getCommaKeyword_9_3_0());
                    	    				
                    	    // InternalActivityDiagram.g:959:5: ( ( ruleQualifiedName ) )
                    	    // InternalActivityDiagram.g:960:6: ( ruleQualifiedName )
                    	    {
                    	    // InternalActivityDiagram.g:960:6: ( ruleQualifiedName )
                    	    // InternalActivityDiagram.g:961:7: ruleQualifiedName
                    	    {

                    	    							if (current==null) {
                    	    								current = createModelElement(grammarAccess.getActivityRule());
                    	    							}
                    	    						

                    	    							newCompositeNode(grammarAccess.getActivityAccess().getInterruptedByActivityCrossReference_9_3_1_0());
                    	    						
                    	    pushFollow(FOLLOW_19);
                    	    ruleQualifiedName();

                    	    state._fsp--;


                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop27;
                        }
                    } while (true);

                    otherlv_52=(Token)match(input,24,FOLLOW_37); 

                    				newLeafNode(otherlv_52, grammarAccess.getActivityAccess().getRightParenthesisKeyword_9_4());
                    			

                    }
                    break;

            }

            // InternalActivityDiagram.g:981:3: (otherlv_53= 'interrupts' otherlv_54= '(' ( ( ruleQualifiedName ) ) (otherlv_56= ',' ( ( ruleQualifiedName ) ) )* otherlv_58= ')' )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==41) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // InternalActivityDiagram.g:982:4: otherlv_53= 'interrupts' otherlv_54= '(' ( ( ruleQualifiedName ) ) (otherlv_56= ',' ( ( ruleQualifiedName ) ) )* otherlv_58= ')'
                    {
                    otherlv_53=(Token)match(input,41,FOLLOW_17); 

                    				newLeafNode(otherlv_53, grammarAccess.getActivityAccess().getInterruptsKeyword_10_0());
                    			
                    otherlv_54=(Token)match(input,23,FOLLOW_9); 

                    				newLeafNode(otherlv_54, grammarAccess.getActivityAccess().getLeftParenthesisKeyword_10_1());
                    			
                    // InternalActivityDiagram.g:990:4: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:991:5: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:991:5: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:992:6: ruleQualifiedName
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getActivityRule());
                    						}
                    					

                    						newCompositeNode(grammarAccess.getActivityAccess().getInterruptsActivityCrossReference_10_2_0());
                    					
                    pushFollow(FOLLOW_19);
                    ruleQualifiedName();

                    state._fsp--;


                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalActivityDiagram.g:1006:4: (otherlv_56= ',' ( ( ruleQualifiedName ) ) )*
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==15) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // InternalActivityDiagram.g:1007:5: otherlv_56= ',' ( ( ruleQualifiedName ) )
                    	    {
                    	    otherlv_56=(Token)match(input,15,FOLLOW_9); 

                    	    					newLeafNode(otherlv_56, grammarAccess.getActivityAccess().getCommaKeyword_10_3_0());
                    	    				
                    	    // InternalActivityDiagram.g:1011:5: ( ( ruleQualifiedName ) )
                    	    // InternalActivityDiagram.g:1012:6: ( ruleQualifiedName )
                    	    {
                    	    // InternalActivityDiagram.g:1012:6: ( ruleQualifiedName )
                    	    // InternalActivityDiagram.g:1013:7: ruleQualifiedName
                    	    {

                    	    							if (current==null) {
                    	    								current = createModelElement(grammarAccess.getActivityRule());
                    	    							}
                    	    						

                    	    							newCompositeNode(grammarAccess.getActivityAccess().getInterruptsActivityCrossReference_10_3_1_0());
                    	    						
                    	    pushFollow(FOLLOW_19);
                    	    ruleQualifiedName();

                    	    state._fsp--;


                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop29;
                        }
                    } while (true);

                    otherlv_58=(Token)match(input,24,FOLLOW_38); 

                    				newLeafNode(otherlv_58, grammarAccess.getActivityAccess().getRightParenthesisKeyword_10_4());
                    			

                    }
                    break;

            }

            otherlv_59=(Token)match(input,28,FOLLOW_2); 

            			newLeafNode(otherlv_59, grammarAccess.getActivityAccess().getRightCurlyBracketKeyword_11());
            		

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
    // $ANTLR end "ruleActivity"


    // $ANTLR start "entryRuleConditionalActivity"
    // InternalActivityDiagram.g:1041:1: entryRuleConditionalActivity returns [EObject current=null] : iv_ruleConditionalActivity= ruleConditionalActivity EOF ;
    public final EObject entryRuleConditionalActivity() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConditionalActivity = null;


        try {
            // InternalActivityDiagram.g:1041:60: (iv_ruleConditionalActivity= ruleConditionalActivity EOF )
            // InternalActivityDiagram.g:1042:2: iv_ruleConditionalActivity= ruleConditionalActivity EOF
            {
             newCompositeNode(grammarAccess.getConditionalActivityRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleConditionalActivity=ruleConditionalActivity();

            state._fsp--;

             current =iv_ruleConditionalActivity; 
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
    // $ANTLR end "entryRuleConditionalActivity"


    // $ANTLR start "ruleConditionalActivity"
    // InternalActivityDiagram.g:1048:1: ruleConditionalActivity returns [EObject current=null] : ( () ( ( ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )* ) ( (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) ) ) ) ) ;
    public final EObject ruleConditionalActivity() throws RecognitionException {
        EObject current = null;

        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        EObject lv_outcome_1_0 = null;

        AntlrDatatypeRuleToken lv_bOp_2_0 = null;

        EObject lv_outcome_3_0 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:1054:2: ( ( () ( ( ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )* ) ( (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) ) ) ) ) )
            // InternalActivityDiagram.g:1055:2: ( () ( ( ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )* ) ( (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) ) ) ) )
            {
            // InternalActivityDiagram.g:1055:2: ( () ( ( ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )* ) ( (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) ) ) ) )
            // InternalActivityDiagram.g:1056:3: () ( ( ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )* ) ( (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) ) ) )
            {
            // InternalActivityDiagram.g:1056:3: ()
            // InternalActivityDiagram.g:1057:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getConditionalActivityAccess().getConditionalActivityAction_0(),
            					current);
            			

            }

            // InternalActivityDiagram.g:1063:3: ( ( ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )* ) ( (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) ) ) )
            // InternalActivityDiagram.g:1064:4: ( ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )* ) ( (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) ) )
            {
            // InternalActivityDiagram.g:1064:4: ( ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )* )
            // InternalActivityDiagram.g:1065:5: ( (lv_outcome_1_0= ruleOutcome ) ) ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )*
            {
            // InternalActivityDiagram.g:1065:5: ( (lv_outcome_1_0= ruleOutcome ) )
            // InternalActivityDiagram.g:1066:6: (lv_outcome_1_0= ruleOutcome )
            {
            // InternalActivityDiagram.g:1066:6: (lv_outcome_1_0= ruleOutcome )
            // InternalActivityDiagram.g:1067:7: lv_outcome_1_0= ruleOutcome
            {

            							newCompositeNode(grammarAccess.getConditionalActivityAccess().getOutcomeOutcomeParserRuleCall_1_0_0_0());
            						
            pushFollow(FOLLOW_39);
            lv_outcome_1_0=ruleOutcome();

            state._fsp--;


            							if (current==null) {
            								current = createModelElementForParent(grammarAccess.getConditionalActivityRule());
            							}
            							add(
            								current,
            								"outcome",
            								lv_outcome_1_0,
            								"com.smr.activity.dsl.ActivityDiagram.Outcome");
            							afterParserOrEnumRuleCall();
            						

            }


            }

            // InternalActivityDiagram.g:1084:5: ( ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) ) )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=45 && LA31_0<=46)) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // InternalActivityDiagram.g:1085:6: ( (lv_bOp_2_0= ruleBooleanOp ) ) ( (lv_outcome_3_0= ruleOutcome ) )
            	    {
            	    // InternalActivityDiagram.g:1085:6: ( (lv_bOp_2_0= ruleBooleanOp ) )
            	    // InternalActivityDiagram.g:1086:7: (lv_bOp_2_0= ruleBooleanOp )
            	    {
            	    // InternalActivityDiagram.g:1086:7: (lv_bOp_2_0= ruleBooleanOp )
            	    // InternalActivityDiagram.g:1087:8: lv_bOp_2_0= ruleBooleanOp
            	    {

            	    								newCompositeNode(grammarAccess.getConditionalActivityAccess().getBOpBooleanOpParserRuleCall_1_0_1_0_0());
            	    							
            	    pushFollow(FOLLOW_32);
            	    lv_bOp_2_0=ruleBooleanOp();

            	    state._fsp--;


            	    								if (current==null) {
            	    									current = createModelElementForParent(grammarAccess.getConditionalActivityRule());
            	    								}
            	    								add(
            	    									current,
            	    									"bOp",
            	    									lv_bOp_2_0,
            	    									"com.smr.activity.dsl.ActivityDiagram.BooleanOp");
            	    								afterParserOrEnumRuleCall();
            	    							

            	    }


            	    }

            	    // InternalActivityDiagram.g:1104:6: ( (lv_outcome_3_0= ruleOutcome ) )
            	    // InternalActivityDiagram.g:1105:7: (lv_outcome_3_0= ruleOutcome )
            	    {
            	    // InternalActivityDiagram.g:1105:7: (lv_outcome_3_0= ruleOutcome )
            	    // InternalActivityDiagram.g:1106:8: lv_outcome_3_0= ruleOutcome
            	    {

            	    								newCompositeNode(grammarAccess.getConditionalActivityAccess().getOutcomeOutcomeParserRuleCall_1_0_1_1_0());
            	    							
            	    pushFollow(FOLLOW_39);
            	    lv_outcome_3_0=ruleOutcome();

            	    state._fsp--;


            	    								if (current==null) {
            	    									current = createModelElementForParent(grammarAccess.getConditionalActivityRule());
            	    								}
            	    								add(
            	    									current,
            	    									"outcome",
            	    									lv_outcome_3_0,
            	    									"com.smr.activity.dsl.ActivityDiagram.Outcome");
            	    								afterParserOrEnumRuleCall();
            	    							

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);


            }

            // InternalActivityDiagram.g:1125:4: ( (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) ) | (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) ) )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==42) ) {
                alt32=1;
            }
            else if ( (LA32_0==43) ) {
                alt32=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // InternalActivityDiagram.g:1126:5: (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) )
                    {
                    // InternalActivityDiagram.g:1126:5: (otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) ) )
                    // InternalActivityDiagram.g:1127:6: otherlv_4= '=>' otherlv_5= 'nextActivity' otherlv_6= ':' ( ( ruleQualifiedName ) )
                    {
                    otherlv_4=(Token)match(input,42,FOLLOW_40); 

                    						newLeafNode(otherlv_4, grammarAccess.getConditionalActivityAccess().getEqualsSignGreaterThanSignKeyword_1_1_0_0());
                    					
                    otherlv_5=(Token)match(input,37,FOLLOW_26); 

                    						newLeafNode(otherlv_5, grammarAccess.getConditionalActivityAccess().getNextActivityKeyword_1_1_0_1());
                    					
                    otherlv_6=(Token)match(input,31,FOLLOW_9); 

                    						newLeafNode(otherlv_6, grammarAccess.getConditionalActivityAccess().getColonKeyword_1_1_0_2());
                    					
                    // InternalActivityDiagram.g:1139:6: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:1140:7: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:1140:7: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:1141:8: ruleQualifiedName
                    {

                    								if (current==null) {
                    									current = createModelElement(grammarAccess.getConditionalActivityRule());
                    								}
                    							

                    								newCompositeNode(grammarAccess.getConditionalActivityAccess().getOnTrueNextActivityActivityCrossReference_1_1_0_3_0());
                    							
                    pushFollow(FOLLOW_2);
                    ruleQualifiedName();

                    state._fsp--;


                    								afterParserOrEnumRuleCall();
                    							

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalActivityDiagram.g:1157:5: (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) )
                    {
                    // InternalActivityDiagram.g:1157:5: (otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) ) )
                    // InternalActivityDiagram.g:1158:6: otherlv_8= 'final' otherlv_9= 'result' otherlv_10= ':' ( (otherlv_11= RULE_ID ) )
                    {
                    otherlv_8=(Token)match(input,43,FOLLOW_41); 

                    						newLeafNode(otherlv_8, grammarAccess.getConditionalActivityAccess().getFinalKeyword_1_1_1_0());
                    					
                    otherlv_9=(Token)match(input,44,FOLLOW_26); 

                    						newLeafNode(otherlv_9, grammarAccess.getConditionalActivityAccess().getResultKeyword_1_1_1_1());
                    					
                    otherlv_10=(Token)match(input,31,FOLLOW_9); 

                    						newLeafNode(otherlv_10, grammarAccess.getConditionalActivityAccess().getColonKeyword_1_1_1_2());
                    					
                    // InternalActivityDiagram.g:1170:6: ( (otherlv_11= RULE_ID ) )
                    // InternalActivityDiagram.g:1171:7: (otherlv_11= RULE_ID )
                    {
                    // InternalActivityDiagram.g:1171:7: (otherlv_11= RULE_ID )
                    // InternalActivityDiagram.g:1172:8: otherlv_11= RULE_ID
                    {

                    								if (current==null) {
                    									current = createModelElement(grammarAccess.getConditionalActivityRule());
                    								}
                    							
                    otherlv_11=(Token)match(input,RULE_ID,FOLLOW_2); 

                    								newLeafNode(otherlv_11, grammarAccess.getConditionalActivityAccess().getOnTrueFinalResultParameterCrossReference_1_1_1_3_0());
                    							

                    }


                    }


                    }


                    }
                    break;

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
    // $ANTLR end "ruleConditionalActivity"


    // $ANTLR start "entryRuleBooleanOp"
    // InternalActivityDiagram.g:1190:1: entryRuleBooleanOp returns [String current=null] : iv_ruleBooleanOp= ruleBooleanOp EOF ;
    public final String entryRuleBooleanOp() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBooleanOp = null;


        try {
            // InternalActivityDiagram.g:1190:49: (iv_ruleBooleanOp= ruleBooleanOp EOF )
            // InternalActivityDiagram.g:1191:2: iv_ruleBooleanOp= ruleBooleanOp EOF
            {
             newCompositeNode(grammarAccess.getBooleanOpRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleBooleanOp=ruleBooleanOp();

            state._fsp--;

             current =iv_ruleBooleanOp.getText(); 
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
    // $ANTLR end "entryRuleBooleanOp"


    // $ANTLR start "ruleBooleanOp"
    // InternalActivityDiagram.g:1197:1: ruleBooleanOp returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'and' | kw= 'or' ) ;
    public final AntlrDatatypeRuleToken ruleBooleanOp() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:1203:2: ( (kw= 'and' | kw= 'or' ) )
            // InternalActivityDiagram.g:1204:2: (kw= 'and' | kw= 'or' )
            {
            // InternalActivityDiagram.g:1204:2: (kw= 'and' | kw= 'or' )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==45) ) {
                alt33=1;
            }
            else if ( (LA33_0==46) ) {
                alt33=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // InternalActivityDiagram.g:1205:3: kw= 'and'
                    {
                    kw=(Token)match(input,45,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getBooleanOpAccess().getAndKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalActivityDiagram.g:1211:3: kw= 'or'
                    {
                    kw=(Token)match(input,46,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getBooleanOpAccess().getOrKeyword_1());
                    		

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
    // $ANTLR end "ruleBooleanOp"


    // $ANTLR start "entryRuleOutcome"
    // InternalActivityDiagram.g:1220:1: entryRuleOutcome returns [EObject current=null] : iv_ruleOutcome= ruleOutcome EOF ;
    public final EObject entryRuleOutcome() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOutcome = null;


        try {
            // InternalActivityDiagram.g:1220:48: (iv_ruleOutcome= ruleOutcome EOF )
            // InternalActivityDiagram.g:1221:2: iv_ruleOutcome= ruleOutcome EOF
            {
             newCompositeNode(grammarAccess.getOutcomeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleOutcome=ruleOutcome();

            state._fsp--;

             current =iv_ruleOutcome; 
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
    // $ANTLR end "entryRuleOutcome"


    // $ANTLR start "ruleOutcome"
    // InternalActivityDiagram.g:1227:1: ruleOutcome returns [EObject current=null] : ( () (otherlv_1= 'from' ( ( ruleQualifiedName ) ) )? ( (lv_outcomeValidation_3_0= ruleCheckParameterCondition ) )* ) ;
    public final EObject ruleOutcome() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        EObject lv_outcomeValidation_3_0 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:1233:2: ( ( () (otherlv_1= 'from' ( ( ruleQualifiedName ) ) )? ( (lv_outcomeValidation_3_0= ruleCheckParameterCondition ) )* ) )
            // InternalActivityDiagram.g:1234:2: ( () (otherlv_1= 'from' ( ( ruleQualifiedName ) ) )? ( (lv_outcomeValidation_3_0= ruleCheckParameterCondition ) )* )
            {
            // InternalActivityDiagram.g:1234:2: ( () (otherlv_1= 'from' ( ( ruleQualifiedName ) ) )? ( (lv_outcomeValidation_3_0= ruleCheckParameterCondition ) )* )
            // InternalActivityDiagram.g:1235:3: () (otherlv_1= 'from' ( ( ruleQualifiedName ) ) )? ( (lv_outcomeValidation_3_0= ruleCheckParameterCondition ) )*
            {
            // InternalActivityDiagram.g:1235:3: ()
            // InternalActivityDiagram.g:1236:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getOutcomeAccess().getOutcomeAction_0(),
            					current);
            			

            }

            // InternalActivityDiagram.g:1242:3: (otherlv_1= 'from' ( ( ruleQualifiedName ) ) )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==47) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // InternalActivityDiagram.g:1243:4: otherlv_1= 'from' ( ( ruleQualifiedName ) )
                    {
                    otherlv_1=(Token)match(input,47,FOLLOW_9); 

                    				newLeafNode(otherlv_1, grammarAccess.getOutcomeAccess().getFromKeyword_1_0());
                    			
                    // InternalActivityDiagram.g:1247:4: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:1248:5: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:1248:5: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:1249:6: ruleQualifiedName
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getOutcomeRule());
                    						}
                    					

                    						newCompositeNode(grammarAccess.getOutcomeAccess().getCapabilityOutcomeAbstractOutcomeItemsCrossReference_1_1_0());
                    					
                    pushFollow(FOLLOW_42);
                    ruleQualifiedName();

                    state._fsp--;


                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            // InternalActivityDiagram.g:1264:3: ( (lv_outcomeValidation_3_0= ruleCheckParameterCondition ) )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==24||LA35_0==48||(LA35_0>=52 && LA35_0<=53)) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // InternalActivityDiagram.g:1265:4: (lv_outcomeValidation_3_0= ruleCheckParameterCondition )
            	    {
            	    // InternalActivityDiagram.g:1265:4: (lv_outcomeValidation_3_0= ruleCheckParameterCondition )
            	    // InternalActivityDiagram.g:1266:5: lv_outcomeValidation_3_0= ruleCheckParameterCondition
            	    {

            	    					newCompositeNode(grammarAccess.getOutcomeAccess().getOutcomeValidationCheckParameterConditionParserRuleCall_2_0());
            	    				
            	    pushFollow(FOLLOW_42);
            	    lv_outcomeValidation_3_0=ruleCheckParameterCondition();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getOutcomeRule());
            	    					}
            	    					add(
            	    						current,
            	    						"outcomeValidation",
            	    						lv_outcomeValidation_3_0,
            	    						"com.smr.activity.dsl.ActivityDiagram.CheckParameterCondition");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop35;
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
    // $ANTLR end "ruleOutcome"


    // $ANTLR start "entryRuleCheckParameterCondition"
    // InternalActivityDiagram.g:1287:1: entryRuleCheckParameterCondition returns [EObject current=null] : iv_ruleCheckParameterCondition= ruleCheckParameterCondition EOF ;
    public final EObject entryRuleCheckParameterCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCheckParameterCondition = null;


        try {
            // InternalActivityDiagram.g:1287:64: (iv_ruleCheckParameterCondition= ruleCheckParameterCondition EOF )
            // InternalActivityDiagram.g:1288:2: iv_ruleCheckParameterCondition= ruleCheckParameterCondition EOF
            {
             newCompositeNode(grammarAccess.getCheckParameterConditionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCheckParameterCondition=ruleCheckParameterCondition();

            state._fsp--;

             current =iv_ruleCheckParameterCondition; 
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
    // $ANTLR end "entryRuleCheckParameterCondition"


    // $ANTLR start "ruleCheckParameterCondition"
    // InternalActivityDiagram.g:1294:1: ruleCheckParameterCondition returns [EObject current=null] : ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?) ) ) ;
    public final EObject ruleCheckParameterCondition() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token otherlv_16=null;
        EObject lv_checkMaxValue_7_0 = null;

        EObject lv_checkMinValue_9_0 = null;

        EObject lv_checkValues_12_0 = null;

        EObject lv_checkValues_14_0 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:1300:2: ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?) ) ) )
            // InternalActivityDiagram.g:1301:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?) ) )
            {
            // InternalActivityDiagram.g:1301:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?) ) )
            // InternalActivityDiagram.g:1302:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?) )
            {
            // InternalActivityDiagram.g:1302:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?) )
            // InternalActivityDiagram.g:1303:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?)
            {
             
            			  getUnorderedGroupHelper().enter(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup());
            			
            // InternalActivityDiagram.g:1306:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?)
            // InternalActivityDiagram.g:1307:5: ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+ {...}?
            {
            // InternalActivityDiagram.g:1307:5: ( ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) ) )+
            int cnt39=0;
            loop39:
            do {
                int alt39=4;
                switch ( input.LA(1) ) {
                case 48:
                    {
                    int LA39_2 = input.LA(2);

                    if ( getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 0) ) {
                        alt39=1;
                    }


                    }
                    break;
                case 52:
                    {
                    int LA39_3 = input.LA(2);

                    if ( getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 1) ) {
                        alt39=2;
                    }


                    }
                    break;
                case 53:
                    {
                    int LA39_4 = input.LA(2);

                    if ( getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 2) ) {
                        alt39=3;
                    }


                    }
                    break;
                case 24:
                    {
                    int LA39_5 = input.LA(2);

                    if ( getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 2) ) {
                        alt39=3;
                    }


                    }
                    break;

                }

                switch (alt39) {
            	case 1 :
            	    // InternalActivityDiagram.g:1308:3: ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) )
            	    {
            	    // InternalActivityDiagram.g:1308:3: ({...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) ) )
            	    // InternalActivityDiagram.g:1309:4: {...}? => ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 0)");
            	    }
            	    // InternalActivityDiagram.g:1309:117: ( ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) ) )
            	    // InternalActivityDiagram.g:1310:5: ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 0);
            	    				
            	    // InternalActivityDiagram.g:1313:8: ({...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? ) )
            	    // InternalActivityDiagram.g:1313:9: {...}? => (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "true");
            	    }
            	    // InternalActivityDiagram.g:1313:18: (otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )? )
            	    // InternalActivityDiagram.g:1313:19: otherlv_1= 'if' otherlv_2= 'outcome' ( ( ruleQualifiedName ) ) otherlv_4= 'is' otherlv_5= '(' (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )?
            	    {
            	    otherlv_1=(Token)match(input,48,FOLLOW_43); 

            	    								newLeafNode(otherlv_1, grammarAccess.getCheckParameterConditionAccess().getIfKeyword_0_0());
            	    							
            	    otherlv_2=(Token)match(input,49,FOLLOW_9); 

            	    								newLeafNode(otherlv_2, grammarAccess.getCheckParameterConditionAccess().getOutcomeKeyword_0_1());
            	    							
            	    // InternalActivityDiagram.g:1321:8: ( ( ruleQualifiedName ) )
            	    // InternalActivityDiagram.g:1322:9: ( ruleQualifiedName )
            	    {
            	    // InternalActivityDiagram.g:1322:9: ( ruleQualifiedName )
            	    // InternalActivityDiagram.g:1323:10: ruleQualifiedName
            	    {

            	    										if (current==null) {
            	    											current = createModelElement(grammarAccess.getCheckParameterConditionRule());
            	    										}
            	    									

            	    										newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getParameterParameterCrossReference_0_2_0());
            	    									
            	    pushFollow(FOLLOW_44);
            	    ruleQualifiedName();

            	    state._fsp--;


            	    										afterParserOrEnumRuleCall();
            	    									

            	    }


            	    }

            	    otherlv_4=(Token)match(input,50,FOLLOW_17); 

            	    								newLeafNode(otherlv_4, grammarAccess.getCheckParameterConditionAccess().getIsKeyword_0_3());
            	    							
            	    otherlv_5=(Token)match(input,23,FOLLOW_45); 

            	    								newLeafNode(otherlv_5, grammarAccess.getCheckParameterConditionAccess().getLeftParenthesisKeyword_0_4());
            	    							
            	    // InternalActivityDiagram.g:1345:8: (otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) ) )?
            	    int alt36=2;
            	    int LA36_0 = input.LA(1);

            	    if ( (LA36_0==51) ) {
            	        alt36=1;
            	    }
            	    switch (alt36) {
            	        case 1 :
            	            // InternalActivityDiagram.g:1346:9: otherlv_6= '>' ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) )
            	            {
            	            otherlv_6=(Token)match(input,51,FOLLOW_46); 

            	            									newLeafNode(otherlv_6, grammarAccess.getCheckParameterConditionAccess().getGreaterThanSignKeyword_0_5_0());
            	            								
            	            // InternalActivityDiagram.g:1350:9: ( (lv_checkMaxValue_7_0= rulePrimitiveValue ) )
            	            // InternalActivityDiagram.g:1351:10: (lv_checkMaxValue_7_0= rulePrimitiveValue )
            	            {
            	            // InternalActivityDiagram.g:1351:10: (lv_checkMaxValue_7_0= rulePrimitiveValue )
            	            // InternalActivityDiagram.g:1352:11: lv_checkMaxValue_7_0= rulePrimitiveValue
            	            {

            	            											newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getCheckMaxValuePrimitiveValueParserRuleCall_0_5_1_0());
            	            										
            	            pushFollow(FOLLOW_42);
            	            lv_checkMaxValue_7_0=rulePrimitiveValue();

            	            state._fsp--;


            	            											if (current==null) {
            	            												current = createModelElementForParent(grammarAccess.getCheckParameterConditionRule());
            	            											}
            	            											set(
            	            												current,
            	            												"checkMaxValue",
            	            												lv_checkMaxValue_7_0,
            	            												"com.dml.dsl.Dml.PrimitiveValue");
            	            											afterParserOrEnumRuleCall();
            	            										

            	            }


            	            }


            	            }
            	            break;

            	    }


            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalActivityDiagram.g:1376:3: ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) )
            	    {
            	    // InternalActivityDiagram.g:1376:3: ({...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) ) )
            	    // InternalActivityDiagram.g:1377:4: {...}? => ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 1)");
            	    }
            	    // InternalActivityDiagram.g:1377:117: ( ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) ) )
            	    // InternalActivityDiagram.g:1378:5: ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 1);
            	    				
            	    // InternalActivityDiagram.g:1381:8: ({...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) ) )
            	    // InternalActivityDiagram.g:1381:9: {...}? => (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "true");
            	    }
            	    // InternalActivityDiagram.g:1381:18: (otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) ) )
            	    // InternalActivityDiagram.g:1381:19: otherlv_8= '<' ( (lv_checkMinValue_9_0= rulePrimitiveValue ) )
            	    {
            	    otherlv_8=(Token)match(input,52,FOLLOW_46); 

            	    								newLeafNode(otherlv_8, grammarAccess.getCheckParameterConditionAccess().getLessThanSignKeyword_1_0());
            	    							
            	    // InternalActivityDiagram.g:1385:8: ( (lv_checkMinValue_9_0= rulePrimitiveValue ) )
            	    // InternalActivityDiagram.g:1386:9: (lv_checkMinValue_9_0= rulePrimitiveValue )
            	    {
            	    // InternalActivityDiagram.g:1386:9: (lv_checkMinValue_9_0= rulePrimitiveValue )
            	    // InternalActivityDiagram.g:1387:10: lv_checkMinValue_9_0= rulePrimitiveValue
            	    {

            	    										newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getCheckMinValuePrimitiveValueParserRuleCall_1_1_0());
            	    									
            	    pushFollow(FOLLOW_42);
            	    lv_checkMinValue_9_0=rulePrimitiveValue();

            	    state._fsp--;


            	    										if (current==null) {
            	    											current = createModelElementForParent(grammarAccess.getCheckParameterConditionRule());
            	    										}
            	    										set(
            	    											current,
            	    											"checkMinValue",
            	    											lv_checkMinValue_9_0,
            	    											"com.dml.dsl.Dml.PrimitiveValue");
            	    										afterParserOrEnumRuleCall();
            	    									

            	    }


            	    }


            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalActivityDiagram.g:1410:3: ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) )
            	    {
            	    // InternalActivityDiagram.g:1410:3: ({...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) ) )
            	    // InternalActivityDiagram.g:1411:4: {...}? => ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 2) ) {
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 2)");
            	    }
            	    // InternalActivityDiagram.g:1411:117: ( ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) ) )
            	    // InternalActivityDiagram.g:1412:5: ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup(), 2);
            	    				
            	    // InternalActivityDiagram.g:1415:8: ({...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' ) )
            	    // InternalActivityDiagram.g:1415:9: {...}? => ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "true");
            	    }
            	    // InternalActivityDiagram.g:1415:18: ( (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')' )
            	    // InternalActivityDiagram.g:1415:19: (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )? otherlv_16= ')'
            	    {
            	    // InternalActivityDiagram.g:1415:19: (otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')' )?
            	    int alt38=2;
            	    int LA38_0 = input.LA(1);

            	    if ( (LA38_0==53) ) {
            	        alt38=1;
            	    }
            	    switch (alt38) {
            	        case 1 :
            	            // InternalActivityDiagram.g:1416:9: otherlv_10= '=' otherlv_11= '(' ( (lv_checkValues_12_0= rulePrimitiveValue ) ) (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )* otherlv_15= ')'
            	            {
            	            otherlv_10=(Token)match(input,53,FOLLOW_17); 

            	            									newLeafNode(otherlv_10, grammarAccess.getCheckParameterConditionAccess().getEqualsSignKeyword_2_0_0());
            	            								
            	            otherlv_11=(Token)match(input,23,FOLLOW_46); 

            	            									newLeafNode(otherlv_11, grammarAccess.getCheckParameterConditionAccess().getLeftParenthesisKeyword_2_0_1());
            	            								
            	            // InternalActivityDiagram.g:1424:9: ( (lv_checkValues_12_0= rulePrimitiveValue ) )
            	            // InternalActivityDiagram.g:1425:10: (lv_checkValues_12_0= rulePrimitiveValue )
            	            {
            	            // InternalActivityDiagram.g:1425:10: (lv_checkValues_12_0= rulePrimitiveValue )
            	            // InternalActivityDiagram.g:1426:11: lv_checkValues_12_0= rulePrimitiveValue
            	            {

            	            											newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getCheckValuesPrimitiveValueParserRuleCall_2_0_2_0());
            	            										
            	            pushFollow(FOLLOW_19);
            	            lv_checkValues_12_0=rulePrimitiveValue();

            	            state._fsp--;


            	            											if (current==null) {
            	            												current = createModelElementForParent(grammarAccess.getCheckParameterConditionRule());
            	            											}
            	            											add(
            	            												current,
            	            												"checkValues",
            	            												lv_checkValues_12_0,
            	            												"com.dml.dsl.Dml.PrimitiveValue");
            	            											afterParserOrEnumRuleCall();
            	            										

            	            }


            	            }

            	            // InternalActivityDiagram.g:1443:9: (otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) ) )*
            	            loop37:
            	            do {
            	                int alt37=2;
            	                int LA37_0 = input.LA(1);

            	                if ( (LA37_0==15) ) {
            	                    alt37=1;
            	                }


            	                switch (alt37) {
            	            	case 1 :
            	            	    // InternalActivityDiagram.g:1444:10: otherlv_13= ',' ( (lv_checkValues_14_0= rulePrimitiveValue ) )
            	            	    {
            	            	    otherlv_13=(Token)match(input,15,FOLLOW_46); 

            	            	    										newLeafNode(otherlv_13, grammarAccess.getCheckParameterConditionAccess().getCommaKeyword_2_0_3_0());
            	            	    									
            	            	    // InternalActivityDiagram.g:1448:10: ( (lv_checkValues_14_0= rulePrimitiveValue ) )
            	            	    // InternalActivityDiagram.g:1449:11: (lv_checkValues_14_0= rulePrimitiveValue )
            	            	    {
            	            	    // InternalActivityDiagram.g:1449:11: (lv_checkValues_14_0= rulePrimitiveValue )
            	            	    // InternalActivityDiagram.g:1450:12: lv_checkValues_14_0= rulePrimitiveValue
            	            	    {

            	            	    												newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getCheckValuesPrimitiveValueParserRuleCall_2_0_3_1_0());
            	            	    											
            	            	    pushFollow(FOLLOW_19);
            	            	    lv_checkValues_14_0=rulePrimitiveValue();

            	            	    state._fsp--;


            	            	    												if (current==null) {
            	            	    													current = createModelElementForParent(grammarAccess.getCheckParameterConditionRule());
            	            	    												}
            	            	    												add(
            	            	    													current,
            	            	    													"checkValues",
            	            	    													lv_checkValues_14_0,
            	            	    													"com.dml.dsl.Dml.PrimitiveValue");
            	            	    												afterParserOrEnumRuleCall();
            	            	    											

            	            	    }


            	            	    }


            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop37;
            	                }
            	            } while (true);

            	            otherlv_15=(Token)match(input,24,FOLLOW_47); 

            	            									newLeafNode(otherlv_15, grammarAccess.getCheckParameterConditionAccess().getRightParenthesisKeyword_2_0_4());
            	            								

            	            }
            	            break;

            	    }

            	    otherlv_16=(Token)match(input,24,FOLLOW_42); 

            	    								newLeafNode(otherlv_16, grammarAccess.getCheckParameterConditionAccess().getRightParenthesisKeyword_2_1());
            	    							

            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt39 >= 1 ) break loop39;
                        EarlyExitException eee =
                            new EarlyExitException(39, input);
                        throw eee;
                }
                cnt39++;
            } while (true);

            if ( ! getUnorderedGroupHelper().canLeave(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup()) ) {
                throw new FailedPredicateException(input, "ruleCheckParameterCondition", "getUnorderedGroupHelper().canLeave(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup())");
            }

            }


            }

             
            			  getUnorderedGroupHelper().leave(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup());
            			

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
    // $ANTLR end "ruleCheckParameterCondition"


    // $ANTLR start "entryRuleDataModel"
    // InternalActivityDiagram.g:1494:1: entryRuleDataModel returns [EObject current=null] : iv_ruleDataModel= ruleDataModel EOF ;
    public final EObject entryRuleDataModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataModel = null;


        try {
            // InternalActivityDiagram.g:1494:50: (iv_ruleDataModel= ruleDataModel EOF )
            // InternalActivityDiagram.g:1495:2: iv_ruleDataModel= ruleDataModel EOF
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
    // InternalActivityDiagram.g:1501:1: ruleDataModel returns [EObject current=null] : ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) ) ;
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
            // InternalActivityDiagram.g:1507:2: ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) ) )
            // InternalActivityDiagram.g:1508:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) )
            {
            // InternalActivityDiagram.g:1508:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) )
            // InternalActivityDiagram.g:1509:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) )
            {
            // InternalActivityDiagram.g:1509:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) )
            // InternalActivityDiagram.g:1510:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?)
            {
             
            			  getUnorderedGroupHelper().enter(grammarAccess.getDataModelAccess().getUnorderedGroup());
            			
            // InternalActivityDiagram.g:1513:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?)
            // InternalActivityDiagram.g:1514:5: ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?
            {
            // InternalActivityDiagram.g:1514:5: ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+
            int cnt44=0;
            loop44:
            do {
                int alt44=3;
                int LA44_0 = input.LA(1);

                if ( LA44_0 == 54 && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
                    alt44=1;
                }
                else if ( ( LA44_0 == 28 || LA44_0 == 56 ) && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
                    alt44=2;
                }


                switch (alt44) {
            	case 1 :
            	    // InternalActivityDiagram.g:1515:3: ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) )
            	    {
            	    // InternalActivityDiagram.g:1515:3: ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) )
            	    // InternalActivityDiagram.g:1516:4: {...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0)");
            	    }
            	    // InternalActivityDiagram.g:1516:103: ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) )
            	    // InternalActivityDiagram.g:1517:5: ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0);
            	    				
            	    // InternalActivityDiagram.g:1520:8: ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) )
            	    // InternalActivityDiagram.g:1520:9: {...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "true");
            	    }
            	    // InternalActivityDiagram.g:1520:18: (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? )
            	    // InternalActivityDiagram.g:1520:19: otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )?
            	    {
            	    otherlv_1=(Token)match(input,54,FOLLOW_3); 

            	    								newLeafNode(otherlv_1, grammarAccess.getDataModelAccess().getDataModelKeyword_0_0());
            	    							
            	    // InternalActivityDiagram.g:1524:8: ( (lv_name_2_0= ruleEString ) )
            	    // InternalActivityDiagram.g:1525:9: (lv_name_2_0= ruleEString )
            	    {
            	    // InternalActivityDiagram.g:1525:9: (lv_name_2_0= ruleEString )
            	    // InternalActivityDiagram.g:1526:10: lv_name_2_0= ruleEString
            	    {

            	    										newCompositeNode(grammarAccess.getDataModelAccess().getNameEStringParserRuleCall_0_1_0());
            	    									
            	    pushFollow(FOLLOW_22);
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

            	    otherlv_3=(Token)match(input,27,FOLLOW_48); 

            	    								newLeafNode(otherlv_3, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_2());
            	    							
            	    // InternalActivityDiagram.g:1547:8: (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )?
            	    int alt41=2;
            	    int LA41_0 = input.LA(1);

            	    if ( (LA41_0==55) ) {
            	        alt41=1;
            	    }
            	    switch (alt41) {
            	        case 1 :
            	            // InternalActivityDiagram.g:1548:9: otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}'
            	            {
            	            otherlv_4=(Token)match(input,55,FOLLOW_22); 

            	            									newLeafNode(otherlv_4, grammarAccess.getDataModelAccess().getPrimitivesKeyword_0_3_0());
            	            								
            	            otherlv_5=(Token)match(input,27,FOLLOW_18); 

            	            									newLeafNode(otherlv_5, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_3_1());
            	            								
            	            // InternalActivityDiagram.g:1556:9: ( (lv_primitives_6_0= ruleParameter ) )
            	            // InternalActivityDiagram.g:1557:10: (lv_primitives_6_0= ruleParameter )
            	            {
            	            // InternalActivityDiagram.g:1557:10: (lv_primitives_6_0= ruleParameter )
            	            // InternalActivityDiagram.g:1558:11: lv_primitives_6_0= ruleParameter
            	            {

            	            											newCompositeNode(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_2_0());
            	            										
            	            pushFollow(FOLLOW_24);
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

            	            // InternalActivityDiagram.g:1575:9: (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )*
            	            loop40:
            	            do {
            	                int alt40=2;
            	                int LA40_0 = input.LA(1);

            	                if ( (LA40_0==15) ) {
            	                    alt40=1;
            	                }


            	                switch (alt40) {
            	            	case 1 :
            	            	    // InternalActivityDiagram.g:1576:10: otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) )
            	            	    {
            	            	    otherlv_7=(Token)match(input,15,FOLLOW_18); 

            	            	    										newLeafNode(otherlv_7, grammarAccess.getDataModelAccess().getCommaKeyword_0_3_3_0());
            	            	    									
            	            	    // InternalActivityDiagram.g:1580:10: ( (lv_primitives_8_0= ruleParameter ) )
            	            	    // InternalActivityDiagram.g:1581:11: (lv_primitives_8_0= ruleParameter )
            	            	    {
            	            	    // InternalActivityDiagram.g:1581:11: (lv_primitives_8_0= ruleParameter )
            	            	    // InternalActivityDiagram.g:1582:12: lv_primitives_8_0= ruleParameter
            	            	    {

            	            	    												newCompositeNode(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_3_1_0());
            	            	    											
            	            	    pushFollow(FOLLOW_24);
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
            	            	    break loop40;
            	                }
            	            } while (true);

            	            otherlv_9=(Token)match(input,28,FOLLOW_49); 

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
            	    // InternalActivityDiagram.g:1611:3: ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) )
            	    {
            	    // InternalActivityDiagram.g:1611:3: ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) )
            	    // InternalActivityDiagram.g:1612:4: {...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1)");
            	    }
            	    // InternalActivityDiagram.g:1612:103: ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) )
            	    // InternalActivityDiagram.g:1613:5: ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) )
            	    {

            	    					getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1);
            	    				
            	    // InternalActivityDiagram.g:1616:8: ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) )
            	    // InternalActivityDiagram.g:1616:9: {...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleDataModel", "true");
            	    }
            	    // InternalActivityDiagram.g:1616:18: ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' )
            	    // InternalActivityDiagram.g:1616:19: (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}'
            	    {
            	    // InternalActivityDiagram.g:1616:19: (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )?
            	    int alt43=2;
            	    int LA43_0 = input.LA(1);

            	    if ( (LA43_0==56) ) {
            	        alt43=1;
            	    }
            	    switch (alt43) {
            	        case 1 :
            	            // InternalActivityDiagram.g:1617:9: otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}'
            	            {
            	            otherlv_10=(Token)match(input,56,FOLLOW_22); 

            	            									newLeafNode(otherlv_10, grammarAccess.getDataModelAccess().getCompositesKeyword_1_0_0());
            	            								
            	            otherlv_11=(Token)match(input,27,FOLLOW_9); 

            	            									newLeafNode(otherlv_11, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_1_0_1());
            	            								
            	            // InternalActivityDiagram.g:1625:9: ( (otherlv_12= RULE_ID ) )
            	            // InternalActivityDiagram.g:1626:10: (otherlv_12= RULE_ID )
            	            {
            	            // InternalActivityDiagram.g:1626:10: (otherlv_12= RULE_ID )
            	            // InternalActivityDiagram.g:1627:11: otherlv_12= RULE_ID
            	            {

            	            											if (current==null) {
            	            												current = createModelElement(grammarAccess.getDataModelRule());
            	            											}
            	            										
            	            otherlv_12=(Token)match(input,RULE_ID,FOLLOW_24); 

            	            											newLeafNode(otherlv_12, grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_2_0());
            	            										

            	            }


            	            }

            	            // InternalActivityDiagram.g:1638:9: (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )*
            	            loop42:
            	            do {
            	                int alt42=2;
            	                int LA42_0 = input.LA(1);

            	                if ( (LA42_0==15) ) {
            	                    alt42=1;
            	                }


            	                switch (alt42) {
            	            	case 1 :
            	            	    // InternalActivityDiagram.g:1639:10: otherlv_13= ',' ( (otherlv_14= RULE_ID ) )
            	            	    {
            	            	    otherlv_13=(Token)match(input,15,FOLLOW_9); 

            	            	    										newLeafNode(otherlv_13, grammarAccess.getDataModelAccess().getCommaKeyword_1_0_3_0());
            	            	    									
            	            	    // InternalActivityDiagram.g:1643:10: ( (otherlv_14= RULE_ID ) )
            	            	    // InternalActivityDiagram.g:1644:11: (otherlv_14= RULE_ID )
            	            	    {
            	            	    // InternalActivityDiagram.g:1644:11: (otherlv_14= RULE_ID )
            	            	    // InternalActivityDiagram.g:1645:12: otherlv_14= RULE_ID
            	            	    {

            	            	    												if (current==null) {
            	            	    													current = createModelElement(grammarAccess.getDataModelRule());
            	            	    												}
            	            	    											
            	            	    otherlv_14=(Token)match(input,RULE_ID,FOLLOW_24); 

            	            	    												newLeafNode(otherlv_14, grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_3_1_0());
            	            	    											

            	            	    }


            	            	    }


            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop42;
            	                }
            	            } while (true);

            	            otherlv_15=(Token)match(input,28,FOLLOW_38); 

            	            									newLeafNode(otherlv_15, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_0_4());
            	            								

            	            }
            	            break;

            	    }

            	    otherlv_16=(Token)match(input,28,FOLLOW_49); 

            	    								newLeafNode(otherlv_16, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_1());
            	    							

            	    }


            	    }

            	     
            	    					getUnorderedGroupHelper().returnFromSelection(grammarAccess.getDataModelAccess().getUnorderedGroup());
            	    				

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt44 >= 1 ) break loop44;
                        EarlyExitException eee =
                            new EarlyExitException(44, input);
                        throw eee;
                }
                cnt44++;
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
    // InternalActivityDiagram.g:1683:1: entryRuleParameter returns [EObject current=null] : iv_ruleParameter= ruleParameter EOF ;
    public final EObject entryRuleParameter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParameter = null;


        try {
            // InternalActivityDiagram.g:1683:50: (iv_ruleParameter= ruleParameter EOF )
            // InternalActivityDiagram.g:1684:2: iv_ruleParameter= ruleParameter EOF
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
    // InternalActivityDiagram.g:1690:1: ruleParameter returns [EObject current=null] : (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType ) ;
    public final EObject ruleParameter() throws RecognitionException {
        EObject current = null;

        EObject this_SimpleType_0 = null;

        EObject this_AbstractType_1 = null;

        EObject this_ArrayType_2 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:1696:2: ( (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType ) )
            // InternalActivityDiagram.g:1697:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )
            {
            // InternalActivityDiagram.g:1697:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )
            int alt45=3;
            alt45 = dfa45.predict(input);
            switch (alt45) {
                case 1 :
                    // InternalActivityDiagram.g:1698:3: this_SimpleType_0= ruleSimpleType
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
                    // InternalActivityDiagram.g:1707:3: this_AbstractType_1= ruleAbstractType
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
                    // InternalActivityDiagram.g:1716:3: this_ArrayType_2= ruleArrayType
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
    // InternalActivityDiagram.g:1728:1: entryRuleQualifiedName returns [String current=null] : iv_ruleQualifiedName= ruleQualifiedName EOF ;
    public final String entryRuleQualifiedName() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQualifiedName = null;


        try {
            // InternalActivityDiagram.g:1728:53: (iv_ruleQualifiedName= ruleQualifiedName EOF )
            // InternalActivityDiagram.g:1729:2: iv_ruleQualifiedName= ruleQualifiedName EOF
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
    // InternalActivityDiagram.g:1735:1: ruleQualifiedName returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQualifiedName() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:1741:2: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // InternalActivityDiagram.g:1742:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // InternalActivityDiagram.g:1742:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // InternalActivityDiagram.g:1743:3: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_50); 

            			current.merge(this_ID_0);
            		

            			newLeafNode(this_ID_0, grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_0());
            		
            // InternalActivityDiagram.g:1750:3: (kw= '.' this_ID_2= RULE_ID )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==57) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // InternalActivityDiagram.g:1751:4: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,57,FOLLOW_9); 

            	    				current.merge(kw);
            	    				newLeafNode(kw, grammarAccess.getQualifiedNameAccess().getFullStopKeyword_1_0());
            	    			
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_50); 

            	    				current.merge(this_ID_2);
            	    			

            	    				newLeafNode(this_ID_2, grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_1_1());
            	    			

            	    }
            	    break;

            	default :
            	    break loop46;
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
    // InternalActivityDiagram.g:1768:1: entryRuleSimpleType returns [EObject current=null] : iv_ruleSimpleType= ruleSimpleType EOF ;
    public final EObject entryRuleSimpleType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleType = null;


        try {
            // InternalActivityDiagram.g:1768:51: (iv_ruleSimpleType= ruleSimpleType EOF )
            // InternalActivityDiagram.g:1769:2: iv_ruleSimpleType= ruleSimpleType EOF
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
    // InternalActivityDiagram.g:1775:1: ruleSimpleType returns [EObject current=null] : ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? ) ;
    public final EObject ruleSimpleType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        Enumerator lv_type_1_0 = null;

        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:1781:2: ( ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? ) )
            // InternalActivityDiagram.g:1782:2: ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? )
            {
            // InternalActivityDiagram.g:1782:2: ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? )
            // InternalActivityDiagram.g:1783:3: () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )?
            {
            // InternalActivityDiagram.g:1783:3: ()
            // InternalActivityDiagram.g:1784:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getSimpleTypeAccess().getSimpleTypeAction_0(),
            					current);
            			

            }

            // InternalActivityDiagram.g:1790:3: ( (lv_type_1_0= rulePrimitiveValueType ) )
            // InternalActivityDiagram.g:1791:4: (lv_type_1_0= rulePrimitiveValueType )
            {
            // InternalActivityDiagram.g:1791:4: (lv_type_1_0= rulePrimitiveValueType )
            // InternalActivityDiagram.g:1792:5: lv_type_1_0= rulePrimitiveValueType
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

            // InternalActivityDiagram.g:1809:3: ( (lv_name_2_0= ruleEString ) )
            // InternalActivityDiagram.g:1810:4: (lv_name_2_0= ruleEString )
            {
            // InternalActivityDiagram.g:1810:4: (lv_name_2_0= ruleEString )
            // InternalActivityDiagram.g:1811:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getSimpleTypeAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_51);
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

            // InternalActivityDiagram.g:1828:3: (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==53) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // InternalActivityDiagram.g:1829:4: otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) )
                    {
                    otherlv_3=(Token)match(input,53,FOLLOW_46); 

                    				newLeafNode(otherlv_3, grammarAccess.getSimpleTypeAccess().getEqualsSignKeyword_3_0());
                    			
                    // InternalActivityDiagram.g:1833:4: ( (lv_value_4_0= rulePrimitiveValue ) )
                    // InternalActivityDiagram.g:1834:5: (lv_value_4_0= rulePrimitiveValue )
                    {
                    // InternalActivityDiagram.g:1834:5: (lv_value_4_0= rulePrimitiveValue )
                    // InternalActivityDiagram.g:1835:6: lv_value_4_0= rulePrimitiveValue
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
    // InternalActivityDiagram.g:1857:1: entryRuleAbstractType returns [EObject current=null] : iv_ruleAbstractType= ruleAbstractType EOF ;
    public final EObject entryRuleAbstractType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractType = null;


        try {
            // InternalActivityDiagram.g:1857:53: (iv_ruleAbstractType= ruleAbstractType EOF )
            // InternalActivityDiagram.g:1858:2: iv_ruleAbstractType= ruleAbstractType EOF
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
    // InternalActivityDiagram.g:1864:1: ruleAbstractType returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? ) ;
    public final EObject ruleAbstractType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:1870:2: ( ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? ) )
            // InternalActivityDiagram.g:1871:2: ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? )
            {
            // InternalActivityDiagram.g:1871:2: ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? )
            // InternalActivityDiagram.g:1872:3: () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )?
            {
            // InternalActivityDiagram.g:1872:3: ()
            // InternalActivityDiagram.g:1873:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getAbstractTypeAccess().getAbstractTypeAction_0(),
            					current);
            			

            }

            // InternalActivityDiagram.g:1879:3: ( ( ruleQualifiedName ) )
            // InternalActivityDiagram.g:1880:4: ( ruleQualifiedName )
            {
            // InternalActivityDiagram.g:1880:4: ( ruleQualifiedName )
            // InternalActivityDiagram.g:1881:5: ruleQualifiedName
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

            // InternalActivityDiagram.g:1895:3: ( (lv_name_2_0= ruleEString ) )
            // InternalActivityDiagram.g:1896:4: (lv_name_2_0= ruleEString )
            {
            // InternalActivityDiagram.g:1896:4: (lv_name_2_0= ruleEString )
            // InternalActivityDiagram.g:1897:5: lv_name_2_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getAbstractTypeAccess().getNameEStringParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_51);
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

            // InternalActivityDiagram.g:1914:3: (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==53) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // InternalActivityDiagram.g:1915:4: otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) )
                    {
                    otherlv_3=(Token)match(input,53,FOLLOW_46); 

                    				newLeafNode(otherlv_3, grammarAccess.getAbstractTypeAccess().getEqualsSignKeyword_3_0());
                    			
                    // InternalActivityDiagram.g:1919:4: ( (lv_value_4_0= ruleAbstractObjectValue ) )
                    // InternalActivityDiagram.g:1920:5: (lv_value_4_0= ruleAbstractObjectValue )
                    {
                    // InternalActivityDiagram.g:1920:5: (lv_value_4_0= ruleAbstractObjectValue )
                    // InternalActivityDiagram.g:1921:6: lv_value_4_0= ruleAbstractObjectValue
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
    // InternalActivityDiagram.g:1943:1: entryRulePrimitiveValue returns [EObject current=null] : iv_rulePrimitiveValue= rulePrimitiveValue EOF ;
    public final EObject entryRulePrimitiveValue() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimitiveValue = null;


        try {
            // InternalActivityDiagram.g:1943:55: (iv_rulePrimitiveValue= rulePrimitiveValue EOF )
            // InternalActivityDiagram.g:1944:2: iv_rulePrimitiveValue= rulePrimitiveValue EOF
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
    // InternalActivityDiagram.g:1950:1: rulePrimitiveValue returns [EObject current=null] : ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue ) ;
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
            // InternalActivityDiagram.g:1956:2: ( ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue ) )
            // InternalActivityDiagram.g:1957:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )
            {
            // InternalActivityDiagram.g:1957:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )
            int alt49=7;
            alt49 = dfa49.predict(input);
            switch (alt49) {
                case 1 :
                    // InternalActivityDiagram.g:1958:3: ( () ( (lv_intValue_1_0= ruleEInt ) ) )
                    {
                    // InternalActivityDiagram.g:1958:3: ( () ( (lv_intValue_1_0= ruleEInt ) ) )
                    // InternalActivityDiagram.g:1959:4: () ( (lv_intValue_1_0= ruleEInt ) )
                    {
                    // InternalActivityDiagram.g:1959:4: ()
                    // InternalActivityDiagram.g:1960:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getIntValueAction_0_0(),
                    						current);
                    				

                    }

                    // InternalActivityDiagram.g:1966:4: ( (lv_intValue_1_0= ruleEInt ) )
                    // InternalActivityDiagram.g:1967:5: (lv_intValue_1_0= ruleEInt )
                    {
                    // InternalActivityDiagram.g:1967:5: (lv_intValue_1_0= ruleEInt )
                    // InternalActivityDiagram.g:1968:6: lv_intValue_1_0= ruleEInt
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
                    // InternalActivityDiagram.g:1987:3: ( () ( (lv_floatValue_3_0= ruleEFloat ) ) )
                    {
                    // InternalActivityDiagram.g:1987:3: ( () ( (lv_floatValue_3_0= ruleEFloat ) ) )
                    // InternalActivityDiagram.g:1988:4: () ( (lv_floatValue_3_0= ruleEFloat ) )
                    {
                    // InternalActivityDiagram.g:1988:4: ()
                    // InternalActivityDiagram.g:1989:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getFloatValueAction_1_0(),
                    						current);
                    				

                    }

                    // InternalActivityDiagram.g:1995:4: ( (lv_floatValue_3_0= ruleEFloat ) )
                    // InternalActivityDiagram.g:1996:5: (lv_floatValue_3_0= ruleEFloat )
                    {
                    // InternalActivityDiagram.g:1996:5: (lv_floatValue_3_0= ruleEFloat )
                    // InternalActivityDiagram.g:1997:6: lv_floatValue_3_0= ruleEFloat
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
                    // InternalActivityDiagram.g:2016:3: ( () ( (lv_stringValue_5_0= RULE_STRING ) ) )
                    {
                    // InternalActivityDiagram.g:2016:3: ( () ( (lv_stringValue_5_0= RULE_STRING ) ) )
                    // InternalActivityDiagram.g:2017:4: () ( (lv_stringValue_5_0= RULE_STRING ) )
                    {
                    // InternalActivityDiagram.g:2017:4: ()
                    // InternalActivityDiagram.g:2018:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getStringValueAction_2_0(),
                    						current);
                    				

                    }

                    // InternalActivityDiagram.g:2024:4: ( (lv_stringValue_5_0= RULE_STRING ) )
                    // InternalActivityDiagram.g:2025:5: (lv_stringValue_5_0= RULE_STRING )
                    {
                    // InternalActivityDiagram.g:2025:5: (lv_stringValue_5_0= RULE_STRING )
                    // InternalActivityDiagram.g:2026:6: lv_stringValue_5_0= RULE_STRING
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
                    // InternalActivityDiagram.g:2044:3: ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) )
                    {
                    // InternalActivityDiagram.g:2044:3: ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) )
                    // InternalActivityDiagram.g:2045:4: () ( (lv_boolValue_7_0= ruleEBoolean ) )
                    {
                    // InternalActivityDiagram.g:2045:4: ()
                    // InternalActivityDiagram.g:2046:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getBoolValueAction_3_0(),
                    						current);
                    				

                    }

                    // InternalActivityDiagram.g:2052:4: ( (lv_boolValue_7_0= ruleEBoolean ) )
                    // InternalActivityDiagram.g:2053:5: (lv_boolValue_7_0= ruleEBoolean )
                    {
                    // InternalActivityDiagram.g:2053:5: (lv_boolValue_7_0= ruleEBoolean )
                    // InternalActivityDiagram.g:2054:6: lv_boolValue_7_0= ruleEBoolean
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
                    // InternalActivityDiagram.g:2073:3: ( () ( (lv_dateValue_9_0= ruleEDate ) ) )
                    {
                    // InternalActivityDiagram.g:2073:3: ( () ( (lv_dateValue_9_0= ruleEDate ) ) )
                    // InternalActivityDiagram.g:2074:4: () ( (lv_dateValue_9_0= ruleEDate ) )
                    {
                    // InternalActivityDiagram.g:2074:4: ()
                    // InternalActivityDiagram.g:2075:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getPrimitiveValueAccess().getDateValueAction_4_0(),
                    						current);
                    				

                    }

                    // InternalActivityDiagram.g:2081:4: ( (lv_dateValue_9_0= ruleEDate ) )
                    // InternalActivityDiagram.g:2082:5: (lv_dateValue_9_0= ruleEDate )
                    {
                    // InternalActivityDiagram.g:2082:5: (lv_dateValue_9_0= ruleEDate )
                    // InternalActivityDiagram.g:2083:6: lv_dateValue_9_0= ruleEDate
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
                    // InternalActivityDiagram.g:2102:3: this_ArrayValues_10= ruleArrayValues
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
                    // InternalActivityDiagram.g:2111:3: this_AbstractObjectValue_11= ruleAbstractObjectValue
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
    // InternalActivityDiagram.g:2123:1: entryRuleAbstractObjectValue returns [EObject current=null] : iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF ;
    public final EObject entryRuleAbstractObjectValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractObjectValue = null;


        try {
            // InternalActivityDiagram.g:2123:60: (iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF )
            // InternalActivityDiagram.g:2124:2: iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF
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
    // InternalActivityDiagram.g:2130:1: ruleAbstractObjectValue returns [EObject current=null] : ( () ( (lv_abstractValue_1_0= RULE_ID ) ) ) ;
    public final EObject ruleAbstractObjectValue() throws RecognitionException {
        EObject current = null;

        Token lv_abstractValue_1_0=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2136:2: ( ( () ( (lv_abstractValue_1_0= RULE_ID ) ) ) )
            // InternalActivityDiagram.g:2137:2: ( () ( (lv_abstractValue_1_0= RULE_ID ) ) )
            {
            // InternalActivityDiagram.g:2137:2: ( () ( (lv_abstractValue_1_0= RULE_ID ) ) )
            // InternalActivityDiagram.g:2138:3: () ( (lv_abstractValue_1_0= RULE_ID ) )
            {
            // InternalActivityDiagram.g:2138:3: ()
            // InternalActivityDiagram.g:2139:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getAbstractObjectValueAccess().getAbstractObjectValueAction_0(),
            					current);
            			

            }

            // InternalActivityDiagram.g:2145:3: ( (lv_abstractValue_1_0= RULE_ID ) )
            // InternalActivityDiagram.g:2146:4: (lv_abstractValue_1_0= RULE_ID )
            {
            // InternalActivityDiagram.g:2146:4: (lv_abstractValue_1_0= RULE_ID )
            // InternalActivityDiagram.g:2147:5: lv_abstractValue_1_0= RULE_ID
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
    // InternalActivityDiagram.g:2167:1: entryRuleArrayValues returns [EObject current=null] : iv_ruleArrayValues= ruleArrayValues EOF ;
    public final EObject entryRuleArrayValues() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayValues = null;


        try {
            // InternalActivityDiagram.g:2167:52: (iv_ruleArrayValues= ruleArrayValues EOF )
            // InternalActivityDiagram.g:2168:2: iv_ruleArrayValues= ruleArrayValues EOF
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
    // InternalActivityDiagram.g:2174:1: ruleArrayValues returns [EObject current=null] : ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' ) ;
    public final EObject ruleArrayValues() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_values_2_0 = null;

        EObject lv_values_4_0 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:2180:2: ( ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' ) )
            // InternalActivityDiagram.g:2181:2: ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' )
            {
            // InternalActivityDiagram.g:2181:2: ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' )
            // InternalActivityDiagram.g:2182:3: () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']'
            {
            // InternalActivityDiagram.g:2182:3: ()
            // InternalActivityDiagram.g:2183:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getArrayValuesAccess().getArrayValuesAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,14,FOLLOW_52); 

            			newLeafNode(otherlv_1, grammarAccess.getArrayValuesAccess().getLeftSquareBracketKeyword_1());
            		
            // InternalActivityDiagram.g:2193:3: ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( ((LA51_0>=RULE_STRING && LA51_0<=RULE_INT)||LA51_0==14||(LA51_0>=57 && LA51_0<=60)) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // InternalActivityDiagram.g:2194:4: ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )*
                    {
                    // InternalActivityDiagram.g:2194:4: ( (lv_values_2_0= rulePrimitiveValue ) )
                    // InternalActivityDiagram.g:2195:5: (lv_values_2_0= rulePrimitiveValue )
                    {
                    // InternalActivityDiagram.g:2195:5: (lv_values_2_0= rulePrimitiveValue )
                    // InternalActivityDiagram.g:2196:6: lv_values_2_0= rulePrimitiveValue
                    {

                    						newCompositeNode(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_0_0());
                    					
                    pushFollow(FOLLOW_53);
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

                    // InternalActivityDiagram.g:2213:4: (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )*
                    loop50:
                    do {
                        int alt50=2;
                        int LA50_0 = input.LA(1);

                        if ( (LA50_0==15) ) {
                            alt50=1;
                        }


                        switch (alt50) {
                    	case 1 :
                    	    // InternalActivityDiagram.g:2214:5: otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) )
                    	    {
                    	    otherlv_3=(Token)match(input,15,FOLLOW_46); 

                    	    					newLeafNode(otherlv_3, grammarAccess.getArrayValuesAccess().getCommaKeyword_2_1_0());
                    	    				
                    	    // InternalActivityDiagram.g:2218:5: ( (lv_values_4_0= rulePrimitiveValue ) )
                    	    // InternalActivityDiagram.g:2219:6: (lv_values_4_0= rulePrimitiveValue )
                    	    {
                    	    // InternalActivityDiagram.g:2219:6: (lv_values_4_0= rulePrimitiveValue )
                    	    // InternalActivityDiagram.g:2220:7: lv_values_4_0= rulePrimitiveValue
                    	    {

                    	    							newCompositeNode(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_1_1_0());
                    	    						
                    	    pushFollow(FOLLOW_53);
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
                    	    break loop50;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,16,FOLLOW_2); 

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
    // InternalActivityDiagram.g:2247:1: entryRuleArrayType returns [EObject current=null] : iv_ruleArrayType= ruleArrayType EOF ;
    public final EObject entryRuleArrayType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayType = null;


        try {
            // InternalActivityDiagram.g:2247:50: (iv_ruleArrayType= ruleArrayType EOF )
            // InternalActivityDiagram.g:2248:2: iv_ruleArrayType= ruleArrayType EOF
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
    // InternalActivityDiagram.g:2254:1: ruleArrayType returns [EObject current=null] : ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? ) ;
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
            // InternalActivityDiagram.g:2260:2: ( ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? ) )
            // InternalActivityDiagram.g:2261:2: ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? )
            {
            // InternalActivityDiagram.g:2261:2: ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? )
            // InternalActivityDiagram.g:2262:3: () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )?
            {
            // InternalActivityDiagram.g:2262:3: ()
            // InternalActivityDiagram.g:2263:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getArrayTypeAccess().getArrayTypeAction_0(),
            					current);
            			

            }

            // InternalActivityDiagram.g:2269:3: ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) )
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( ((LA52_0>=67 && LA52_0<=72)) ) {
                alt52=1;
            }
            else if ( (LA52_0==RULE_ID) ) {
                alt52=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // InternalActivityDiagram.g:2270:4: ( (lv_primitiveType_1_0= rulePrimitiveValueType ) )
                    {
                    // InternalActivityDiagram.g:2270:4: ( (lv_primitiveType_1_0= rulePrimitiveValueType ) )
                    // InternalActivityDiagram.g:2271:5: (lv_primitiveType_1_0= rulePrimitiveValueType )
                    {
                    // InternalActivityDiagram.g:2271:5: (lv_primitiveType_1_0= rulePrimitiveValueType )
                    // InternalActivityDiagram.g:2272:6: lv_primitiveType_1_0= rulePrimitiveValueType
                    {

                    						newCompositeNode(grammarAccess.getArrayTypeAccess().getPrimitiveTypePrimitiveValueTypeEnumRuleCall_1_0_0());
                    					
                    pushFollow(FOLLOW_6);
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
                    // InternalActivityDiagram.g:2290:4: ( ( ruleQualifiedName ) )
                    {
                    // InternalActivityDiagram.g:2290:4: ( ( ruleQualifiedName ) )
                    // InternalActivityDiagram.g:2291:5: ( ruleQualifiedName )
                    {
                    // InternalActivityDiagram.g:2291:5: ( ruleQualifiedName )
                    // InternalActivityDiagram.g:2292:6: ruleQualifiedName
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getArrayTypeRule());
                    						}
                    					

                    						newCompositeNode(grammarAccess.getArrayTypeAccess().getDataModelTypeDataModelCrossReference_1_1_0());
                    					
                    pushFollow(FOLLOW_6);
                    ruleQualifiedName();

                    state._fsp--;


                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            otherlv_3=(Token)match(input,14,FOLLOW_54); 

            			newLeafNode(otherlv_3, grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_2());
            		
            otherlv_4=(Token)match(input,16,FOLLOW_3); 

            			newLeafNode(otherlv_4, grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_3());
            		
            // InternalActivityDiagram.g:2315:3: ( (lv_name_5_0= ruleEString ) )
            // InternalActivityDiagram.g:2316:4: (lv_name_5_0= ruleEString )
            {
            // InternalActivityDiagram.g:2316:4: (lv_name_5_0= ruleEString )
            // InternalActivityDiagram.g:2317:5: lv_name_5_0= ruleEString
            {

            					newCompositeNode(grammarAccess.getArrayTypeAccess().getNameEStringParserRuleCall_4_0());
            				
            pushFollow(FOLLOW_51);
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

            // InternalActivityDiagram.g:2334:3: (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==53) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // InternalActivityDiagram.g:2335:4: otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']'
                    {
                    otherlv_6=(Token)match(input,53,FOLLOW_6); 

                    				newLeafNode(otherlv_6, grammarAccess.getArrayTypeAccess().getEqualsSignKeyword_5_0());
                    			
                    otherlv_7=(Token)match(input,14,FOLLOW_52); 

                    				newLeafNode(otherlv_7, grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_5_1());
                    			
                    // InternalActivityDiagram.g:2343:4: ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( ((LA54_0>=RULE_STRING && LA54_0<=RULE_INT)||LA54_0==14||(LA54_0>=57 && LA54_0<=60)) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // InternalActivityDiagram.g:2344:5: ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )*
                            {
                            // InternalActivityDiagram.g:2344:5: ( (lv_values_8_0= rulePrimitiveValue ) )
                            // InternalActivityDiagram.g:2345:6: (lv_values_8_0= rulePrimitiveValue )
                            {
                            // InternalActivityDiagram.g:2345:6: (lv_values_8_0= rulePrimitiveValue )
                            // InternalActivityDiagram.g:2346:7: lv_values_8_0= rulePrimitiveValue
                            {

                            							newCompositeNode(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_0_0());
                            						
                            pushFollow(FOLLOW_53);
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

                            // InternalActivityDiagram.g:2363:5: (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )*
                            loop53:
                            do {
                                int alt53=2;
                                int LA53_0 = input.LA(1);

                                if ( (LA53_0==15) ) {
                                    alt53=1;
                                }


                                switch (alt53) {
                            	case 1 :
                            	    // InternalActivityDiagram.g:2364:6: otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) )
                            	    {
                            	    otherlv_9=(Token)match(input,15,FOLLOW_46); 

                            	    						newLeafNode(otherlv_9, grammarAccess.getArrayTypeAccess().getCommaKeyword_5_2_1_0());
                            	    					
                            	    // InternalActivityDiagram.g:2368:6: ( (lv_values_10_0= rulePrimitiveValue ) )
                            	    // InternalActivityDiagram.g:2369:7: (lv_values_10_0= rulePrimitiveValue )
                            	    {
                            	    // InternalActivityDiagram.g:2369:7: (lv_values_10_0= rulePrimitiveValue )
                            	    // InternalActivityDiagram.g:2370:8: lv_values_10_0= rulePrimitiveValue
                            	    {

                            	    								newCompositeNode(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_1_1_0());
                            	    							
                            	    pushFollow(FOLLOW_53);
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
                            	    break loop53;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_11=(Token)match(input,16,FOLLOW_2); 

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
    // InternalActivityDiagram.g:2398:1: entryRuleEString returns [String current=null] : iv_ruleEString= ruleEString EOF ;
    public final String entryRuleEString() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEString = null;


        try {
            // InternalActivityDiagram.g:2398:47: (iv_ruleEString= ruleEString EOF )
            // InternalActivityDiagram.g:2399:2: iv_ruleEString= ruleEString EOF
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
    // InternalActivityDiagram.g:2405:1: ruleEString returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleEString() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;
        Token this_ID_1=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2411:2: ( (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID ) )
            // InternalActivityDiagram.g:2412:2: (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID )
            {
            // InternalActivityDiagram.g:2412:2: (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==RULE_STRING) ) {
                alt56=1;
            }
            else if ( (LA56_0==RULE_ID) ) {
                alt56=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // InternalActivityDiagram.g:2413:3: this_STRING_0= RULE_STRING
                    {
                    this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

                    			current.merge(this_STRING_0);
                    		

                    			newLeafNode(this_STRING_0, grammarAccess.getEStringAccess().getSTRINGTerminalRuleCall_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalActivityDiagram.g:2421:3: this_ID_1= RULE_ID
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
    // InternalActivityDiagram.g:2432:1: entryRuleEInt returns [String current=null] : iv_ruleEInt= ruleEInt EOF ;
    public final String entryRuleEInt() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEInt = null;


        try {
            // InternalActivityDiagram.g:2432:44: (iv_ruleEInt= ruleEInt EOF )
            // InternalActivityDiagram.g:2433:2: iv_ruleEInt= ruleEInt EOF
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
    // InternalActivityDiagram.g:2439:1: ruleEInt returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? this_INT_1= RULE_INT ) ;
    public final AntlrDatatypeRuleToken ruleEInt() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2445:2: ( ( (kw= '-' )? this_INT_1= RULE_INT ) )
            // InternalActivityDiagram.g:2446:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            {
            // InternalActivityDiagram.g:2446:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            // InternalActivityDiagram.g:2447:3: (kw= '-' )? this_INT_1= RULE_INT
            {
            // InternalActivityDiagram.g:2447:3: (kw= '-' )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==58) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // InternalActivityDiagram.g:2448:4: kw= '-'
                    {
                    kw=(Token)match(input,58,FOLLOW_55); 

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
    // InternalActivityDiagram.g:2465:1: entryRuleEBoolean returns [String current=null] : iv_ruleEBoolean= ruleEBoolean EOF ;
    public final String entryRuleEBoolean() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEBoolean = null;


        try {
            // InternalActivityDiagram.g:2465:48: (iv_ruleEBoolean= ruleEBoolean EOF )
            // InternalActivityDiagram.g:2466:2: iv_ruleEBoolean= ruleEBoolean EOF
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
    // InternalActivityDiagram.g:2472:1: ruleEBoolean returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'false' | kw= 'true' ) ;
    public final AntlrDatatypeRuleToken ruleEBoolean() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2478:2: ( (kw= 'false' | kw= 'true' ) )
            // InternalActivityDiagram.g:2479:2: (kw= 'false' | kw= 'true' )
            {
            // InternalActivityDiagram.g:2479:2: (kw= 'false' | kw= 'true' )
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==59) ) {
                alt58=1;
            }
            else if ( (LA58_0==60) ) {
                alt58=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 58, 0, input);

                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // InternalActivityDiagram.g:2480:3: kw= 'false'
                    {
                    kw=(Token)match(input,59,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getEBooleanAccess().getFalseKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalActivityDiagram.g:2486:3: kw= 'true'
                    {
                    kw=(Token)match(input,60,FOLLOW_2); 

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
    // InternalActivityDiagram.g:2495:1: entryRuleEFloat returns [String current=null] : iv_ruleEFloat= ruleEFloat EOF ;
    public final String entryRuleEFloat() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEFloat = null;


        try {
            // InternalActivityDiagram.g:2495:46: (iv_ruleEFloat= ruleEFloat EOF )
            // InternalActivityDiagram.g:2496:2: iv_ruleEFloat= ruleEFloat EOF
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
    // InternalActivityDiagram.g:2502:1: ruleEFloat returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? ) ;
    public final AntlrDatatypeRuleToken ruleEFloat() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;
        Token this_INT_3=null;
        Token this_INT_7=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2508:2: ( ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? ) )
            // InternalActivityDiagram.g:2509:2: ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? )
            {
            // InternalActivityDiagram.g:2509:2: ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? )
            // InternalActivityDiagram.g:2510:3: (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )?
            {
            // InternalActivityDiagram.g:2510:3: (kw= '-' )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==58) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // InternalActivityDiagram.g:2511:4: kw= '-'
                    {
                    kw=(Token)match(input,58,FOLLOW_56); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getEFloatAccess().getHyphenMinusKeyword_0());
                    			

                    }
                    break;

            }

            // InternalActivityDiagram.g:2517:3: (this_INT_1= RULE_INT )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==RULE_INT) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // InternalActivityDiagram.g:2518:4: this_INT_1= RULE_INT
                    {
                    this_INT_1=(Token)match(input,RULE_INT,FOLLOW_57); 

                    				current.merge(this_INT_1);
                    			

                    				newLeafNode(this_INT_1, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_1());
                    			

                    }
                    break;

            }

            kw=(Token)match(input,57,FOLLOW_55); 

            			current.merge(kw);
            			newLeafNode(kw, grammarAccess.getEFloatAccess().getFullStopKeyword_2());
            		
            this_INT_3=(Token)match(input,RULE_INT,FOLLOW_58); 

            			current.merge(this_INT_3);
            		

            			newLeafNode(this_INT_3, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_3());
            		
            // InternalActivityDiagram.g:2538:3: ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( ((LA63_0>=61 && LA63_0<=62)) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // InternalActivityDiagram.g:2539:4: (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT
                    {
                    // InternalActivityDiagram.g:2539:4: (kw= 'E' | kw= 'e' )
                    int alt61=2;
                    int LA61_0 = input.LA(1);

                    if ( (LA61_0==61) ) {
                        alt61=1;
                    }
                    else if ( (LA61_0==62) ) {
                        alt61=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 61, 0, input);

                        throw nvae;
                    }
                    switch (alt61) {
                        case 1 :
                            // InternalActivityDiagram.g:2540:5: kw= 'E'
                            {
                            kw=(Token)match(input,61,FOLLOW_59); 

                            					current.merge(kw);
                            					newLeafNode(kw, grammarAccess.getEFloatAccess().getEKeyword_4_0_0());
                            				

                            }
                            break;
                        case 2 :
                            // InternalActivityDiagram.g:2546:5: kw= 'e'
                            {
                            kw=(Token)match(input,62,FOLLOW_59); 

                            					current.merge(kw);
                            					newLeafNode(kw, grammarAccess.getEFloatAccess().getEKeyword_4_0_1());
                            				

                            }
                            break;

                    }

                    // InternalActivityDiagram.g:2552:4: (kw= '-' )?
                    int alt62=2;
                    int LA62_0 = input.LA(1);

                    if ( (LA62_0==58) ) {
                        alt62=1;
                    }
                    switch (alt62) {
                        case 1 :
                            // InternalActivityDiagram.g:2553:5: kw= '-'
                            {
                            kw=(Token)match(input,58,FOLLOW_55); 

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
    // InternalActivityDiagram.g:2571:1: entryRuleEDate returns [String current=null] : iv_ruleEDate= ruleEDate EOF ;
    public final String entryRuleEDate() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEDate = null;


        try {
            // InternalActivityDiagram.g:2571:45: (iv_ruleEDate= ruleEDate EOF )
            // InternalActivityDiagram.g:2572:2: iv_ruleEDate= ruleEDate EOF
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
    // InternalActivityDiagram.g:2578:1: ruleEDate returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear ) ;
    public final AntlrDatatypeRuleToken ruleEDate() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_Day_0 = null;

        AntlrDatatypeRuleToken this_Month_2 = null;

        AntlrDatatypeRuleToken this_Year_4 = null;



        	enterRule();

        try {
            // InternalActivityDiagram.g:2584:2: ( (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear ) )
            // InternalActivityDiagram.g:2585:2: (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear )
            {
            // InternalActivityDiagram.g:2585:2: (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear )
            // InternalActivityDiagram.g:2586:3: this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear
            {

            			newCompositeNode(grammarAccess.getEDateAccess().getDayParserRuleCall_0());
            		
            pushFollow(FOLLOW_60);
            this_Day_0=ruleDay();

            state._fsp--;


            			current.merge(this_Day_0);
            		

            			afterParserOrEnumRuleCall();
            		
            kw=(Token)match(input,58,FOLLOW_55); 

            			current.merge(kw);
            			newLeafNode(kw, grammarAccess.getEDateAccess().getHyphenMinusKeyword_1());
            		

            			newCompositeNode(grammarAccess.getEDateAccess().getMonthParserRuleCall_2());
            		
            pushFollow(FOLLOW_60);
            this_Month_2=ruleMonth();

            state._fsp--;


            			current.merge(this_Month_2);
            		

            			afterParserOrEnumRuleCall();
            		
            kw=(Token)match(input,58,FOLLOW_55); 

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
    // InternalActivityDiagram.g:2630:1: entryRuleDay returns [String current=null] : iv_ruleDay= ruleDay EOF ;
    public final String entryRuleDay() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDay = null;


        try {
            // InternalActivityDiagram.g:2630:43: (iv_ruleDay= ruleDay EOF )
            // InternalActivityDiagram.g:2631:2: iv_ruleDay= ruleDay EOF
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
    // InternalActivityDiagram.g:2637:1: ruleDay returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleDay() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2643:2: (this_INT_0= RULE_INT )
            // InternalActivityDiagram.g:2644:2: this_INT_0= RULE_INT
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
    // InternalActivityDiagram.g:2654:1: entryRuleMonth returns [String current=null] : iv_ruleMonth= ruleMonth EOF ;
    public final String entryRuleMonth() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleMonth = null;


        try {
            // InternalActivityDiagram.g:2654:45: (iv_ruleMonth= ruleMonth EOF )
            // InternalActivityDiagram.g:2655:2: iv_ruleMonth= ruleMonth EOF
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
    // InternalActivityDiagram.g:2661:1: ruleMonth returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleMonth() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2667:2: (this_INT_0= RULE_INT )
            // InternalActivityDiagram.g:2668:2: this_INT_0= RULE_INT
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
    // InternalActivityDiagram.g:2678:1: entryRuleYear returns [String current=null] : iv_ruleYear= ruleYear EOF ;
    public final String entryRuleYear() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleYear = null;


        try {
            // InternalActivityDiagram.g:2678:44: (iv_ruleYear= ruleYear EOF )
            // InternalActivityDiagram.g:2679:2: iv_ruleYear= ruleYear EOF
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
    // InternalActivityDiagram.g:2685:1: ruleYear returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleYear() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2691:2: (this_INT_0= RULE_INT )
            // InternalActivityDiagram.g:2692:2: this_INT_0= RULE_INT
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


    // $ANTLR start "ruleUnitTime"
    // InternalActivityDiagram.g:2702:1: ruleUnitTime returns [Enumerator current=null] : ( (enumLiteral_0= 'secs' ) | (enumLiteral_1= 'mins' ) | (enumLiteral_2= 'hrs' ) | (enumLiteral_3= 'days' ) ) ;
    public final Enumerator ruleUnitTime() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;


        	enterRule();

        try {
            // InternalActivityDiagram.g:2708:2: ( ( (enumLiteral_0= 'secs' ) | (enumLiteral_1= 'mins' ) | (enumLiteral_2= 'hrs' ) | (enumLiteral_3= 'days' ) ) )
            // InternalActivityDiagram.g:2709:2: ( (enumLiteral_0= 'secs' ) | (enumLiteral_1= 'mins' ) | (enumLiteral_2= 'hrs' ) | (enumLiteral_3= 'days' ) )
            {
            // InternalActivityDiagram.g:2709:2: ( (enumLiteral_0= 'secs' ) | (enumLiteral_1= 'mins' ) | (enumLiteral_2= 'hrs' ) | (enumLiteral_3= 'days' ) )
            int alt64=4;
            switch ( input.LA(1) ) {
            case 63:
                {
                alt64=1;
                }
                break;
            case 64:
                {
                alt64=2;
                }
                break;
            case 65:
                {
                alt64=3;
                }
                break;
            case 66:
                {
                alt64=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }

            switch (alt64) {
                case 1 :
                    // InternalActivityDiagram.g:2710:3: (enumLiteral_0= 'secs' )
                    {
                    // InternalActivityDiagram.g:2710:3: (enumLiteral_0= 'secs' )
                    // InternalActivityDiagram.g:2711:4: enumLiteral_0= 'secs'
                    {
                    enumLiteral_0=(Token)match(input,63,FOLLOW_2); 

                    				current = grammarAccess.getUnitTimeAccess().getSecsEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getUnitTimeAccess().getSecsEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalActivityDiagram.g:2718:3: (enumLiteral_1= 'mins' )
                    {
                    // InternalActivityDiagram.g:2718:3: (enumLiteral_1= 'mins' )
                    // InternalActivityDiagram.g:2719:4: enumLiteral_1= 'mins'
                    {
                    enumLiteral_1=(Token)match(input,64,FOLLOW_2); 

                    				current = grammarAccess.getUnitTimeAccess().getMinsEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getUnitTimeAccess().getMinsEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalActivityDiagram.g:2726:3: (enumLiteral_2= 'hrs' )
                    {
                    // InternalActivityDiagram.g:2726:3: (enumLiteral_2= 'hrs' )
                    // InternalActivityDiagram.g:2727:4: enumLiteral_2= 'hrs'
                    {
                    enumLiteral_2=(Token)match(input,65,FOLLOW_2); 

                    				current = grammarAccess.getUnitTimeAccess().getHrsEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getUnitTimeAccess().getHrsEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;
                case 4 :
                    // InternalActivityDiagram.g:2734:3: (enumLiteral_3= 'days' )
                    {
                    // InternalActivityDiagram.g:2734:3: (enumLiteral_3= 'days' )
                    // InternalActivityDiagram.g:2735:4: enumLiteral_3= 'days'
                    {
                    enumLiteral_3=(Token)match(input,66,FOLLOW_2); 

                    				current = grammarAccess.getUnitTimeAccess().getDaysEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_3, grammarAccess.getUnitTimeAccess().getDaysEnumLiteralDeclaration_3());
                    			

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
    // $ANTLR end "ruleUnitTime"


    // $ANTLR start "rulePrimitiveValueType"
    // InternalActivityDiagram.g:2745:1: rulePrimitiveValueType returns [Enumerator current=null] : ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) ) ;
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
            // InternalActivityDiagram.g:2751:2: ( ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) ) )
            // InternalActivityDiagram.g:2752:2: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) )
            {
            // InternalActivityDiagram.g:2752:2: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) )
            int alt65=6;
            switch ( input.LA(1) ) {
            case 67:
                {
                alt65=1;
                }
                break;
            case 68:
                {
                alt65=2;
                }
                break;
            case 69:
                {
                alt65=3;
                }
                break;
            case 70:
                {
                alt65=4;
                }
                break;
            case 71:
                {
                alt65=5;
                }
                break;
            case 72:
                {
                alt65=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 65, 0, input);

                throw nvae;
            }

            switch (alt65) {
                case 1 :
                    // InternalActivityDiagram.g:2753:3: (enumLiteral_0= 'int' )
                    {
                    // InternalActivityDiagram.g:2753:3: (enumLiteral_0= 'int' )
                    // InternalActivityDiagram.g:2754:4: enumLiteral_0= 'int'
                    {
                    enumLiteral_0=(Token)match(input,67,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalActivityDiagram.g:2761:3: (enumLiteral_1= 'boolean' )
                    {
                    // InternalActivityDiagram.g:2761:3: (enumLiteral_1= 'boolean' )
                    // InternalActivityDiagram.g:2762:4: enumLiteral_1= 'boolean'
                    {
                    enumLiteral_1=(Token)match(input,68,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalActivityDiagram.g:2769:3: (enumLiteral_2= 'float' )
                    {
                    // InternalActivityDiagram.g:2769:3: (enumLiteral_2= 'float' )
                    // InternalActivityDiagram.g:2770:4: enumLiteral_2= 'float'
                    {
                    enumLiteral_2=(Token)match(input,69,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;
                case 4 :
                    // InternalActivityDiagram.g:2777:3: (enumLiteral_3= 'string' )
                    {
                    // InternalActivityDiagram.g:2777:3: (enumLiteral_3= 'string' )
                    // InternalActivityDiagram.g:2778:4: enumLiteral_3= 'string'
                    {
                    enumLiteral_3=(Token)match(input,70,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_3, grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3());
                    			

                    }


                    }
                    break;
                case 5 :
                    // InternalActivityDiagram.g:2785:3: (enumLiteral_4= 'object' )
                    {
                    // InternalActivityDiagram.g:2785:3: (enumLiteral_4= 'object' )
                    // InternalActivityDiagram.g:2786:4: enumLiteral_4= 'object'
                    {
                    enumLiteral_4=(Token)match(input,71,FOLLOW_2); 

                    				current = grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_4, grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4());
                    			

                    }


                    }
                    break;
                case 6 :
                    // InternalActivityDiagram.g:2793:3: (enumLiteral_5= 'date' )
                    {
                    // InternalActivityDiagram.g:2793:3: (enumLiteral_5= 'date' )
                    // InternalActivityDiagram.g:2794:4: enumLiteral_5= 'date'
                    {
                    enumLiteral_5=(Token)match(input,72,FOLLOW_2); 

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


    protected DFA45 dfa45 = new DFA45(this);
    protected DFA49 dfa49 = new DFA49(this);
    static final String dfa_1s = "\15\uffff";
    static final String dfa_2s = "\1\5\7\4\2\uffff\1\5\1\uffff\1\4";
    static final String dfa_3s = "\1\110\6\16\1\71\2\uffff\1\5\1\uffff\1\71";
    static final String dfa_4s = "\10\uffff\1\3\1\1\1\uffff\1\2\1\uffff";
    static final String dfa_5s = "\15\uffff}>";
    static final String[] dfa_6s = {
            "\1\7\75\uffff\1\1\1\2\1\3\1\4\1\5\1\6",
            "\2\11\10\uffff\1\10",
            "\2\11\10\uffff\1\10",
            "\2\11\10\uffff\1\10",
            "\2\11\10\uffff\1\10",
            "\2\11\10\uffff\1\10",
            "\2\11\10\uffff\1\10",
            "\2\13\10\uffff\1\10\52\uffff\1\12",
            "",
            "",
            "\1\14",
            "",
            "\2\13\10\uffff\1\10\52\uffff\1\12"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA45 extends DFA {

        public DFA45(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "1697:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )";
        }
    }
    static final String dfa_7s = "\13\uffff";
    static final String dfa_8s = "\2\uffff\1\12\5\uffff\1\12\2\uffff";
    static final String dfa_9s = "\1\4\1\6\1\17\5\uffff\1\17\2\uffff";
    static final String dfa_10s = "\1\74\1\71\1\72\5\uffff\1\71\2\uffff";
    static final String dfa_11s = "\3\uffff\1\2\1\3\1\4\1\6\1\7\1\uffff\1\5\1\1";
    static final String dfa_12s = "\13\uffff}>";
    static final String[] dfa_13s = {
            "\1\4\1\7\1\2\7\uffff\1\6\52\uffff\1\3\1\1\2\5",
            "\1\10\62\uffff\1\3",
            "\2\12\7\uffff\1\12\3\uffff\1\12\15\uffff\2\12\1\uffff\2\12\1\uffff\1\12\3\uffff\2\12\3\uffff\1\3\1\11",
            "",
            "",
            "",
            "",
            "",
            "\2\12\7\uffff\1\12\3\uffff\1\12\15\uffff\2\12\1\uffff\2\12\1\uffff\1\12\3\uffff\2\12\3\uffff\1\3",
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

    class DFA49 extends DFA {

        public DFA49(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 49;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_9;
            this.max = dfa_10;
            this.accept = dfa_11;
            this.special = dfa_12;
            this.transition = dfa_13;
        }
        public String getDescription() {
            return "1957:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )";
        }
    }
 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x00000000022B1002L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x00000000022B1022L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x00000000022B9002L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000002288002L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000002280022L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000002208002L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000000020L,0x00000000000001F8L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000001008000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000010008000L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000F40000000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000F00000000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000000E00000000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x000003F018000000L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x000003F010000000L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000000001000020L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0031EC0001000000L});
    public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0000038010000000L});
    public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x0600000000000040L});
    public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000007L});
    public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0000030010000000L});
    public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_38 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_39 = new BitSet(new long[]{0x00006C0000000000L});
    public static final BitSet FOLLOW_40 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_41 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_42 = new BitSet(new long[]{0x0031000001000002L});
    public static final BitSet FOLLOW_43 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_44 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_45 = new BitSet(new long[]{0x0039000001000002L});
    public static final BitSet FOLLOW_46 = new BitSet(new long[]{0x1E00000000004070L});
    public static final BitSet FOLLOW_47 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_48 = new BitSet(new long[]{0x01C0000010000002L});
    public static final BitSet FOLLOW_49 = new BitSet(new long[]{0x0140000010000002L});
    public static final BitSet FOLLOW_50 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_51 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_52 = new BitSet(new long[]{0x1E00000000014070L});
    public static final BitSet FOLLOW_53 = new BitSet(new long[]{0x0000000000018000L});
    public static final BitSet FOLLOW_54 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_55 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_56 = new BitSet(new long[]{0x0200000000000040L});
    public static final BitSet FOLLOW_57 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_58 = new BitSet(new long[]{0x6000000000000002L});
    public static final BitSet FOLLOW_59 = new BitSet(new long[]{0x0400000000000040L});
    public static final BitSet FOLLOW_60 = new BitSet(new long[]{0x0400000000000000L});

}
