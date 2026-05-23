package com.mncml.dsl.parser.antlr.internal;

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
import com.mncml.dsl.services.MncGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalMncParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_ADDRESSFORMAT", "RULE_STRING", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'Model'", "'import'", "'.*'", "'async'", "'['", "','", "']'", "'Publish'", "'level'", "'='", "'startStates'", "':'", "'endStates'", "'SubscribableItemList'", "'{'", "'subscribedEvents'", "'subscribedAlarms'", "'subscribedDataPoints'", "'}'", "'Action'", "'raise'", "'alarms'", "'fire'", "'commands'", "'generate'", "'events'", "'trigger'", "'data'", "'execute'", "'operations'", "'transition'", "'states'", "'('", "')'", "'->'", "'expected'", "'InterfaceDescription'", "'uses'", "'dataPoints'", "'responses'", "'operatingStates'", "'ControlNode'", "'implements'", "'interface'", "'childNodes'", "'CommandResponseBlock'", "'EventBlock'", "'AlarmBlock'", "'DataPointBlock'", "'Command'", "'Generate'", "'Response'", "'expectedResponse'", "'ResponseAggregation'", "'received'", "'Responses'", "'parameterTranslations'", "'and'", "'or'", "'parameters'", "'operation'", "'Max'", "'Value'", "'Min'", "'Possible'", "'Values'", "'Validate'", "'onFail'", "'onSuccess'", "'currentState'", "'any'", "'exitAction'", "'=>'", "'nextState'", "'entryAction'", "'inputParameters'", "'translatedParameters'", "'Event'", "'Alarm'", "'DataPoint'", "'IPaddress'", "'port'", "'DataModel'", "'primitives'", "'composites'", "'.'", "'-'", "'false'", "'true'", "'E'", "'e'", "'int'", "'boolean'", "'float'", "'string'", "'object'", "'date'"
    };
    public static final int T__50=50;
    public static final int T__59=59;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__60=60;
    public static final int T__61=61;
    public static final int RULE_ID=4;
    public static final int RULE_INT=7;
    public static final int T__66=66;
    public static final int RULE_ML_COMMENT=8;
    public static final int T__67=67;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int RULE_ADDRESSFORMAT=5;
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
    public static final int T__91=91;
    public static final int T__100=100;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int T__102=102;
    public static final int T__94=94;
    public static final int T__101=101;
    public static final int T__90=90;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__99=99;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int T__95=95;
    public static final int T__96=96;
    public static final int T__97=97;
    public static final int T__98=98;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int RULE_STRING=6;
    public static final int RULE_SL_COMMENT=9;
    public static final int T__77=77;
    public static final int T__78=78;
    public static final int T__79=79;
    public static final int T__73=73;
    public static final int EOF=-1;
    public static final int T__74=74;
    public static final int T__75=75;
    public static final int T__76=76;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int RULE_WS=10;
    public static final int RULE_ANY_OTHER=11;
    public static final int T__88=88;
    public static final int T__108=108;
    public static final int T__89=89;
    public static final int T__107=107;
    public static final int T__84=84;
    public static final int T__104=104;
    public static final int T__85=85;
    public static final int T__103=103;
    public static final int T__86=86;
    public static final int T__106=106;
    public static final int T__87=87;
    public static final int T__105=105;

    // delegates
    // delegators


        public InternalMncParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalMncParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalMncParser.tokenNames; }
    public String getGrammarFileName() { return "InternalMnc.g"; }



     	private MncGrammarAccess grammarAccess;

        public InternalMncParser(TokenStream input, MncGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Model";
       	}

       	@Override
       	protected MncGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleModel"
    // InternalMnc.g:65:1: entryRuleModel returns [EObject current=null] : iv_ruleModel= ruleModel EOF ;
    public final EObject entryRuleModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModel = null;


        try {
            // InternalMnc.g:65:46: (iv_ruleModel= ruleModel EOF )
            // InternalMnc.g:66:2: iv_ruleModel= ruleModel EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getModelRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleModel=ruleModel();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleModel; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleModel"


    // $ANTLR start "ruleModel"
    // InternalMnc.g:72:1: ruleModel returns [EObject current=null] : ( () ( (lv_importSection_1_0= ruleImport ) )* otherlv_2= 'Model' ( (lv_name_3_0= ruleEString ) ) ( ( (lv_systems_4_0= ruleInterfaceDescription ) ) ( (lv_systems_5_0= ruleControlNode ) )? ) ) ;
    public final EObject ruleModel() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject lv_importSection_1_0 = null;

        AntlrDatatypeRuleToken lv_name_3_0 = null;

        EObject lv_systems_4_0 = null;

        EObject lv_systems_5_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:78:2: ( ( () ( (lv_importSection_1_0= ruleImport ) )* otherlv_2= 'Model' ( (lv_name_3_0= ruleEString ) ) ( ( (lv_systems_4_0= ruleInterfaceDescription ) ) ( (lv_systems_5_0= ruleControlNode ) )? ) ) )
            // InternalMnc.g:79:2: ( () ( (lv_importSection_1_0= ruleImport ) )* otherlv_2= 'Model' ( (lv_name_3_0= ruleEString ) ) ( ( (lv_systems_4_0= ruleInterfaceDescription ) ) ( (lv_systems_5_0= ruleControlNode ) )? ) )
            {
            // InternalMnc.g:79:2: ( () ( (lv_importSection_1_0= ruleImport ) )* otherlv_2= 'Model' ( (lv_name_3_0= ruleEString ) ) ( ( (lv_systems_4_0= ruleInterfaceDescription ) ) ( (lv_systems_5_0= ruleControlNode ) )? ) )
            // InternalMnc.g:80:3: () ( (lv_importSection_1_0= ruleImport ) )* otherlv_2= 'Model' ( (lv_name_3_0= ruleEString ) ) ( ( (lv_systems_4_0= ruleInterfaceDescription ) ) ( (lv_systems_5_0= ruleControlNode ) )? )
            {
            // InternalMnc.g:80:3: ()
            // InternalMnc.g:81:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getModelAccess().getModelAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:87:3: ( (lv_importSection_1_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalMnc.g:88:4: (lv_importSection_1_0= ruleImport )
            	    {
            	    // InternalMnc.g:88:4: (lv_importSection_1_0= ruleImport )
            	    // InternalMnc.g:89:5: lv_importSection_1_0= ruleImport
            	    {
            	    if ( state.backtracking==0 ) {

            	      					newCompositeNode(grammarAccess.getModelAccess().getImportSectionImportParserRuleCall_1_0());
            	      				
            	    }
            	    pushFollow(FOLLOW_3);
            	    lv_importSection_1_0=ruleImport();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					if (current==null) {
            	      						current = createModelElementForParent(grammarAccess.getModelRule());
            	      					}
            	      					add(
            	      						current,
            	      						"importSection",
            	      						lv_importSection_1_0,
            	      						"com.mncml.dsl.Mnc.Import");
            	      					afterParserOrEnumRuleCall();
            	      				
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            otherlv_2=(Token)match(input,12,FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getModelAccess().getModelKeyword_2());
              		
            }
            // InternalMnc.g:110:3: ( (lv_name_3_0= ruleEString ) )
            // InternalMnc.g:111:4: (lv_name_3_0= ruleEString )
            {
            // InternalMnc.g:111:4: (lv_name_3_0= ruleEString )
            // InternalMnc.g:112:5: lv_name_3_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getModelAccess().getNameEStringParserRuleCall_3_0());
              				
            }
            pushFollow(FOLLOW_5);
            lv_name_3_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getModelRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_3_0,
              						"com.dml.dsl.Dml.EString");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:129:3: ( ( (lv_systems_4_0= ruleInterfaceDescription ) ) ( (lv_systems_5_0= ruleControlNode ) )? )
            // InternalMnc.g:130:4: ( (lv_systems_4_0= ruleInterfaceDescription ) ) ( (lv_systems_5_0= ruleControlNode ) )?
            {
            // InternalMnc.g:130:4: ( (lv_systems_4_0= ruleInterfaceDescription ) )
            // InternalMnc.g:131:5: (lv_systems_4_0= ruleInterfaceDescription )
            {
            // InternalMnc.g:131:5: (lv_systems_4_0= ruleInterfaceDescription )
            // InternalMnc.g:132:6: lv_systems_4_0= ruleInterfaceDescription
            {
            if ( state.backtracking==0 ) {

              						newCompositeNode(grammarAccess.getModelAccess().getSystemsInterfaceDescriptionParserRuleCall_4_0_0());
              					
            }
            pushFollow(FOLLOW_6);
            lv_systems_4_0=ruleInterfaceDescription();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              						if (current==null) {
              							current = createModelElementForParent(grammarAccess.getModelRule());
              						}
              						add(
              							current,
              							"systems",
              							lv_systems_4_0,
              							"com.mncml.dsl.Mnc.InterfaceDescription");
              						afterParserOrEnumRuleCall();
              					
            }

            }


            }

            // InternalMnc.g:149:4: ( (lv_systems_5_0= ruleControlNode ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==53) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // InternalMnc.g:150:5: (lv_systems_5_0= ruleControlNode )
                    {
                    // InternalMnc.g:150:5: (lv_systems_5_0= ruleControlNode )
                    // InternalMnc.g:151:6: lv_systems_5_0= ruleControlNode
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getModelAccess().getSystemsControlNodeParserRuleCall_4_1_0());
                      					
                    }
                    pushFollow(FOLLOW_2);
                    lv_systems_5_0=ruleControlNode();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getModelRule());
                      						}
                      						add(
                      							current,
                      							"systems",
                      							lv_systems_5_0,
                      							"com.mncml.dsl.Mnc.ControlNode");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }
                    break;

            }


            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleModel"


    // $ANTLR start "entryRuleImport"
    // InternalMnc.g:173:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // InternalMnc.g:173:47: (iv_ruleImport= ruleImport EOF )
            // InternalMnc.g:174:2: iv_ruleImport= ruleImport EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getImportRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleImport=ruleImport();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleImport; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleImport"


    // $ANTLR start "ruleImport"
    // InternalMnc.g:180:1: ruleImport returns [EObject current=null] : (otherlv_0= 'import' ( (lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_importedNamespace_1_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:186:2: ( (otherlv_0= 'import' ( (lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard ) ) ) )
            // InternalMnc.g:187:2: (otherlv_0= 'import' ( (lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard ) ) )
            {
            // InternalMnc.g:187:2: (otherlv_0= 'import' ( (lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard ) ) )
            // InternalMnc.g:188:3: otherlv_0= 'import' ( (lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard ) )
            {
            otherlv_0=(Token)match(input,13,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_0, grammarAccess.getImportAccess().getImportKeyword_0());
              		
            }
            // InternalMnc.g:192:3: ( (lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard ) )
            // InternalMnc.g:193:4: (lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard )
            {
            // InternalMnc.g:193:4: (lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard )
            // InternalMnc.g:194:5: lv_importedNamespace_1_0= ruleQualifiedNameWithWildCard
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getImportAccess().getImportedNamespaceQualifiedNameWithWildCardParserRuleCall_1_0());
              				
            }
            pushFollow(FOLLOW_2);
            lv_importedNamespace_1_0=ruleQualifiedNameWithWildCard();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getImportRule());
              					}
              					set(
              						current,
              						"importedNamespace",
              						lv_importedNamespace_1_0,
              						"com.mncml.dsl.Mnc.QualifiedNameWithWildCard");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleImport"


    // $ANTLR start "entryRuleQualifiedNameWithWildCard"
    // InternalMnc.g:215:1: entryRuleQualifiedNameWithWildCard returns [String current=null] : iv_ruleQualifiedNameWithWildCard= ruleQualifiedNameWithWildCard EOF ;
    public final String entryRuleQualifiedNameWithWildCard() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQualifiedNameWithWildCard = null;


        try {
            // InternalMnc.g:215:65: (iv_ruleQualifiedNameWithWildCard= ruleQualifiedNameWithWildCard EOF )
            // InternalMnc.g:216:2: iv_ruleQualifiedNameWithWildCard= ruleQualifiedNameWithWildCard EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getQualifiedNameWithWildCardRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleQualifiedNameWithWildCard=ruleQualifiedNameWithWildCard();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleQualifiedNameWithWildCard.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleQualifiedNameWithWildCard"


    // $ANTLR start "ruleQualifiedNameWithWildCard"
    // InternalMnc.g:222:1: ruleQualifiedNameWithWildCard returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_QualifiedName_0= ruleQualifiedName (kw= '.*' )? ) ;
    public final AntlrDatatypeRuleToken ruleQualifiedNameWithWildCard() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QualifiedName_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:228:2: ( (this_QualifiedName_0= ruleQualifiedName (kw= '.*' )? ) )
            // InternalMnc.g:229:2: (this_QualifiedName_0= ruleQualifiedName (kw= '.*' )? )
            {
            // InternalMnc.g:229:2: (this_QualifiedName_0= ruleQualifiedName (kw= '.*' )? )
            // InternalMnc.g:230:3: this_QualifiedName_0= ruleQualifiedName (kw= '.*' )?
            {
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getQualifiedNameWithWildCardAccess().getQualifiedNameParserRuleCall_0());
              		
            }
            pushFollow(FOLLOW_8);
            this_QualifiedName_0=ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_QualifiedName_0);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
            }
            // InternalMnc.g:240:3: (kw= '.*' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==14) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // InternalMnc.g:241:4: kw= '.*'
                    {
                    kw=(Token)match(input,14,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(kw);
                      				newLeafNode(kw, grammarAccess.getQualifiedNameWithWildCardAccess().getFullStopAsteriskKeyword_1());
                      			
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleQualifiedNameWithWildCard"


    // $ANTLR start "entryRuleCommand"
    // InternalMnc.g:251:1: entryRuleCommand returns [EObject current=null] : iv_ruleCommand= ruleCommand EOF ;
    public final EObject entryRuleCommand() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCommand = null;


        try {
            // InternalMnc.g:251:48: (iv_ruleCommand= ruleCommand EOF )
            // InternalMnc.g:252:2: iv_ruleCommand= ruleCommand EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getCommandRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleCommand=ruleCommand();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleCommand; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleCommand"


    // $ANTLR start "ruleCommand"
    // InternalMnc.g:258:1: ruleCommand returns [EObject current=null] : ( () ( (lv_asynch_1_0= 'async' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']' ) ;
    public final EObject ruleCommand() throws RecognitionException {
        EObject current = null;

        Token lv_asynch_1_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_parameters_4_0 = null;

        EObject lv_parameters_6_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:264:2: ( ( () ( (lv_asynch_1_0= 'async' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']' ) )
            // InternalMnc.g:265:2: ( () ( (lv_asynch_1_0= 'async' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']' )
            {
            // InternalMnc.g:265:2: ( () ( (lv_asynch_1_0= 'async' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']' )
            // InternalMnc.g:266:3: () ( (lv_asynch_1_0= 'async' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']'
            {
            // InternalMnc.g:266:3: ()
            // InternalMnc.g:267:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getCommandAccess().getCommandAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:273:3: ( (lv_asynch_1_0= 'async' ) )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==15) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // InternalMnc.g:274:4: (lv_asynch_1_0= 'async' )
                    {
                    // InternalMnc.g:274:4: (lv_asynch_1_0= 'async' )
                    // InternalMnc.g:275:5: lv_asynch_1_0= 'async'
                    {
                    lv_asynch_1_0=(Token)match(input,15,FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_asynch_1_0, grammarAccess.getCommandAccess().getAsynchAsyncKeyword_1_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getCommandRule());
                      					}
                      					setWithLastConsumed(current, "asynch", lv_asynch_1_0 != null, "async");
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:287:3: ( (lv_name_2_0= ruleEString ) )
            // InternalMnc.g:288:4: (lv_name_2_0= ruleEString )
            {
            // InternalMnc.g:288:4: (lv_name_2_0= ruleEString )
            // InternalMnc.g:289:5: lv_name_2_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getCommandAccess().getNameEStringParserRuleCall_2_0());
              				
            }
            pushFollow(FOLLOW_9);
            lv_name_2_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getCommandRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_2_0,
              						"com.dml.dsl.Dml.EString");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_3=(Token)match(input,16,FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getCommandAccess().getLeftSquareBracketKeyword_3());
              		
            }
            // InternalMnc.g:310:3: ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_ID||(LA6_0>=103 && LA6_0<=108)) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // InternalMnc.g:311:4: ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )*
                    {
                    // InternalMnc.g:311:4: ( (lv_parameters_4_0= ruleParameter ) )
                    // InternalMnc.g:312:5: (lv_parameters_4_0= ruleParameter )
                    {
                    // InternalMnc.g:312:5: (lv_parameters_4_0= ruleParameter )
                    // InternalMnc.g:313:6: lv_parameters_4_0= ruleParameter
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getCommandAccess().getParametersParameterParserRuleCall_4_0_0());
                      					
                    }
                    pushFollow(FOLLOW_11);
                    lv_parameters_4_0=ruleParameter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getCommandRule());
                      						}
                      						add(
                      							current,
                      							"parameters",
                      							lv_parameters_4_0,
                      							"com.dml.dsl.Dml.Parameter");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:330:4: (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==17) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // InternalMnc.g:331:5: otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) )
                    	    {
                    	    otherlv_5=(Token)match(input,17,FOLLOW_12); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_5, grammarAccess.getCommandAccess().getCommaKeyword_4_1_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:335:5: ( (lv_parameters_6_0= ruleParameter ) )
                    	    // InternalMnc.g:336:6: (lv_parameters_6_0= ruleParameter )
                    	    {
                    	    // InternalMnc.g:336:6: (lv_parameters_6_0= ruleParameter )
                    	    // InternalMnc.g:337:7: lv_parameters_6_0= ruleParameter
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getCommandAccess().getParametersParameterParserRuleCall_4_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_11);
                    	    lv_parameters_6_0=ruleParameter();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElementForParent(grammarAccess.getCommandRule());
                    	      							}
                    	      							add(
                    	      								current,
                    	      								"parameters",
                    	      								lv_parameters_6_0,
                    	      								"com.dml.dsl.Dml.Parameter");
                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


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

            otherlv_7=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_7, grammarAccess.getCommandAccess().getRightSquareBracketKeyword_5());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleCommand"


    // $ANTLR start "entryRuleEvent"
    // InternalMnc.g:364:1: entryRuleEvent returns [EObject current=null] : iv_ruleEvent= ruleEvent EOF ;
    public final EObject entryRuleEvent() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEvent = null;


        try {
            // InternalMnc.g:364:46: (iv_ruleEvent= ruleEvent EOF )
            // InternalMnc.g:365:2: iv_ruleEvent= ruleEvent EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEventRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleEvent=ruleEvent();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEvent; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleEvent"


    // $ANTLR start "ruleEvent"
    // InternalMnc.g:371:1: ruleEvent returns [EObject current=null] : ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']' ) ;
    public final EObject ruleEvent() throws RecognitionException {
        EObject current = null;

        Token lv_publish_1_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_parameters_4_0 = null;

        EObject lv_parameters_6_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:377:2: ( ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']' ) )
            // InternalMnc.g:378:2: ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']' )
            {
            // InternalMnc.g:378:2: ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']' )
            // InternalMnc.g:379:3: () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? otherlv_7= ']'
            {
            // InternalMnc.g:379:3: ()
            // InternalMnc.g:380:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getEventAccess().getEventAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:386:3: ( (lv_publish_1_0= 'Publish' ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==19) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // InternalMnc.g:387:4: (lv_publish_1_0= 'Publish' )
                    {
                    // InternalMnc.g:387:4: (lv_publish_1_0= 'Publish' )
                    // InternalMnc.g:388:5: lv_publish_1_0= 'Publish'
                    {
                    lv_publish_1_0=(Token)match(input,19,FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_publish_1_0, grammarAccess.getEventAccess().getPublishPublishKeyword_1_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getEventRule());
                      					}
                      					setWithLastConsumed(current, "publish", lv_publish_1_0 != null, "Publish");
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:400:3: ( (lv_name_2_0= ruleEString ) )
            // InternalMnc.g:401:4: (lv_name_2_0= ruleEString )
            {
            // InternalMnc.g:401:4: (lv_name_2_0= ruleEString )
            // InternalMnc.g:402:5: lv_name_2_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getEventAccess().getNameEStringParserRuleCall_2_0());
              				
            }
            pushFollow(FOLLOW_9);
            lv_name_2_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getEventRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_2_0,
              						"com.dml.dsl.Dml.EString");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_3=(Token)match(input,16,FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getEventAccess().getLeftSquareBracketKeyword_3());
              		
            }
            // InternalMnc.g:423:3: ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==RULE_ID||(LA9_0>=103 && LA9_0<=108)) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // InternalMnc.g:424:4: ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )*
                    {
                    // InternalMnc.g:424:4: ( (lv_parameters_4_0= ruleParameter ) )
                    // InternalMnc.g:425:5: (lv_parameters_4_0= ruleParameter )
                    {
                    // InternalMnc.g:425:5: (lv_parameters_4_0= ruleParameter )
                    // InternalMnc.g:426:6: lv_parameters_4_0= ruleParameter
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getEventAccess().getParametersParameterParserRuleCall_4_0_0());
                      					
                    }
                    pushFollow(FOLLOW_11);
                    lv_parameters_4_0=ruleParameter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getEventRule());
                      						}
                      						add(
                      							current,
                      							"parameters",
                      							lv_parameters_4_0,
                      							"com.dml.dsl.Dml.Parameter");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:443:4: (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==17) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // InternalMnc.g:444:5: otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) )
                    	    {
                    	    otherlv_5=(Token)match(input,17,FOLLOW_12); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_5, grammarAccess.getEventAccess().getCommaKeyword_4_1_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:448:5: ( (lv_parameters_6_0= ruleParameter ) )
                    	    // InternalMnc.g:449:6: (lv_parameters_6_0= ruleParameter )
                    	    {
                    	    // InternalMnc.g:449:6: (lv_parameters_6_0= ruleParameter )
                    	    // InternalMnc.g:450:7: lv_parameters_6_0= ruleParameter
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getEventAccess().getParametersParameterParserRuleCall_4_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_11);
                    	    lv_parameters_6_0=ruleParameter();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElementForParent(grammarAccess.getEventRule());
                    	      							}
                    	      							add(
                    	      								current,
                    	      								"parameters",
                    	      								lv_parameters_6_0,
                    	      								"com.dml.dsl.Dml.Parameter");
                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_7=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_7, grammarAccess.getEventAccess().getRightSquareBracketKeyword_5());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleEvent"


    // $ANTLR start "entryRuleResponse"
    // InternalMnc.g:477:1: entryRuleResponse returns [EObject current=null] : iv_ruleResponse= ruleResponse EOF ;
    public final EObject entryRuleResponse() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleResponse = null;


        try {
            // InternalMnc.g:477:49: (iv_ruleResponse= ruleResponse EOF )
            // InternalMnc.g:478:2: iv_ruleResponse= ruleResponse EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getResponseRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleResponse=ruleResponse();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleResponse; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleResponse"


    // $ANTLR start "ruleResponse"
    // InternalMnc.g:484:1: ruleResponse returns [EObject current=null] : ( () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( ( (lv_parameters_3_0= ruleParameter ) ) (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* )? otherlv_6= ']' ) ;
    public final EObject ruleResponse() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_parameters_3_0 = null;

        EObject lv_parameters_5_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:490:2: ( ( () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( ( (lv_parameters_3_0= ruleParameter ) ) (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* )? otherlv_6= ']' ) )
            // InternalMnc.g:491:2: ( () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( ( (lv_parameters_3_0= ruleParameter ) ) (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* )? otherlv_6= ']' )
            {
            // InternalMnc.g:491:2: ( () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( ( (lv_parameters_3_0= ruleParameter ) ) (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* )? otherlv_6= ']' )
            // InternalMnc.g:492:3: () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( ( (lv_parameters_3_0= ruleParameter ) ) (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* )? otherlv_6= ']'
            {
            // InternalMnc.g:492:3: ()
            // InternalMnc.g:493:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getResponseAccess().getResponseAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:499:3: ( (lv_name_1_0= ruleEString ) )
            // InternalMnc.g:500:4: (lv_name_1_0= ruleEString )
            {
            // InternalMnc.g:500:4: (lv_name_1_0= ruleEString )
            // InternalMnc.g:501:5: lv_name_1_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getResponseAccess().getNameEStringParserRuleCall_1_0());
              				
            }
            pushFollow(FOLLOW_9);
            lv_name_1_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getResponseRule());
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

            otherlv_2=(Token)match(input,16,FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getResponseAccess().getLeftSquareBracketKeyword_2());
              		
            }
            // InternalMnc.g:522:3: ( ( (lv_parameters_3_0= ruleParameter ) ) (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_ID||(LA11_0>=103 && LA11_0<=108)) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalMnc.g:523:4: ( (lv_parameters_3_0= ruleParameter ) ) (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )*
                    {
                    // InternalMnc.g:523:4: ( (lv_parameters_3_0= ruleParameter ) )
                    // InternalMnc.g:524:5: (lv_parameters_3_0= ruleParameter )
                    {
                    // InternalMnc.g:524:5: (lv_parameters_3_0= ruleParameter )
                    // InternalMnc.g:525:6: lv_parameters_3_0= ruleParameter
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getResponseAccess().getParametersParameterParserRuleCall_3_0_0());
                      					
                    }
                    pushFollow(FOLLOW_11);
                    lv_parameters_3_0=ruleParameter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getResponseRule());
                      						}
                      						add(
                      							current,
                      							"parameters",
                      							lv_parameters_3_0,
                      							"com.dml.dsl.Dml.Parameter");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:542:4: (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==17) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // InternalMnc.g:543:5: otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) )
                    	    {
                    	    otherlv_4=(Token)match(input,17,FOLLOW_12); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_4, grammarAccess.getResponseAccess().getCommaKeyword_3_1_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:547:5: ( (lv_parameters_5_0= ruleParameter ) )
                    	    // InternalMnc.g:548:6: (lv_parameters_5_0= ruleParameter )
                    	    {
                    	    // InternalMnc.g:548:6: (lv_parameters_5_0= ruleParameter )
                    	    // InternalMnc.g:549:7: lv_parameters_5_0= ruleParameter
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getResponseAccess().getParametersParameterParserRuleCall_3_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_11);
                    	    lv_parameters_5_0=ruleParameter();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElementForParent(grammarAccess.getResponseRule());
                    	      							}
                    	      							add(
                    	      								current,
                    	      								"parameters",
                    	      								lv_parameters_5_0,
                    	      								"com.dml.dsl.Dml.Parameter");
                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_6=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_6, grammarAccess.getResponseAccess().getRightSquareBracketKeyword_4());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleResponse"


    // $ANTLR start "entryRuleAlarm"
    // InternalMnc.g:576:1: entryRuleAlarm returns [EObject current=null] : iv_ruleAlarm= ruleAlarm EOF ;
    public final EObject entryRuleAlarm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAlarm = null;


        try {
            // InternalMnc.g:576:46: (iv_ruleAlarm= ruleAlarm EOF )
            // InternalMnc.g:577:2: iv_ruleAlarm= ruleAlarm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAlarmRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleAlarm=ruleAlarm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAlarm; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleAlarm"


    // $ANTLR start "ruleAlarm"
    // InternalMnc.g:583:1: ruleAlarm returns [EObject current=null] : ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? (otherlv_7= 'level' otherlv_8= '=' ( (lv_level_9_0= ruleEInt ) ) )? otherlv_10= ']' ) ;
    public final EObject ruleAlarm() throws RecognitionException {
        EObject current = null;

        Token lv_publish_1_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_parameters_4_0 = null;

        EObject lv_parameters_6_0 = null;

        AntlrDatatypeRuleToken lv_level_9_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:589:2: ( ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? (otherlv_7= 'level' otherlv_8= '=' ( (lv_level_9_0= ruleEInt ) ) )? otherlv_10= ']' ) )
            // InternalMnc.g:590:2: ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? (otherlv_7= 'level' otherlv_8= '=' ( (lv_level_9_0= ruleEInt ) ) )? otherlv_10= ']' )
            {
            // InternalMnc.g:590:2: ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? (otherlv_7= 'level' otherlv_8= '=' ( (lv_level_9_0= ruleEInt ) ) )? otherlv_10= ']' )
            // InternalMnc.g:591:3: () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_name_2_0= ruleEString ) ) otherlv_3= '[' ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )? (otherlv_7= 'level' otherlv_8= '=' ( (lv_level_9_0= ruleEInt ) ) )? otherlv_10= ']'
            {
            // InternalMnc.g:591:3: ()
            // InternalMnc.g:592:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getAlarmAccess().getAlarmAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:598:3: ( (lv_publish_1_0= 'Publish' ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==19) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // InternalMnc.g:599:4: (lv_publish_1_0= 'Publish' )
                    {
                    // InternalMnc.g:599:4: (lv_publish_1_0= 'Publish' )
                    // InternalMnc.g:600:5: lv_publish_1_0= 'Publish'
                    {
                    lv_publish_1_0=(Token)match(input,19,FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_publish_1_0, grammarAccess.getAlarmAccess().getPublishPublishKeyword_1_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getAlarmRule());
                      					}
                      					setWithLastConsumed(current, "publish", lv_publish_1_0 != null, "Publish");
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:612:3: ( (lv_name_2_0= ruleEString ) )
            // InternalMnc.g:613:4: (lv_name_2_0= ruleEString )
            {
            // InternalMnc.g:613:4: (lv_name_2_0= ruleEString )
            // InternalMnc.g:614:5: lv_name_2_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAlarmAccess().getNameEStringParserRuleCall_2_0());
              				
            }
            pushFollow(FOLLOW_9);
            lv_name_2_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAlarmRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_2_0,
              						"com.dml.dsl.Dml.EString");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_3=(Token)match(input,16,FOLLOW_13); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getAlarmAccess().getLeftSquareBracketKeyword_3());
              		
            }
            // InternalMnc.g:635:3: ( ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_ID||(LA14_0>=103 && LA14_0<=108)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // InternalMnc.g:636:4: ( (lv_parameters_4_0= ruleParameter ) ) (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )*
                    {
                    // InternalMnc.g:636:4: ( (lv_parameters_4_0= ruleParameter ) )
                    // InternalMnc.g:637:5: (lv_parameters_4_0= ruleParameter )
                    {
                    // InternalMnc.g:637:5: (lv_parameters_4_0= ruleParameter )
                    // InternalMnc.g:638:6: lv_parameters_4_0= ruleParameter
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getAlarmAccess().getParametersParameterParserRuleCall_4_0_0());
                      					
                    }
                    pushFollow(FOLLOW_14);
                    lv_parameters_4_0=ruleParameter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getAlarmRule());
                      						}
                      						add(
                      							current,
                      							"parameters",
                      							lv_parameters_4_0,
                      							"com.dml.dsl.Dml.Parameter");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:655:4: (otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) ) )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==17) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // InternalMnc.g:656:5: otherlv_5= ',' ( (lv_parameters_6_0= ruleParameter ) )
                    	    {
                    	    otherlv_5=(Token)match(input,17,FOLLOW_12); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_5, grammarAccess.getAlarmAccess().getCommaKeyword_4_1_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:660:5: ( (lv_parameters_6_0= ruleParameter ) )
                    	    // InternalMnc.g:661:6: (lv_parameters_6_0= ruleParameter )
                    	    {
                    	    // InternalMnc.g:661:6: (lv_parameters_6_0= ruleParameter )
                    	    // InternalMnc.g:662:7: lv_parameters_6_0= ruleParameter
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getAlarmAccess().getParametersParameterParserRuleCall_4_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_14);
                    	    lv_parameters_6_0=ruleParameter();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElementForParent(grammarAccess.getAlarmRule());
                    	      							}
                    	      							add(
                    	      								current,
                    	      								"parameters",
                    	      								lv_parameters_6_0,
                    	      								"com.dml.dsl.Dml.Parameter");
                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

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

            // InternalMnc.g:681:3: (otherlv_7= 'level' otherlv_8= '=' ( (lv_level_9_0= ruleEInt ) ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==20) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // InternalMnc.g:682:4: otherlv_7= 'level' otherlv_8= '=' ( (lv_level_9_0= ruleEInt ) )
                    {
                    otherlv_7=(Token)match(input,20,FOLLOW_15); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_7, grammarAccess.getAlarmAccess().getLevelKeyword_5_0());
                      			
                    }
                    otherlv_8=(Token)match(input,21,FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_8, grammarAccess.getAlarmAccess().getEqualsSignKeyword_5_1());
                      			
                    }
                    // InternalMnc.g:690:4: ( (lv_level_9_0= ruleEInt ) )
                    // InternalMnc.g:691:5: (lv_level_9_0= ruleEInt )
                    {
                    // InternalMnc.g:691:5: (lv_level_9_0= ruleEInt )
                    // InternalMnc.g:692:6: lv_level_9_0= ruleEInt
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getAlarmAccess().getLevelEIntParserRuleCall_5_2_0());
                      					
                    }
                    pushFollow(FOLLOW_17);
                    lv_level_9_0=ruleEInt();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getAlarmRule());
                      						}
                      						set(
                      							current,
                      							"level",
                      							lv_level_9_0,
                      							"com.dml.dsl.Dml.EInt");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }


                    }
                    break;

            }

            otherlv_10=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_10, grammarAccess.getAlarmAccess().getRightSquareBracketKeyword_6());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleAlarm"


    // $ANTLR start "entryRuleDataPoint"
    // InternalMnc.g:718:1: entryRuleDataPoint returns [EObject current=null] : iv_ruleDataPoint= ruleDataPoint EOF ;
    public final EObject entryRuleDataPoint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataPoint = null;


        try {
            // InternalMnc.g:718:50: (iv_ruleDataPoint= ruleDataPoint EOF )
            // InternalMnc.g:719:2: iv_ruleDataPoint= ruleDataPoint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDataPointRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleDataPoint=ruleDataPoint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDataPoint; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDataPoint"


    // $ANTLR start "ruleDataPoint"
    // InternalMnc.g:725:1: ruleDataPoint returns [EObject current=null] : ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_type_2_0= rulePrimitiveValueType ) )? ( (lv_name_3_0= ruleEString ) ) (otherlv_4= '=' ( (lv_value_5_0= rulePrimitiveValue ) ) )? otherlv_6= '[' ( ( (lv_parameters_7_0= ruleParameter ) ) (otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) ) )* )? otherlv_10= ']' ) ;
    public final EObject ruleDataPoint() throws RecognitionException {
        EObject current = null;

        Token lv_publish_1_0=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Enumerator lv_type_2_0 = null;

        AntlrDatatypeRuleToken lv_name_3_0 = null;

        EObject lv_value_5_0 = null;

        EObject lv_parameters_7_0 = null;

        EObject lv_parameters_9_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:731:2: ( ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_type_2_0= rulePrimitiveValueType ) )? ( (lv_name_3_0= ruleEString ) ) (otherlv_4= '=' ( (lv_value_5_0= rulePrimitiveValue ) ) )? otherlv_6= '[' ( ( (lv_parameters_7_0= ruleParameter ) ) (otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) ) )* )? otherlv_10= ']' ) )
            // InternalMnc.g:732:2: ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_type_2_0= rulePrimitiveValueType ) )? ( (lv_name_3_0= ruleEString ) ) (otherlv_4= '=' ( (lv_value_5_0= rulePrimitiveValue ) ) )? otherlv_6= '[' ( ( (lv_parameters_7_0= ruleParameter ) ) (otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) ) )* )? otherlv_10= ']' )
            {
            // InternalMnc.g:732:2: ( () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_type_2_0= rulePrimitiveValueType ) )? ( (lv_name_3_0= ruleEString ) ) (otherlv_4= '=' ( (lv_value_5_0= rulePrimitiveValue ) ) )? otherlv_6= '[' ( ( (lv_parameters_7_0= ruleParameter ) ) (otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) ) )* )? otherlv_10= ']' )
            // InternalMnc.g:733:3: () ( (lv_publish_1_0= 'Publish' ) )? ( (lv_type_2_0= rulePrimitiveValueType ) )? ( (lv_name_3_0= ruleEString ) ) (otherlv_4= '=' ( (lv_value_5_0= rulePrimitiveValue ) ) )? otherlv_6= '[' ( ( (lv_parameters_7_0= ruleParameter ) ) (otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) ) )* )? otherlv_10= ']'
            {
            // InternalMnc.g:733:3: ()
            // InternalMnc.g:734:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getDataPointAccess().getDataPointAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:740:3: ( (lv_publish_1_0= 'Publish' ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==19) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalMnc.g:741:4: (lv_publish_1_0= 'Publish' )
                    {
                    // InternalMnc.g:741:4: (lv_publish_1_0= 'Publish' )
                    // InternalMnc.g:742:5: lv_publish_1_0= 'Publish'
                    {
                    lv_publish_1_0=(Token)match(input,19,FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_publish_1_0, grammarAccess.getDataPointAccess().getPublishPublishKeyword_1_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getDataPointRule());
                      					}
                      					setWithLastConsumed(current, "publish", lv_publish_1_0 != null, "Publish");
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:754:3: ( (lv_type_2_0= rulePrimitiveValueType ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>=103 && LA17_0<=108)) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // InternalMnc.g:755:4: (lv_type_2_0= rulePrimitiveValueType )
                    {
                    // InternalMnc.g:755:4: (lv_type_2_0= rulePrimitiveValueType )
                    // InternalMnc.g:756:5: lv_type_2_0= rulePrimitiveValueType
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getDataPointAccess().getTypePrimitiveValueTypeEnumRuleCall_2_0());
                      				
                    }
                    pushFollow(FOLLOW_4);
                    lv_type_2_0=rulePrimitiveValueType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getDataPointRule());
                      					}
                      					set(
                      						current,
                      						"type",
                      						lv_type_2_0,
                      						"com.dml.dsl.Dml.PrimitiveValueType");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:773:3: ( (lv_name_3_0= ruleEString ) )
            // InternalMnc.g:774:4: (lv_name_3_0= ruleEString )
            {
            // InternalMnc.g:774:4: (lv_name_3_0= ruleEString )
            // InternalMnc.g:775:5: lv_name_3_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDataPointAccess().getNameEStringParserRuleCall_3_0());
              				
            }
            pushFollow(FOLLOW_19);
            lv_name_3_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDataPointRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_3_0,
              						"com.dml.dsl.Dml.EString");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:792:3: (otherlv_4= '=' ( (lv_value_5_0= rulePrimitiveValue ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==21) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // InternalMnc.g:793:4: otherlv_4= '=' ( (lv_value_5_0= rulePrimitiveValue ) )
                    {
                    otherlv_4=(Token)match(input,21,FOLLOW_20); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_4, grammarAccess.getDataPointAccess().getEqualsSignKeyword_4_0());
                      			
                    }
                    // InternalMnc.g:797:4: ( (lv_value_5_0= rulePrimitiveValue ) )
                    // InternalMnc.g:798:5: (lv_value_5_0= rulePrimitiveValue )
                    {
                    // InternalMnc.g:798:5: (lv_value_5_0= rulePrimitiveValue )
                    // InternalMnc.g:799:6: lv_value_5_0= rulePrimitiveValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getDataPointAccess().getValuePrimitiveValueParserRuleCall_4_1_0());
                      					
                    }
                    pushFollow(FOLLOW_9);
                    lv_value_5_0=rulePrimitiveValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getDataPointRule());
                      						}
                      						set(
                      							current,
                      							"value",
                      							lv_value_5_0,
                      							"com.dml.dsl.Dml.PrimitiveValue");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }


                    }
                    break;

            }

            otherlv_6=(Token)match(input,16,FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_6, grammarAccess.getDataPointAccess().getLeftSquareBracketKeyword_5());
              		
            }
            // InternalMnc.g:821:3: ( ( (lv_parameters_7_0= ruleParameter ) ) (otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) ) )* )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==RULE_ID||(LA20_0>=103 && LA20_0<=108)) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // InternalMnc.g:822:4: ( (lv_parameters_7_0= ruleParameter ) ) (otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) ) )*
                    {
                    // InternalMnc.g:822:4: ( (lv_parameters_7_0= ruleParameter ) )
                    // InternalMnc.g:823:5: (lv_parameters_7_0= ruleParameter )
                    {
                    // InternalMnc.g:823:5: (lv_parameters_7_0= ruleParameter )
                    // InternalMnc.g:824:6: lv_parameters_7_0= ruleParameter
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getDataPointAccess().getParametersParameterParserRuleCall_6_0_0());
                      					
                    }
                    pushFollow(FOLLOW_11);
                    lv_parameters_7_0=ruleParameter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getDataPointRule());
                      						}
                      						add(
                      							current,
                      							"parameters",
                      							lv_parameters_7_0,
                      							"com.dml.dsl.Dml.Parameter");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:841:4: (otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) ) )*
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==17) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // InternalMnc.g:842:5: otherlv_8= ',' ( (lv_parameters_9_0= ruleParameter ) )
                    	    {
                    	    otherlv_8=(Token)match(input,17,FOLLOW_12); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_8, grammarAccess.getDataPointAccess().getCommaKeyword_6_1_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:846:5: ( (lv_parameters_9_0= ruleParameter ) )
                    	    // InternalMnc.g:847:6: (lv_parameters_9_0= ruleParameter )
                    	    {
                    	    // InternalMnc.g:847:6: (lv_parameters_9_0= ruleParameter )
                    	    // InternalMnc.g:848:7: lv_parameters_9_0= ruleParameter
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getDataPointAccess().getParametersParameterParserRuleCall_6_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_11);
                    	    lv_parameters_9_0=ruleParameter();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElementForParent(grammarAccess.getDataPointRule());
                    	      							}
                    	      							add(
                    	      								current,
                    	      								"parameters",
                    	      								lv_parameters_9_0,
                    	      								"com.dml.dsl.Dml.Parameter");
                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_10=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_10, grammarAccess.getDataPointAccess().getRightSquareBracketKeyword_7());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleDataPoint"


    // $ANTLR start "entryRuleOperatingStateUtility"
    // InternalMnc.g:875:1: entryRuleOperatingStateUtility returns [EObject current=null] : iv_ruleOperatingStateUtility= ruleOperatingStateUtility EOF ;
    public final EObject entryRuleOperatingStateUtility() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOperatingStateUtility = null;


        try {
            // InternalMnc.g:875:62: (iv_ruleOperatingStateUtility= ruleOperatingStateUtility EOF )
            // InternalMnc.g:876:2: iv_ruleOperatingStateUtility= ruleOperatingStateUtility EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOperatingStateUtilityRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleOperatingStateUtility=ruleOperatingStateUtility();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOperatingStateUtility; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleOperatingStateUtility"


    // $ANTLR start "ruleOperatingStateUtility"
    // InternalMnc.g:882:1: ruleOperatingStateUtility returns [EObject current=null] : ( () ( (lv_operatingStates_1_0= ruleOperatingState ) )* (otherlv_2= 'startStates' otherlv_3= ':' ( ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) ) )* )? (otherlv_7= 'endStates' otherlv_8= ':' ( (otherlv_9= RULE_ID ) ) (otherlv_10= ',' ( (otherlv_11= RULE_ID ) ) )* )? ) ;
    public final EObject ruleOperatingStateUtility() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        EObject lv_operatingStates_1_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:888:2: ( ( () ( (lv_operatingStates_1_0= ruleOperatingState ) )* (otherlv_2= 'startStates' otherlv_3= ':' ( ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) ) )* )? (otherlv_7= 'endStates' otherlv_8= ':' ( (otherlv_9= RULE_ID ) ) (otherlv_10= ',' ( (otherlv_11= RULE_ID ) ) )* )? ) )
            // InternalMnc.g:889:2: ( () ( (lv_operatingStates_1_0= ruleOperatingState ) )* (otherlv_2= 'startStates' otherlv_3= ':' ( ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) ) )* )? (otherlv_7= 'endStates' otherlv_8= ':' ( (otherlv_9= RULE_ID ) ) (otherlv_10= ',' ( (otherlv_11= RULE_ID ) ) )* )? )
            {
            // InternalMnc.g:889:2: ( () ( (lv_operatingStates_1_0= ruleOperatingState ) )* (otherlv_2= 'startStates' otherlv_3= ':' ( ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) ) )* )? (otherlv_7= 'endStates' otherlv_8= ':' ( (otherlv_9= RULE_ID ) ) (otherlv_10= ',' ( (otherlv_11= RULE_ID ) ) )* )? )
            // InternalMnc.g:890:3: () ( (lv_operatingStates_1_0= ruleOperatingState ) )* (otherlv_2= 'startStates' otherlv_3= ':' ( ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) ) )* )? (otherlv_7= 'endStates' otherlv_8= ':' ( (otherlv_9= RULE_ID ) ) (otherlv_10= ',' ( (otherlv_11= RULE_ID ) ) )* )?
            {
            // InternalMnc.g:890:3: ()
            // InternalMnc.g:891:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getOperatingStateUtilityAccess().getOperatingStateUtilityAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:897:3: ( (lv_operatingStates_1_0= ruleOperatingState ) )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==RULE_ID||LA21_0==RULE_STRING) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // InternalMnc.g:898:4: (lv_operatingStates_1_0= ruleOperatingState )
            	    {
            	    // InternalMnc.g:898:4: (lv_operatingStates_1_0= ruleOperatingState )
            	    // InternalMnc.g:899:5: lv_operatingStates_1_0= ruleOperatingState
            	    {
            	    if ( state.backtracking==0 ) {

            	      					newCompositeNode(grammarAccess.getOperatingStateUtilityAccess().getOperatingStatesOperatingStateParserRuleCall_1_0());
            	      				
            	    }
            	    pushFollow(FOLLOW_21);
            	    lv_operatingStates_1_0=ruleOperatingState();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					if (current==null) {
            	      						current = createModelElementForParent(grammarAccess.getOperatingStateUtilityRule());
            	      					}
            	      					add(
            	      						current,
            	      						"operatingStates",
            	      						lv_operatingStates_1_0,
            	      						"com.mncml.dsl.Mnc.OperatingState");
            	      					afterParserOrEnumRuleCall();
            	      				
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            // InternalMnc.g:916:3: (otherlv_2= 'startStates' otherlv_3= ':' ( ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) ) )* )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==22) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // InternalMnc.g:917:4: otherlv_2= 'startStates' otherlv_3= ':' ( ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) ) )*
                    {
                    otherlv_2=(Token)match(input,22,FOLLOW_22); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_2, grammarAccess.getOperatingStateUtilityAccess().getStartStatesKeyword_2_0());
                      			
                    }
                    otherlv_3=(Token)match(input,23,FOLLOW_23); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_3, grammarAccess.getOperatingStateUtilityAccess().getColonKeyword_2_1());
                      			
                    }
                    // InternalMnc.g:925:4: ( ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) ) )*
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==RULE_ID) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // InternalMnc.g:926:5: ( (otherlv_4= RULE_ID ) ) (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) )
                    	    {
                    	    // InternalMnc.g:926:5: ( (otherlv_4= RULE_ID ) )
                    	    // InternalMnc.g:927:6: (otherlv_4= RULE_ID )
                    	    {
                    	    // InternalMnc.g:927:6: (otherlv_4= RULE_ID )
                    	    // InternalMnc.g:928:7: otherlv_4= RULE_ID
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElement(grammarAccess.getOperatingStateUtilityRule());
                    	      							}
                    	      						
                    	    }
                    	    otherlv_4=(Token)match(input,RULE_ID,FOLLOW_24); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							newLeafNode(otherlv_4, grammarAccess.getOperatingStateUtilityAccess().getStartStateOperatingStateCrossReference_2_2_0_0());
                    	      						
                    	    }

                    	    }


                    	    }

                    	    // InternalMnc.g:939:5: (otherlv_5= ',' ( (otherlv_6= RULE_ID ) ) )
                    	    // InternalMnc.g:940:6: otherlv_5= ',' ( (otherlv_6= RULE_ID ) )
                    	    {
                    	    otherlv_5=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						newLeafNode(otherlv_5, grammarAccess.getOperatingStateUtilityAccess().getCommaKeyword_2_2_1_0());
                    	      					
                    	    }
                    	    // InternalMnc.g:944:6: ( (otherlv_6= RULE_ID ) )
                    	    // InternalMnc.g:945:7: (otherlv_6= RULE_ID )
                    	    {
                    	    // InternalMnc.g:945:7: (otherlv_6= RULE_ID )
                    	    // InternalMnc.g:946:8: otherlv_6= RULE_ID
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      								if (current==null) {
                    	      									current = createModelElement(grammarAccess.getOperatingStateUtilityRule());
                    	      								}
                    	      							
                    	    }
                    	    otherlv_6=(Token)match(input,RULE_ID,FOLLOW_23); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      								newLeafNode(otherlv_6, grammarAccess.getOperatingStateUtilityAccess().getStartStateOperatingStateCrossReference_2_2_1_1_0());
                    	      							
                    	    }

                    	    }


                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);


                    }
                    break;

            }

            // InternalMnc.g:960:3: (otherlv_7= 'endStates' otherlv_8= ':' ( (otherlv_9= RULE_ID ) ) (otherlv_10= ',' ( (otherlv_11= RULE_ID ) ) )* )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==24) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // InternalMnc.g:961:4: otherlv_7= 'endStates' otherlv_8= ':' ( (otherlv_9= RULE_ID ) ) (otherlv_10= ',' ( (otherlv_11= RULE_ID ) ) )*
                    {
                    otherlv_7=(Token)match(input,24,FOLLOW_22); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_7, grammarAccess.getOperatingStateUtilityAccess().getEndStatesKeyword_3_0());
                      			
                    }
                    otherlv_8=(Token)match(input,23,FOLLOW_7); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_8, grammarAccess.getOperatingStateUtilityAccess().getColonKeyword_3_1());
                      			
                    }
                    // InternalMnc.g:969:4: ( (otherlv_9= RULE_ID ) )
                    // InternalMnc.g:970:5: (otherlv_9= RULE_ID )
                    {
                    // InternalMnc.g:970:5: (otherlv_9= RULE_ID )
                    // InternalMnc.g:971:6: otherlv_9= RULE_ID
                    {
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElement(grammarAccess.getOperatingStateUtilityRule());
                      						}
                      					
                    }
                    otherlv_9=(Token)match(input,RULE_ID,FOLLOW_25); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						newLeafNode(otherlv_9, grammarAccess.getOperatingStateUtilityAccess().getEndStateOperatingStateCrossReference_3_2_0());
                      					
                    }

                    }


                    }

                    // InternalMnc.g:982:4: (otherlv_10= ',' ( (otherlv_11= RULE_ID ) ) )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==17) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // InternalMnc.g:983:5: otherlv_10= ',' ( (otherlv_11= RULE_ID ) )
                    	    {
                    	    otherlv_10=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_10, grammarAccess.getOperatingStateUtilityAccess().getCommaKeyword_3_3_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:987:5: ( (otherlv_11= RULE_ID ) )
                    	    // InternalMnc.g:988:6: (otherlv_11= RULE_ID )
                    	    {
                    	    // InternalMnc.g:988:6: (otherlv_11= RULE_ID )
                    	    // InternalMnc.g:989:7: otherlv_11= RULE_ID
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElement(grammarAccess.getOperatingStateUtilityRule());
                    	      							}
                    	      						
                    	    }
                    	    otherlv_11=(Token)match(input,RULE_ID,FOLLOW_25); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							newLeafNode(otherlv_11, grammarAccess.getOperatingStateUtilityAccess().getEndStateOperatingStateCrossReference_3_3_1_0());
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleOperatingStateUtility"


    // $ANTLR start "entryRuleOperatingState"
    // InternalMnc.g:1006:1: entryRuleOperatingState returns [EObject current=null] : iv_ruleOperatingState= ruleOperatingState EOF ;
    public final EObject entryRuleOperatingState() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOperatingState = null;


        try {
            // InternalMnc.g:1006:55: (iv_ruleOperatingState= ruleOperatingState EOF )
            // InternalMnc.g:1007:2: iv_ruleOperatingState= ruleOperatingState EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOperatingStateRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleOperatingState=ruleOperatingState();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOperatingState; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleOperatingState"


    // $ANTLR start "ruleOperatingState"
    // InternalMnc.g:1013:1: ruleOperatingState returns [EObject current=null] : ( () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( (lv_parameters_3_0= ruleParameter ) )* otherlv_4= ']' ) ;
    public final EObject ruleOperatingState() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_parameters_3_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:1019:2: ( ( () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( (lv_parameters_3_0= ruleParameter ) )* otherlv_4= ']' ) )
            // InternalMnc.g:1020:2: ( () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( (lv_parameters_3_0= ruleParameter ) )* otherlv_4= ']' )
            {
            // InternalMnc.g:1020:2: ( () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( (lv_parameters_3_0= ruleParameter ) )* otherlv_4= ']' )
            // InternalMnc.g:1021:3: () ( (lv_name_1_0= ruleEString ) ) otherlv_2= '[' ( (lv_parameters_3_0= ruleParameter ) )* otherlv_4= ']'
            {
            // InternalMnc.g:1021:3: ()
            // InternalMnc.g:1022:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getOperatingStateAccess().getOperatingStateAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:1028:3: ( (lv_name_1_0= ruleEString ) )
            // InternalMnc.g:1029:4: (lv_name_1_0= ruleEString )
            {
            // InternalMnc.g:1029:4: (lv_name_1_0= ruleEString )
            // InternalMnc.g:1030:5: lv_name_1_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getOperatingStateAccess().getNameEStringParserRuleCall_1_0());
              				
            }
            pushFollow(FOLLOW_9);
            lv_name_1_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getOperatingStateRule());
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

            otherlv_2=(Token)match(input,16,FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getOperatingStateAccess().getLeftSquareBracketKeyword_2());
              		
            }
            // InternalMnc.g:1051:3: ( (lv_parameters_3_0= ruleParameter ) )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==RULE_ID||(LA26_0>=103 && LA26_0<=108)) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // InternalMnc.g:1052:4: (lv_parameters_3_0= ruleParameter )
            	    {
            	    // InternalMnc.g:1052:4: (lv_parameters_3_0= ruleParameter )
            	    // InternalMnc.g:1053:5: lv_parameters_3_0= ruleParameter
            	    {
            	    if ( state.backtracking==0 ) {

            	      					newCompositeNode(grammarAccess.getOperatingStateAccess().getParametersParameterParserRuleCall_3_0());
            	      				
            	    }
            	    pushFollow(FOLLOW_10);
            	    lv_parameters_3_0=ruleParameter();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					if (current==null) {
            	      						current = createModelElementForParent(grammarAccess.getOperatingStateRule());
            	      					}
            	      					add(
            	      						current,
            	      						"parameters",
            	      						lv_parameters_3_0,
            	      						"com.dml.dsl.Dml.Parameter");
            	      					afterParserOrEnumRuleCall();
            	      				
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

            otherlv_4=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_4, grammarAccess.getOperatingStateAccess().getRightSquareBracketKeyword_4());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleOperatingState"


    // $ANTLR start "entryRuleSubscribableItemList"
    // InternalMnc.g:1078:1: entryRuleSubscribableItemList returns [EObject current=null] : iv_ruleSubscribableItemList= ruleSubscribableItemList EOF ;
    public final EObject entryRuleSubscribableItemList() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubscribableItemList = null;


        try {
            // InternalMnc.g:1078:61: (iv_ruleSubscribableItemList= ruleSubscribableItemList EOF )
            // InternalMnc.g:1079:2: iv_ruleSubscribableItemList= ruleSubscribableItemList EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSubscribableItemListRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleSubscribableItemList=ruleSubscribableItemList();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSubscribableItemList; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleSubscribableItemList"


    // $ANTLR start "ruleSubscribableItemList"
    // InternalMnc.g:1085:1: ruleSubscribableItemList returns [EObject current=null] : ( () otherlv_1= 'SubscribableItemList' otherlv_2= '{' (otherlv_3= 'subscribedEvents' otherlv_4= ':' ( ( ruleQualifiedName ) ) (otherlv_6= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_8= 'subscribedAlarms' otherlv_9= ':' ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_13= 'subscribedDataPoints' otherlv_14= ':' ( ( ruleQualifiedName ) ) (otherlv_16= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_18= '}' ) ;
    public final EObject ruleSubscribableItemList() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_16=null;
        Token otherlv_18=null;


        	enterRule();

        try {
            // InternalMnc.g:1091:2: ( ( () otherlv_1= 'SubscribableItemList' otherlv_2= '{' (otherlv_3= 'subscribedEvents' otherlv_4= ':' ( ( ruleQualifiedName ) ) (otherlv_6= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_8= 'subscribedAlarms' otherlv_9= ':' ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_13= 'subscribedDataPoints' otherlv_14= ':' ( ( ruleQualifiedName ) ) (otherlv_16= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_18= '}' ) )
            // InternalMnc.g:1092:2: ( () otherlv_1= 'SubscribableItemList' otherlv_2= '{' (otherlv_3= 'subscribedEvents' otherlv_4= ':' ( ( ruleQualifiedName ) ) (otherlv_6= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_8= 'subscribedAlarms' otherlv_9= ':' ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_13= 'subscribedDataPoints' otherlv_14= ':' ( ( ruleQualifiedName ) ) (otherlv_16= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_18= '}' )
            {
            // InternalMnc.g:1092:2: ( () otherlv_1= 'SubscribableItemList' otherlv_2= '{' (otherlv_3= 'subscribedEvents' otherlv_4= ':' ( ( ruleQualifiedName ) ) (otherlv_6= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_8= 'subscribedAlarms' otherlv_9= ':' ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_13= 'subscribedDataPoints' otherlv_14= ':' ( ( ruleQualifiedName ) ) (otherlv_16= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_18= '}' )
            // InternalMnc.g:1093:3: () otherlv_1= 'SubscribableItemList' otherlv_2= '{' (otherlv_3= 'subscribedEvents' otherlv_4= ':' ( ( ruleQualifiedName ) ) (otherlv_6= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_8= 'subscribedAlarms' otherlv_9= ':' ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* )? (otherlv_13= 'subscribedDataPoints' otherlv_14= ':' ( ( ruleQualifiedName ) ) (otherlv_16= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_18= '}'
            {
            // InternalMnc.g:1093:3: ()
            // InternalMnc.g:1094:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getSubscribableItemListAccess().getSubscribableItemListAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,25,FOLLOW_26); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getSubscribableItemListAccess().getSubscribableItemListKeyword_1());
              		
            }
            otherlv_2=(Token)match(input,26,FOLLOW_27); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getSubscribableItemListAccess().getLeftCurlyBracketKeyword_2());
              		
            }
            // InternalMnc.g:1108:3: (otherlv_3= 'subscribedEvents' otherlv_4= ':' ( ( ruleQualifiedName ) ) (otherlv_6= ',' ( ( ruleQualifiedName ) ) )* )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==27) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // InternalMnc.g:1109:4: otherlv_3= 'subscribedEvents' otherlv_4= ':' ( ( ruleQualifiedName ) ) (otherlv_6= ',' ( ( ruleQualifiedName ) ) )*
                    {
                    otherlv_3=(Token)match(input,27,FOLLOW_22); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_3, grammarAccess.getSubscribableItemListAccess().getSubscribedEventsKeyword_3_0());
                      			
                    }
                    otherlv_4=(Token)match(input,23,FOLLOW_7); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_4, grammarAccess.getSubscribableItemListAccess().getColonKeyword_3_1());
                      			
                    }
                    // InternalMnc.g:1117:4: ( ( ruleQualifiedName ) )
                    // InternalMnc.g:1118:5: ( ruleQualifiedName )
                    {
                    // InternalMnc.g:1118:5: ( ruleQualifiedName )
                    // InternalMnc.g:1119:6: ruleQualifiedName
                    {
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElement(grammarAccess.getSubscribableItemListRule());
                      						}
                      					
                    }
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSubscribableItemListAccess().getSubscribedEventsEventCrossReference_3_2_0());
                      					
                    }
                    pushFollow(FOLLOW_28);
                    ruleQualifiedName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:1133:4: (otherlv_6= ',' ( ( ruleQualifiedName ) ) )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==17) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // InternalMnc.g:1134:5: otherlv_6= ',' ( ( ruleQualifiedName ) )
                    	    {
                    	    otherlv_6=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_6, grammarAccess.getSubscribableItemListAccess().getCommaKeyword_3_3_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:1138:5: ( ( ruleQualifiedName ) )
                    	    // InternalMnc.g:1139:6: ( ruleQualifiedName )
                    	    {
                    	    // InternalMnc.g:1139:6: ( ruleQualifiedName )
                    	    // InternalMnc.g:1140:7: ruleQualifiedName
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElement(grammarAccess.getSubscribableItemListRule());
                    	      							}
                    	      						
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getSubscribableItemListAccess().getSubscribedEventsEventCrossReference_3_3_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_28);
                    	    ruleQualifiedName();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop27;
                        }
                    } while (true);


                    }
                    break;

            }

            // InternalMnc.g:1156:3: (otherlv_8= 'subscribedAlarms' otherlv_9= ':' ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==28) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // InternalMnc.g:1157:4: otherlv_8= 'subscribedAlarms' otherlv_9= ':' ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )*
                    {
                    otherlv_8=(Token)match(input,28,FOLLOW_22); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_8, grammarAccess.getSubscribableItemListAccess().getSubscribedAlarmsKeyword_4_0());
                      			
                    }
                    otherlv_9=(Token)match(input,23,FOLLOW_7); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_9, grammarAccess.getSubscribableItemListAccess().getColonKeyword_4_1());
                      			
                    }
                    // InternalMnc.g:1165:4: ( ( ruleQualifiedName ) )
                    // InternalMnc.g:1166:5: ( ruleQualifiedName )
                    {
                    // InternalMnc.g:1166:5: ( ruleQualifiedName )
                    // InternalMnc.g:1167:6: ruleQualifiedName
                    {
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElement(grammarAccess.getSubscribableItemListRule());
                      						}
                      					
                    }
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSubscribableItemListAccess().getSubscribedAlarmsAlarmCrossReference_4_2_0());
                      					
                    }
                    pushFollow(FOLLOW_29);
                    ruleQualifiedName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:1181:4: (otherlv_11= ',' ( ( ruleQualifiedName ) ) )*
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==17) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // InternalMnc.g:1182:5: otherlv_11= ',' ( ( ruleQualifiedName ) )
                    	    {
                    	    otherlv_11=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_11, grammarAccess.getSubscribableItemListAccess().getCommaKeyword_4_3_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:1186:5: ( ( ruleQualifiedName ) )
                    	    // InternalMnc.g:1187:6: ( ruleQualifiedName )
                    	    {
                    	    // InternalMnc.g:1187:6: ( ruleQualifiedName )
                    	    // InternalMnc.g:1188:7: ruleQualifiedName
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElement(grammarAccess.getSubscribableItemListRule());
                    	      							}
                    	      						
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getSubscribableItemListAccess().getSubscribedAlarmsAlarmCrossReference_4_3_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_29);
                    	    ruleQualifiedName();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop29;
                        }
                    } while (true);


                    }
                    break;

            }

            // InternalMnc.g:1204:3: (otherlv_13= 'subscribedDataPoints' otherlv_14= ':' ( ( ruleQualifiedName ) ) (otherlv_16= ',' ( ( ruleQualifiedName ) ) )* )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==29) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalMnc.g:1205:4: otherlv_13= 'subscribedDataPoints' otherlv_14= ':' ( ( ruleQualifiedName ) ) (otherlv_16= ',' ( ( ruleQualifiedName ) ) )*
                    {
                    otherlv_13=(Token)match(input,29,FOLLOW_22); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_13, grammarAccess.getSubscribableItemListAccess().getSubscribedDataPointsKeyword_5_0());
                      			
                    }
                    otherlv_14=(Token)match(input,23,FOLLOW_7); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_14, grammarAccess.getSubscribableItemListAccess().getColonKeyword_5_1());
                      			
                    }
                    // InternalMnc.g:1213:4: ( ( ruleQualifiedName ) )
                    // InternalMnc.g:1214:5: ( ruleQualifiedName )
                    {
                    // InternalMnc.g:1214:5: ( ruleQualifiedName )
                    // InternalMnc.g:1215:6: ruleQualifiedName
                    {
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElement(grammarAccess.getSubscribableItemListRule());
                      						}
                      					
                    }
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSubscribableItemListAccess().getSubscribedDataPointsDataPointCrossReference_5_2_0());
                      					
                    }
                    pushFollow(FOLLOW_30);
                    ruleQualifiedName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:1229:4: (otherlv_16= ',' ( ( ruleQualifiedName ) ) )*
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==17) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // InternalMnc.g:1230:5: otherlv_16= ',' ( ( ruleQualifiedName ) )
                    	    {
                    	    otherlv_16=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_16, grammarAccess.getSubscribableItemListAccess().getCommaKeyword_5_3_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:1234:5: ( ( ruleQualifiedName ) )
                    	    // InternalMnc.g:1235:6: ( ruleQualifiedName )
                    	    {
                    	    // InternalMnc.g:1235:6: ( ruleQualifiedName )
                    	    // InternalMnc.g:1236:7: ruleQualifiedName
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElement(grammarAccess.getSubscribableItemListRule());
                    	      							}
                    	      						
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getSubscribableItemListAccess().getSubscribedDataPointsDataPointCrossReference_5_3_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_30);
                    	    ruleQualifiedName();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop31;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_18=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_18, grammarAccess.getSubscribableItemListAccess().getRightCurlyBracketKeyword_6());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleSubscribableItemList"


    // $ANTLR start "entryRuleAction"
    // InternalMnc.g:1260:1: entryRuleAction returns [EObject current=null] : iv_ruleAction= ruleAction EOF ;
    public final EObject entryRuleAction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAction = null;


        try {
            // InternalMnc.g:1260:47: (iv_ruleAction= ruleAction EOF )
            // InternalMnc.g:1261:2: iv_ruleAction= ruleAction EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActionRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleAction=ruleAction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAction; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleAction"


    // $ANTLR start "ruleAction"
    // InternalMnc.g:1267:1: ruleAction returns [EObject current=null] : ( () otherlv_1= 'Action' otherlv_2= '{' (otherlv_3= 'raise' otherlv_4= 'alarms' otherlv_5= '[' ( (lv_raiseAlarm_6_0= ruleActionAlarm ) )* otherlv_7= ']' )? (otherlv_8= 'fire' otherlv_9= 'commands' otherlv_10= '[' ( (lv_fireCommand_11_0= ruleActionCommand ) )* otherlv_12= ']' )? (otherlv_13= 'generate' otherlv_14= 'events' otherlv_15= '[' ( (lv_publishEvent_16_0= ruleActionEvent ) )* otherlv_17= ']' )? (otherlv_18= 'trigger' otherlv_19= 'data' otherlv_20= '[' ( (lv_triggerDataPoint_21_0= ruleActionDataPoint ) )* otherlv_22= ']' )? (otherlv_23= 'execute' otherlv_24= 'operations' otherlv_25= '[' ( (lv_executeOperation_26_0= ruleActionOperation ) )* otherlv_27= ']' )? (otherlv_28= 'transition' otherlv_29= 'states' otherlv_30= '[' ( (lv_transitionStates_31_0= ruleTransition ) )* otherlv_32= ']' )? otherlv_33= '}' ) ;
    public final EObject ruleAction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_12=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_18=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_22=null;
        Token otherlv_23=null;
        Token otherlv_24=null;
        Token otherlv_25=null;
        Token otherlv_27=null;
        Token otherlv_28=null;
        Token otherlv_29=null;
        Token otherlv_30=null;
        Token otherlv_32=null;
        Token otherlv_33=null;
        EObject lv_raiseAlarm_6_0 = null;

        EObject lv_fireCommand_11_0 = null;

        EObject lv_publishEvent_16_0 = null;

        EObject lv_triggerDataPoint_21_0 = null;

        EObject lv_executeOperation_26_0 = null;

        EObject lv_transitionStates_31_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:1273:2: ( ( () otherlv_1= 'Action' otherlv_2= '{' (otherlv_3= 'raise' otherlv_4= 'alarms' otherlv_5= '[' ( (lv_raiseAlarm_6_0= ruleActionAlarm ) )* otherlv_7= ']' )? (otherlv_8= 'fire' otherlv_9= 'commands' otherlv_10= '[' ( (lv_fireCommand_11_0= ruleActionCommand ) )* otherlv_12= ']' )? (otherlv_13= 'generate' otherlv_14= 'events' otherlv_15= '[' ( (lv_publishEvent_16_0= ruleActionEvent ) )* otherlv_17= ']' )? (otherlv_18= 'trigger' otherlv_19= 'data' otherlv_20= '[' ( (lv_triggerDataPoint_21_0= ruleActionDataPoint ) )* otherlv_22= ']' )? (otherlv_23= 'execute' otherlv_24= 'operations' otherlv_25= '[' ( (lv_executeOperation_26_0= ruleActionOperation ) )* otherlv_27= ']' )? (otherlv_28= 'transition' otherlv_29= 'states' otherlv_30= '[' ( (lv_transitionStates_31_0= ruleTransition ) )* otherlv_32= ']' )? otherlv_33= '}' ) )
            // InternalMnc.g:1274:2: ( () otherlv_1= 'Action' otherlv_2= '{' (otherlv_3= 'raise' otherlv_4= 'alarms' otherlv_5= '[' ( (lv_raiseAlarm_6_0= ruleActionAlarm ) )* otherlv_7= ']' )? (otherlv_8= 'fire' otherlv_9= 'commands' otherlv_10= '[' ( (lv_fireCommand_11_0= ruleActionCommand ) )* otherlv_12= ']' )? (otherlv_13= 'generate' otherlv_14= 'events' otherlv_15= '[' ( (lv_publishEvent_16_0= ruleActionEvent ) )* otherlv_17= ']' )? (otherlv_18= 'trigger' otherlv_19= 'data' otherlv_20= '[' ( (lv_triggerDataPoint_21_0= ruleActionDataPoint ) )* otherlv_22= ']' )? (otherlv_23= 'execute' otherlv_24= 'operations' otherlv_25= '[' ( (lv_executeOperation_26_0= ruleActionOperation ) )* otherlv_27= ']' )? (otherlv_28= 'transition' otherlv_29= 'states' otherlv_30= '[' ( (lv_transitionStates_31_0= ruleTransition ) )* otherlv_32= ']' )? otherlv_33= '}' )
            {
            // InternalMnc.g:1274:2: ( () otherlv_1= 'Action' otherlv_2= '{' (otherlv_3= 'raise' otherlv_4= 'alarms' otherlv_5= '[' ( (lv_raiseAlarm_6_0= ruleActionAlarm ) )* otherlv_7= ']' )? (otherlv_8= 'fire' otherlv_9= 'commands' otherlv_10= '[' ( (lv_fireCommand_11_0= ruleActionCommand ) )* otherlv_12= ']' )? (otherlv_13= 'generate' otherlv_14= 'events' otherlv_15= '[' ( (lv_publishEvent_16_0= ruleActionEvent ) )* otherlv_17= ']' )? (otherlv_18= 'trigger' otherlv_19= 'data' otherlv_20= '[' ( (lv_triggerDataPoint_21_0= ruleActionDataPoint ) )* otherlv_22= ']' )? (otherlv_23= 'execute' otherlv_24= 'operations' otherlv_25= '[' ( (lv_executeOperation_26_0= ruleActionOperation ) )* otherlv_27= ']' )? (otherlv_28= 'transition' otherlv_29= 'states' otherlv_30= '[' ( (lv_transitionStates_31_0= ruleTransition ) )* otherlv_32= ']' )? otherlv_33= '}' )
            // InternalMnc.g:1275:3: () otherlv_1= 'Action' otherlv_2= '{' (otherlv_3= 'raise' otherlv_4= 'alarms' otherlv_5= '[' ( (lv_raiseAlarm_6_0= ruleActionAlarm ) )* otherlv_7= ']' )? (otherlv_8= 'fire' otherlv_9= 'commands' otherlv_10= '[' ( (lv_fireCommand_11_0= ruleActionCommand ) )* otherlv_12= ']' )? (otherlv_13= 'generate' otherlv_14= 'events' otherlv_15= '[' ( (lv_publishEvent_16_0= ruleActionEvent ) )* otherlv_17= ']' )? (otherlv_18= 'trigger' otherlv_19= 'data' otherlv_20= '[' ( (lv_triggerDataPoint_21_0= ruleActionDataPoint ) )* otherlv_22= ']' )? (otherlv_23= 'execute' otherlv_24= 'operations' otherlv_25= '[' ( (lv_executeOperation_26_0= ruleActionOperation ) )* otherlv_27= ']' )? (otherlv_28= 'transition' otherlv_29= 'states' otherlv_30= '[' ( (lv_transitionStates_31_0= ruleTransition ) )* otherlv_32= ']' )? otherlv_33= '}'
            {
            // InternalMnc.g:1275:3: ()
            // InternalMnc.g:1276:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getActionAccess().getActionAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,31,FOLLOW_26); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getActionAccess().getActionKeyword_1());
              		
            }
            otherlv_2=(Token)match(input,26,FOLLOW_31); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getActionAccess().getLeftCurlyBracketKeyword_2());
              		
            }
            // InternalMnc.g:1290:3: (otherlv_3= 'raise' otherlv_4= 'alarms' otherlv_5= '[' ( (lv_raiseAlarm_6_0= ruleActionAlarm ) )* otherlv_7= ']' )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==32) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // InternalMnc.g:1291:4: otherlv_3= 'raise' otherlv_4= 'alarms' otherlv_5= '[' ( (lv_raiseAlarm_6_0= ruleActionAlarm ) )* otherlv_7= ']'
                    {
                    otherlv_3=(Token)match(input,32,FOLLOW_32); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_3, grammarAccess.getActionAccess().getRaiseKeyword_3_0());
                      			
                    }
                    otherlv_4=(Token)match(input,33,FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_4, grammarAccess.getActionAccess().getAlarmsKeyword_3_1());
                      			
                    }
                    otherlv_5=(Token)match(input,16,FOLLOW_33); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_5, grammarAccess.getActionAccess().getLeftSquareBracketKeyword_3_2());
                      			
                    }
                    // InternalMnc.g:1303:4: ( (lv_raiseAlarm_6_0= ruleActionAlarm ) )*
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( (LA33_0==RULE_ID) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // InternalMnc.g:1304:5: (lv_raiseAlarm_6_0= ruleActionAlarm )
                    	    {
                    	    // InternalMnc.g:1304:5: (lv_raiseAlarm_6_0= ruleActionAlarm )
                    	    // InternalMnc.g:1305:6: lv_raiseAlarm_6_0= ruleActionAlarm
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getActionAccess().getRaiseAlarmActionAlarmParserRuleCall_3_3_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_33);
                    	    lv_raiseAlarm_6_0=ruleActionAlarm();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getActionRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"raiseAlarm",
                    	      							lv_raiseAlarm_6_0,
                    	      							"com.mncml.dsl.Mnc.ActionAlarm");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop33;
                        }
                    } while (true);

                    otherlv_7=(Token)match(input,18,FOLLOW_34); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_7, grammarAccess.getActionAccess().getRightSquareBracketKeyword_3_4());
                      			
                    }

                    }
                    break;

            }

            // InternalMnc.g:1327:3: (otherlv_8= 'fire' otherlv_9= 'commands' otherlv_10= '[' ( (lv_fireCommand_11_0= ruleActionCommand ) )* otherlv_12= ']' )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==34) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // InternalMnc.g:1328:4: otherlv_8= 'fire' otherlv_9= 'commands' otherlv_10= '[' ( (lv_fireCommand_11_0= ruleActionCommand ) )* otherlv_12= ']'
                    {
                    otherlv_8=(Token)match(input,34,FOLLOW_35); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_8, grammarAccess.getActionAccess().getFireKeyword_4_0());
                      			
                    }
                    otherlv_9=(Token)match(input,35,FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_9, grammarAccess.getActionAccess().getCommandsKeyword_4_1());
                      			
                    }
                    otherlv_10=(Token)match(input,16,FOLLOW_33); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_10, grammarAccess.getActionAccess().getLeftSquareBracketKeyword_4_2());
                      			
                    }
                    // InternalMnc.g:1340:4: ( (lv_fireCommand_11_0= ruleActionCommand ) )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( (LA35_0==RULE_ID) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // InternalMnc.g:1341:5: (lv_fireCommand_11_0= ruleActionCommand )
                    	    {
                    	    // InternalMnc.g:1341:5: (lv_fireCommand_11_0= ruleActionCommand )
                    	    // InternalMnc.g:1342:6: lv_fireCommand_11_0= ruleActionCommand
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getActionAccess().getFireCommandActionCommandParserRuleCall_4_3_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_33);
                    	    lv_fireCommand_11_0=ruleActionCommand();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getActionRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"fireCommand",
                    	      							lv_fireCommand_11_0,
                    	      							"com.mncml.dsl.Mnc.ActionCommand");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop35;
                        }
                    } while (true);

                    otherlv_12=(Token)match(input,18,FOLLOW_36); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_12, grammarAccess.getActionAccess().getRightSquareBracketKeyword_4_4());
                      			
                    }

                    }
                    break;

            }

            // InternalMnc.g:1364:3: (otherlv_13= 'generate' otherlv_14= 'events' otherlv_15= '[' ( (lv_publishEvent_16_0= ruleActionEvent ) )* otherlv_17= ']' )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==36) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // InternalMnc.g:1365:4: otherlv_13= 'generate' otherlv_14= 'events' otherlv_15= '[' ( (lv_publishEvent_16_0= ruleActionEvent ) )* otherlv_17= ']'
                    {
                    otherlv_13=(Token)match(input,36,FOLLOW_37); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_13, grammarAccess.getActionAccess().getGenerateKeyword_5_0());
                      			
                    }
                    otherlv_14=(Token)match(input,37,FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_14, grammarAccess.getActionAccess().getEventsKeyword_5_1());
                      			
                    }
                    otherlv_15=(Token)match(input,16,FOLLOW_33); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_15, grammarAccess.getActionAccess().getLeftSquareBracketKeyword_5_2());
                      			
                    }
                    // InternalMnc.g:1377:4: ( (lv_publishEvent_16_0= ruleActionEvent ) )*
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0==RULE_ID) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // InternalMnc.g:1378:5: (lv_publishEvent_16_0= ruleActionEvent )
                    	    {
                    	    // InternalMnc.g:1378:5: (lv_publishEvent_16_0= ruleActionEvent )
                    	    // InternalMnc.g:1379:6: lv_publishEvent_16_0= ruleActionEvent
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getActionAccess().getPublishEventActionEventParserRuleCall_5_3_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_33);
                    	    lv_publishEvent_16_0=ruleActionEvent();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getActionRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"publishEvent",
                    	      							lv_publishEvent_16_0,
                    	      							"com.mncml.dsl.Mnc.ActionEvent");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop37;
                        }
                    } while (true);

                    otherlv_17=(Token)match(input,18,FOLLOW_38); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_17, grammarAccess.getActionAccess().getRightSquareBracketKeyword_5_4());
                      			
                    }

                    }
                    break;

            }

            // InternalMnc.g:1401:3: (otherlv_18= 'trigger' otherlv_19= 'data' otherlv_20= '[' ( (lv_triggerDataPoint_21_0= ruleActionDataPoint ) )* otherlv_22= ']' )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==38) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // InternalMnc.g:1402:4: otherlv_18= 'trigger' otherlv_19= 'data' otherlv_20= '[' ( (lv_triggerDataPoint_21_0= ruleActionDataPoint ) )* otherlv_22= ']'
                    {
                    otherlv_18=(Token)match(input,38,FOLLOW_39); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_18, grammarAccess.getActionAccess().getTriggerKeyword_6_0());
                      			
                    }
                    otherlv_19=(Token)match(input,39,FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_19, grammarAccess.getActionAccess().getDataKeyword_6_1());
                      			
                    }
                    otherlv_20=(Token)match(input,16,FOLLOW_33); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_20, grammarAccess.getActionAccess().getLeftSquareBracketKeyword_6_2());
                      			
                    }
                    // InternalMnc.g:1414:4: ( (lv_triggerDataPoint_21_0= ruleActionDataPoint ) )*
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( (LA39_0==RULE_ID) ) {
                            alt39=1;
                        }


                        switch (alt39) {
                    	case 1 :
                    	    // InternalMnc.g:1415:5: (lv_triggerDataPoint_21_0= ruleActionDataPoint )
                    	    {
                    	    // InternalMnc.g:1415:5: (lv_triggerDataPoint_21_0= ruleActionDataPoint )
                    	    // InternalMnc.g:1416:6: lv_triggerDataPoint_21_0= ruleActionDataPoint
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getActionAccess().getTriggerDataPointActionDataPointParserRuleCall_6_3_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_33);
                    	    lv_triggerDataPoint_21_0=ruleActionDataPoint();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getActionRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"triggerDataPoint",
                    	      							lv_triggerDataPoint_21_0,
                    	      							"com.mncml.dsl.Mnc.ActionDataPoint");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop39;
                        }
                    } while (true);

                    otherlv_22=(Token)match(input,18,FOLLOW_40); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_22, grammarAccess.getActionAccess().getRightSquareBracketKeyword_6_4());
                      			
                    }

                    }
                    break;

            }

            // InternalMnc.g:1438:3: (otherlv_23= 'execute' otherlv_24= 'operations' otherlv_25= '[' ( (lv_executeOperation_26_0= ruleActionOperation ) )* otherlv_27= ']' )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==40) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // InternalMnc.g:1439:4: otherlv_23= 'execute' otherlv_24= 'operations' otherlv_25= '[' ( (lv_executeOperation_26_0= ruleActionOperation ) )* otherlv_27= ']'
                    {
                    otherlv_23=(Token)match(input,40,FOLLOW_41); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_23, grammarAccess.getActionAccess().getExecuteKeyword_7_0());
                      			
                    }
                    otherlv_24=(Token)match(input,41,FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_24, grammarAccess.getActionAccess().getOperationsKeyword_7_1());
                      			
                    }
                    otherlv_25=(Token)match(input,16,FOLLOW_33); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_25, grammarAccess.getActionAccess().getLeftSquareBracketKeyword_7_2());
                      			
                    }
                    // InternalMnc.g:1451:4: ( (lv_executeOperation_26_0= ruleActionOperation ) )*
                    loop41:
                    do {
                        int alt41=2;
                        int LA41_0 = input.LA(1);

                        if ( (LA41_0==RULE_ID) ) {
                            alt41=1;
                        }


                        switch (alt41) {
                    	case 1 :
                    	    // InternalMnc.g:1452:5: (lv_executeOperation_26_0= ruleActionOperation )
                    	    {
                    	    // InternalMnc.g:1452:5: (lv_executeOperation_26_0= ruleActionOperation )
                    	    // InternalMnc.g:1453:6: lv_executeOperation_26_0= ruleActionOperation
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getActionAccess().getExecuteOperationActionOperationParserRuleCall_7_3_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_33);
                    	    lv_executeOperation_26_0=ruleActionOperation();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getActionRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"executeOperation",
                    	      							lv_executeOperation_26_0,
                    	      							"com.mncml.dsl.Mnc.ActionOperation");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop41;
                        }
                    } while (true);

                    otherlv_27=(Token)match(input,18,FOLLOW_42); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_27, grammarAccess.getActionAccess().getRightSquareBracketKeyword_7_4());
                      			
                    }

                    }
                    break;

            }

            // InternalMnc.g:1475:3: (otherlv_28= 'transition' otherlv_29= 'states' otherlv_30= '[' ( (lv_transitionStates_31_0= ruleTransition ) )* otherlv_32= ']' )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==42) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // InternalMnc.g:1476:4: otherlv_28= 'transition' otherlv_29= 'states' otherlv_30= '[' ( (lv_transitionStates_31_0= ruleTransition ) )* otherlv_32= ']'
                    {
                    otherlv_28=(Token)match(input,42,FOLLOW_43); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_28, grammarAccess.getActionAccess().getTransitionKeyword_8_0());
                      			
                    }
                    otherlv_29=(Token)match(input,43,FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_29, grammarAccess.getActionAccess().getStatesKeyword_8_1());
                      			
                    }
                    otherlv_30=(Token)match(input,16,FOLLOW_44); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_30, grammarAccess.getActionAccess().getLeftSquareBracketKeyword_8_2());
                      			
                    }
                    // InternalMnc.g:1488:4: ( (lv_transitionStates_31_0= ruleTransition ) )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==81) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // InternalMnc.g:1489:5: (lv_transitionStates_31_0= ruleTransition )
                    	    {
                    	    // InternalMnc.g:1489:5: (lv_transitionStates_31_0= ruleTransition )
                    	    // InternalMnc.g:1490:6: lv_transitionStates_31_0= ruleTransition
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getActionAccess().getTransitionStatesTransitionParserRuleCall_8_3_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_44);
                    	    lv_transitionStates_31_0=ruleTransition();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getActionRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"transitionStates",
                    	      							lv_transitionStates_31_0,
                    	      							"com.mncml.dsl.Mnc.Transition");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);

                    otherlv_32=(Token)match(input,18,FOLLOW_45); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_32, grammarAccess.getActionAccess().getRightSquareBracketKeyword_8_4());
                      			
                    }

                    }
                    break;

            }

            otherlv_33=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_33, grammarAccess.getActionAccess().getRightCurlyBracketKeyword_9());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleAction"


    // $ANTLR start "entryRuleActionCommand"
    // InternalMnc.g:1520:1: entryRuleActionCommand returns [EObject current=null] : iv_ruleActionCommand= ruleActionCommand EOF ;
    public final EObject entryRuleActionCommand() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionCommand = null;


        try {
            // InternalMnc.g:1520:54: (iv_ruleActionCommand= ruleActionCommand EOF )
            // InternalMnc.g:1521:2: iv_ruleActionCommand= ruleActionCommand EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActionCommandRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleActionCommand=ruleActionCommand();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleActionCommand; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleActionCommand"


    // $ANTLR start "ruleActionCommand"
    // InternalMnc.g:1527:1: ruleActionCommand returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' (otherlv_5= '->' otherlv_6= 'expected' ( (lv_responseHandling_7_0= ruleResponseBlock ) )* )? ) ;
    public final EObject ruleActionCommand() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        EObject lv_actionParemeter_3_0 = null;

        EObject lv_responseHandling_7_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:1533:2: ( ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' (otherlv_5= '->' otherlv_6= 'expected' ( (lv_responseHandling_7_0= ruleResponseBlock ) )* )? ) )
            // InternalMnc.g:1534:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' (otherlv_5= '->' otherlv_6= 'expected' ( (lv_responseHandling_7_0= ruleResponseBlock ) )* )? )
            {
            // InternalMnc.g:1534:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' (otherlv_5= '->' otherlv_6= 'expected' ( (lv_responseHandling_7_0= ruleResponseBlock ) )* )? )
            // InternalMnc.g:1535:3: () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' (otherlv_5= '->' otherlv_6= 'expected' ( (lv_responseHandling_7_0= ruleResponseBlock ) )* )?
            {
            // InternalMnc.g:1535:3: ()
            // InternalMnc.g:1536:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getActionCommandAccess().getActionCommandAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:1542:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:1543:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:1543:4: ( ruleQualifiedName )
            // InternalMnc.g:1544:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getActionCommandRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getActionCommandAccess().getCommandCommandCrossReference_1_0());
              				
            }
            pushFollow(FOLLOW_46);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_2=(Token)match(input,44,FOLLOW_47); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getActionCommandAccess().getLeftParenthesisKeyword_2());
              		
            }
            // InternalMnc.g:1562:3: ( (lv_actionParemeter_3_0= ruleActionParemeter ) )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==RULE_ID||(LA45_0>=RULE_STRING && LA45_0<=RULE_INT)||LA45_0==16||LA45_0==87||(LA45_0>=97 && LA45_0<=100)) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // InternalMnc.g:1563:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    {
                    // InternalMnc.g:1563:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    // InternalMnc.g:1564:5: lv_actionParemeter_3_0= ruleActionParemeter
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getActionCommandAccess().getActionParemeterActionParemeterParserRuleCall_3_0());
                      				
                    }
                    pushFollow(FOLLOW_48);
                    lv_actionParemeter_3_0=ruleActionParemeter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getActionCommandRule());
                      					}
                      					set(
                      						current,
                      						"actionParemeter",
                      						lv_actionParemeter_3_0,
                      						"com.mncml.dsl.Mnc.ActionParemeter");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,45,FOLLOW_49); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_4, grammarAccess.getActionCommandAccess().getRightParenthesisKeyword_4());
              		
            }
            // InternalMnc.g:1585:3: (otherlv_5= '->' otherlv_6= 'expected' ( (lv_responseHandling_7_0= ruleResponseBlock ) )* )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==46) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // InternalMnc.g:1586:4: otherlv_5= '->' otherlv_6= 'expected' ( (lv_responseHandling_7_0= ruleResponseBlock ) )*
                    {
                    otherlv_5=(Token)match(input,46,FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_5, grammarAccess.getActionCommandAccess().getHyphenMinusGreaterThanSignKeyword_5_0());
                      			
                    }
                    otherlv_6=(Token)match(input,47,FOLLOW_51); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_6, grammarAccess.getActionCommandAccess().getExpectedKeyword_5_1());
                      			
                    }
                    // InternalMnc.g:1594:4: ( (lv_responseHandling_7_0= ruleResponseBlock ) )*
                    loop46:
                    do {
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==64) ) {
                            alt46=1;
                        }


                        switch (alt46) {
                    	case 1 :
                    	    // InternalMnc.g:1595:5: (lv_responseHandling_7_0= ruleResponseBlock )
                    	    {
                    	    // InternalMnc.g:1595:5: (lv_responseHandling_7_0= ruleResponseBlock )
                    	    // InternalMnc.g:1596:6: lv_responseHandling_7_0= ruleResponseBlock
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getActionCommandAccess().getResponseHandlingResponseBlockParserRuleCall_5_2_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_51);
                    	    lv_responseHandling_7_0=ruleResponseBlock();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getActionCommandRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"responseHandling",
                    	      							lv_responseHandling_7_0,
                    	      							"com.mncml.dsl.Mnc.ResponseBlock");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop46;
                        }
                    } while (true);


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleActionCommand"


    // $ANTLR start "entryRuleActionAlarm"
    // InternalMnc.g:1618:1: entryRuleActionAlarm returns [EObject current=null] : iv_ruleActionAlarm= ruleActionAlarm EOF ;
    public final EObject entryRuleActionAlarm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionAlarm = null;


        try {
            // InternalMnc.g:1618:52: (iv_ruleActionAlarm= ruleActionAlarm EOF )
            // InternalMnc.g:1619:2: iv_ruleActionAlarm= ruleActionAlarm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActionAlarmRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleActionAlarm=ruleActionAlarm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleActionAlarm; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleActionAlarm"


    // $ANTLR start "ruleActionAlarm"
    // InternalMnc.g:1625:1: ruleActionAlarm returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' ) ;
    public final EObject ruleActionAlarm() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_actionParemeter_3_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:1631:2: ( ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' ) )
            // InternalMnc.g:1632:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' )
            {
            // InternalMnc.g:1632:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' )
            // InternalMnc.g:1633:3: () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')'
            {
            // InternalMnc.g:1633:3: ()
            // InternalMnc.g:1634:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getActionAlarmAccess().getActionAlarmAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:1640:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:1641:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:1641:4: ( ruleQualifiedName )
            // InternalMnc.g:1642:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getActionAlarmRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getActionAlarmAccess().getAlarmAlarmCrossReference_1_0());
              				
            }
            pushFollow(FOLLOW_46);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_2=(Token)match(input,44,FOLLOW_47); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getActionAlarmAccess().getLeftParenthesisKeyword_2());
              		
            }
            // InternalMnc.g:1660:3: ( (lv_actionParemeter_3_0= ruleActionParemeter ) )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==RULE_ID||(LA48_0>=RULE_STRING && LA48_0<=RULE_INT)||LA48_0==16||LA48_0==87||(LA48_0>=97 && LA48_0<=100)) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // InternalMnc.g:1661:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    {
                    // InternalMnc.g:1661:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    // InternalMnc.g:1662:5: lv_actionParemeter_3_0= ruleActionParemeter
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getActionAlarmAccess().getActionParemeterActionParemeterParserRuleCall_3_0());
                      				
                    }
                    pushFollow(FOLLOW_48);
                    lv_actionParemeter_3_0=ruleActionParemeter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getActionAlarmRule());
                      					}
                      					set(
                      						current,
                      						"actionParemeter",
                      						lv_actionParemeter_3_0,
                      						"com.mncml.dsl.Mnc.ActionParemeter");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,45,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_4, grammarAccess.getActionAlarmAccess().getRightParenthesisKeyword_4());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleActionAlarm"


    // $ANTLR start "entryRuleActionEvent"
    // InternalMnc.g:1687:1: entryRuleActionEvent returns [EObject current=null] : iv_ruleActionEvent= ruleActionEvent EOF ;
    public final EObject entryRuleActionEvent() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionEvent = null;


        try {
            // InternalMnc.g:1687:52: (iv_ruleActionEvent= ruleActionEvent EOF )
            // InternalMnc.g:1688:2: iv_ruleActionEvent= ruleActionEvent EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActionEventRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleActionEvent=ruleActionEvent();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleActionEvent; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleActionEvent"


    // $ANTLR start "ruleActionEvent"
    // InternalMnc.g:1694:1: ruleActionEvent returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' ) ;
    public final EObject ruleActionEvent() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_actionParemeter_3_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:1700:2: ( ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' ) )
            // InternalMnc.g:1701:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' )
            {
            // InternalMnc.g:1701:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' )
            // InternalMnc.g:1702:3: () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')'
            {
            // InternalMnc.g:1702:3: ()
            // InternalMnc.g:1703:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getActionEventAccess().getActionEventAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:1709:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:1710:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:1710:4: ( ruleQualifiedName )
            // InternalMnc.g:1711:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getActionEventRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getActionEventAccess().getEventEventCrossReference_1_0());
              				
            }
            pushFollow(FOLLOW_46);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_2=(Token)match(input,44,FOLLOW_47); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getActionEventAccess().getLeftParenthesisKeyword_2());
              		
            }
            // InternalMnc.g:1729:3: ( (lv_actionParemeter_3_0= ruleActionParemeter ) )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==RULE_ID||(LA49_0>=RULE_STRING && LA49_0<=RULE_INT)||LA49_0==16||LA49_0==87||(LA49_0>=97 && LA49_0<=100)) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // InternalMnc.g:1730:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    {
                    // InternalMnc.g:1730:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    // InternalMnc.g:1731:5: lv_actionParemeter_3_0= ruleActionParemeter
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getActionEventAccess().getActionParemeterActionParemeterParserRuleCall_3_0());
                      				
                    }
                    pushFollow(FOLLOW_48);
                    lv_actionParemeter_3_0=ruleActionParemeter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getActionEventRule());
                      					}
                      					set(
                      						current,
                      						"actionParemeter",
                      						lv_actionParemeter_3_0,
                      						"com.mncml.dsl.Mnc.ActionParemeter");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,45,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_4, grammarAccess.getActionEventAccess().getRightParenthesisKeyword_4());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleActionEvent"


    // $ANTLR start "entryRuleActionDataPoint"
    // InternalMnc.g:1756:1: entryRuleActionDataPoint returns [EObject current=null] : iv_ruleActionDataPoint= ruleActionDataPoint EOF ;
    public final EObject entryRuleActionDataPoint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionDataPoint = null;


        try {
            // InternalMnc.g:1756:56: (iv_ruleActionDataPoint= ruleActionDataPoint EOF )
            // InternalMnc.g:1757:2: iv_ruleActionDataPoint= ruleActionDataPoint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActionDataPointRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleActionDataPoint=ruleActionDataPoint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleActionDataPoint; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleActionDataPoint"


    // $ANTLR start "ruleActionDataPoint"
    // InternalMnc.g:1763:1: ruleActionDataPoint returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' ) ;
    public final EObject ruleActionDataPoint() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_actionParemeter_3_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:1769:2: ( ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' ) )
            // InternalMnc.g:1770:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' )
            {
            // InternalMnc.g:1770:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' )
            // InternalMnc.g:1771:3: () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')'
            {
            // InternalMnc.g:1771:3: ()
            // InternalMnc.g:1772:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getActionDataPointAccess().getActionDataPointAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:1778:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:1779:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:1779:4: ( ruleQualifiedName )
            // InternalMnc.g:1780:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getActionDataPointRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getActionDataPointAccess().getDataPointDataPointCrossReference_1_0());
              				
            }
            pushFollow(FOLLOW_46);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_2=(Token)match(input,44,FOLLOW_47); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getActionDataPointAccess().getLeftParenthesisKeyword_2());
              		
            }
            // InternalMnc.g:1798:3: ( (lv_actionParemeter_3_0= ruleActionParemeter ) )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==RULE_ID||(LA50_0>=RULE_STRING && LA50_0<=RULE_INT)||LA50_0==16||LA50_0==87||(LA50_0>=97 && LA50_0<=100)) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // InternalMnc.g:1799:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    {
                    // InternalMnc.g:1799:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    // InternalMnc.g:1800:5: lv_actionParemeter_3_0= ruleActionParemeter
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getActionDataPointAccess().getActionParemeterActionParemeterParserRuleCall_3_0());
                      				
                    }
                    pushFollow(FOLLOW_48);
                    lv_actionParemeter_3_0=ruleActionParemeter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getActionDataPointRule());
                      					}
                      					set(
                      						current,
                      						"actionParemeter",
                      						lv_actionParemeter_3_0,
                      						"com.mncml.dsl.Mnc.ActionParemeter");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,45,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_4, grammarAccess.getActionDataPointAccess().getRightParenthesisKeyword_4());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleActionDataPoint"


    // $ANTLR start "entryRuleActionOperation"
    // InternalMnc.g:1825:1: entryRuleActionOperation returns [EObject current=null] : iv_ruleActionOperation= ruleActionOperation EOF ;
    public final EObject entryRuleActionOperation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionOperation = null;


        try {
            // InternalMnc.g:1825:56: (iv_ruleActionOperation= ruleActionOperation EOF )
            // InternalMnc.g:1826:2: iv_ruleActionOperation= ruleActionOperation EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActionOperationRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleActionOperation=ruleActionOperation();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleActionOperation; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleActionOperation"


    // $ANTLR start "ruleActionOperation"
    // InternalMnc.g:1832:1: ruleActionOperation returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' ) ;
    public final EObject ruleActionOperation() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_actionParemeter_3_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:1838:2: ( ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' ) )
            // InternalMnc.g:1839:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' )
            {
            // InternalMnc.g:1839:2: ( () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')' )
            // InternalMnc.g:1840:3: () ( ( ruleQualifiedName ) ) otherlv_2= '(' ( (lv_actionParemeter_3_0= ruleActionParemeter ) )? otherlv_4= ')'
            {
            // InternalMnc.g:1840:3: ()
            // InternalMnc.g:1841:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getActionOperationAccess().getActionOperationAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:1847:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:1848:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:1848:4: ( ruleQualifiedName )
            // InternalMnc.g:1849:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getActionOperationRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getActionOperationAccess().getOperationOperationCrossReference_1_0());
              				
            }
            pushFollow(FOLLOW_46);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_2=(Token)match(input,44,FOLLOW_47); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getActionOperationAccess().getLeftParenthesisKeyword_2());
              		
            }
            // InternalMnc.g:1867:3: ( (lv_actionParemeter_3_0= ruleActionParemeter ) )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==RULE_ID||(LA51_0>=RULE_STRING && LA51_0<=RULE_INT)||LA51_0==16||LA51_0==87||(LA51_0>=97 && LA51_0<=100)) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // InternalMnc.g:1868:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    {
                    // InternalMnc.g:1868:4: (lv_actionParemeter_3_0= ruleActionParemeter )
                    // InternalMnc.g:1869:5: lv_actionParemeter_3_0= ruleActionParemeter
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getActionOperationAccess().getActionParemeterActionParemeterParserRuleCall_3_0());
                      				
                    }
                    pushFollow(FOLLOW_48);
                    lv_actionParemeter_3_0=ruleActionParemeter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getActionOperationRule());
                      					}
                      					set(
                      						current,
                      						"actionParemeter",
                      						lv_actionParemeter_3_0,
                      						"com.mncml.dsl.Mnc.ActionParemeter");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,45,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_4, grammarAccess.getActionOperationAccess().getRightParenthesisKeyword_4());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleActionOperation"


    // $ANTLR start "entryRuleActionParemeter"
    // InternalMnc.g:1894:1: entryRuleActionParemeter returns [EObject current=null] : iv_ruleActionParemeter= ruleActionParemeter EOF ;
    public final EObject entryRuleActionParemeter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionParemeter = null;


        try {
            // InternalMnc.g:1894:56: (iv_ruleActionParemeter= ruleActionParemeter EOF )
            // InternalMnc.g:1895:2: iv_ruleActionParemeter= ruleActionParemeter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActionParemeterRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleActionParemeter=ruleActionParemeter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleActionParemeter; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleActionParemeter"


    // $ANTLR start "ruleActionParemeter"
    // InternalMnc.g:1901:1: ruleActionParemeter returns [EObject current=null] : ( ( ( (lv_parameterValues_0_0= rulePrimitiveValue ) ) (otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) ) )* ) | ( ( (lv_parameterMappings_3_0= ruleParameterTranslation ) ) (otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) ) )* ) ) ;
    public final EObject ruleActionParemeter() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_4=null;
        EObject lv_parameterValues_0_0 = null;

        EObject lv_parameterValues_2_0 = null;

        EObject lv_parameterMappings_3_0 = null;

        EObject lv_parameterMappings_5_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:1907:2: ( ( ( ( (lv_parameterValues_0_0= rulePrimitiveValue ) ) (otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) ) )* ) | ( ( (lv_parameterMappings_3_0= ruleParameterTranslation ) ) (otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) ) )* ) ) )
            // InternalMnc.g:1908:2: ( ( ( (lv_parameterValues_0_0= rulePrimitiveValue ) ) (otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) ) )* ) | ( ( (lv_parameterMappings_3_0= ruleParameterTranslation ) ) (otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) ) )* ) )
            {
            // InternalMnc.g:1908:2: ( ( ( (lv_parameterValues_0_0= rulePrimitiveValue ) ) (otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) ) )* ) | ( ( (lv_parameterMappings_3_0= ruleParameterTranslation ) ) (otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) ) )* ) )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==RULE_ID||(LA54_0>=RULE_STRING && LA54_0<=RULE_INT)||LA54_0==16||(LA54_0>=97 && LA54_0<=100)) ) {
                alt54=1;
            }
            else if ( (LA54_0==87) ) {
                alt54=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // InternalMnc.g:1909:3: ( ( (lv_parameterValues_0_0= rulePrimitiveValue ) ) (otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) ) )* )
                    {
                    // InternalMnc.g:1909:3: ( ( (lv_parameterValues_0_0= rulePrimitiveValue ) ) (otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) ) )* )
                    // InternalMnc.g:1910:4: ( (lv_parameterValues_0_0= rulePrimitiveValue ) ) (otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) ) )*
                    {
                    // InternalMnc.g:1910:4: ( (lv_parameterValues_0_0= rulePrimitiveValue ) )
                    // InternalMnc.g:1911:5: (lv_parameterValues_0_0= rulePrimitiveValue )
                    {
                    // InternalMnc.g:1911:5: (lv_parameterValues_0_0= rulePrimitiveValue )
                    // InternalMnc.g:1912:6: lv_parameterValues_0_0= rulePrimitiveValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getActionParemeterAccess().getParameterValuesPrimitiveValueParserRuleCall_0_0_0());
                      					
                    }
                    pushFollow(FOLLOW_25);
                    lv_parameterValues_0_0=rulePrimitiveValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getActionParemeterRule());
                      						}
                      						add(
                      							current,
                      							"parameterValues",
                      							lv_parameterValues_0_0,
                      							"com.dml.dsl.Dml.PrimitiveValue");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:1929:4: (otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) ) )*
                    loop52:
                    do {
                        int alt52=2;
                        int LA52_0 = input.LA(1);

                        if ( (LA52_0==17) ) {
                            alt52=1;
                        }


                        switch (alt52) {
                    	case 1 :
                    	    // InternalMnc.g:1930:5: otherlv_1= ',' ( (lv_parameterValues_2_0= rulePrimitiveValue ) )
                    	    {
                    	    otherlv_1=(Token)match(input,17,FOLLOW_20); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_1, grammarAccess.getActionParemeterAccess().getCommaKeyword_0_1_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:1934:5: ( (lv_parameterValues_2_0= rulePrimitiveValue ) )
                    	    // InternalMnc.g:1935:6: (lv_parameterValues_2_0= rulePrimitiveValue )
                    	    {
                    	    // InternalMnc.g:1935:6: (lv_parameterValues_2_0= rulePrimitiveValue )
                    	    // InternalMnc.g:1936:7: lv_parameterValues_2_0= rulePrimitiveValue
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getActionParemeterAccess().getParameterValuesPrimitiveValueParserRuleCall_0_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_25);
                    	    lv_parameterValues_2_0=rulePrimitiveValue();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElementForParent(grammarAccess.getActionParemeterRule());
                    	      							}
                    	      							add(
                    	      								current,
                    	      								"parameterValues",
                    	      								lv_parameterValues_2_0,
                    	      								"com.dml.dsl.Dml.PrimitiveValue");
                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop52;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // InternalMnc.g:1956:3: ( ( (lv_parameterMappings_3_0= ruleParameterTranslation ) ) (otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) ) )* )
                    {
                    // InternalMnc.g:1956:3: ( ( (lv_parameterMappings_3_0= ruleParameterTranslation ) ) (otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) ) )* )
                    // InternalMnc.g:1957:4: ( (lv_parameterMappings_3_0= ruleParameterTranslation ) ) (otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) ) )*
                    {
                    // InternalMnc.g:1957:4: ( (lv_parameterMappings_3_0= ruleParameterTranslation ) )
                    // InternalMnc.g:1958:5: (lv_parameterMappings_3_0= ruleParameterTranslation )
                    {
                    // InternalMnc.g:1958:5: (lv_parameterMappings_3_0= ruleParameterTranslation )
                    // InternalMnc.g:1959:6: lv_parameterMappings_3_0= ruleParameterTranslation
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getActionParemeterAccess().getParameterMappingsParameterTranslationParserRuleCall_1_0_0());
                      					
                    }
                    pushFollow(FOLLOW_25);
                    lv_parameterMappings_3_0=ruleParameterTranslation();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getActionParemeterRule());
                      						}
                      						add(
                      							current,
                      							"parameterMappings",
                      							lv_parameterMappings_3_0,
                      							"com.mncml.dsl.Mnc.ParameterTranslation");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:1976:4: (otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) ) )*
                    loop53:
                    do {
                        int alt53=2;
                        int LA53_0 = input.LA(1);

                        if ( (LA53_0==17) ) {
                            alt53=1;
                        }


                        switch (alt53) {
                    	case 1 :
                    	    // InternalMnc.g:1977:5: otherlv_4= ',' ( (lv_parameterMappings_5_0= ruleParameterTranslation ) )
                    	    {
                    	    otherlv_4=(Token)match(input,17,FOLLOW_52); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_4, grammarAccess.getActionParemeterAccess().getCommaKeyword_1_1_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:1981:5: ( (lv_parameterMappings_5_0= ruleParameterTranslation ) )
                    	    // InternalMnc.g:1982:6: (lv_parameterMappings_5_0= ruleParameterTranslation )
                    	    {
                    	    // InternalMnc.g:1982:6: (lv_parameterMappings_5_0= ruleParameterTranslation )
                    	    // InternalMnc.g:1983:7: lv_parameterMappings_5_0= ruleParameterTranslation
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getActionParemeterAccess().getParameterMappingsParameterTranslationParserRuleCall_1_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_25);
                    	    lv_parameterMappings_5_0=ruleParameterTranslation();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElementForParent(grammarAccess.getActionParemeterRule());
                    	      							}
                    	      							add(
                    	      								current,
                    	      								"parameterMappings",
                    	      								lv_parameterMappings_5_0,
                    	      								"com.mncml.dsl.Mnc.ParameterTranslation");
                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop53;
                        }
                    } while (true);


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleActionParemeter"


    // $ANTLR start "entryRuleInterfaceDescription"
    // InternalMnc.g:2006:1: entryRuleInterfaceDescription returns [EObject current=null] : iv_ruleInterfaceDescription= ruleInterfaceDescription EOF ;
    public final EObject entryRuleInterfaceDescription() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleInterfaceDescription = null;


        try {
            // InternalMnc.g:2006:61: (iv_ruleInterfaceDescription= ruleInterfaceDescription EOF )
            // InternalMnc.g:2007:2: iv_ruleInterfaceDescription= ruleInterfaceDescription EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getInterfaceDescriptionRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleInterfaceDescription=ruleInterfaceDescription();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleInterfaceDescription; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleInterfaceDescription"


    // $ANTLR start "ruleInterfaceDescription"
    // InternalMnc.g:2013:1: ruleInterfaceDescription returns [EObject current=null] : ( () otherlv_1= 'InterfaceDescription' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'uses' ( ( ruleQualifiedName ) ) (otherlv_5= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_7= '{' ( ( ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* ) ) ) otherlv_37= '}' ) ;
    public final EObject ruleInterfaceDescription() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_16=null;
        Token otherlv_18=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_22=null;
        Token otherlv_23=null;
        Token otherlv_24=null;
        Token otherlv_26=null;
        Token otherlv_27=null;
        Token otherlv_28=null;
        Token otherlv_30=null;
        Token otherlv_31=null;
        Token otherlv_32=null;
        Token otherlv_34=null;
        Token otherlv_37=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_port_10_0 = null;

        EObject lv_dataPoints_13_0 = null;

        EObject lv_alarms_17_0 = null;

        EObject lv_commands_21_0 = null;

        EObject lv_events_25_0 = null;

        EObject lv_responses_29_0 = null;

        EObject lv_operatingStatesUtility_33_0 = null;

        EObject lv_subscribedItems_35_0 = null;

        EObject lv_ipaddress_36_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:2019:2: ( ( () otherlv_1= 'InterfaceDescription' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'uses' ( ( ruleQualifiedName ) ) (otherlv_5= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_7= '{' ( ( ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* ) ) ) otherlv_37= '}' ) )
            // InternalMnc.g:2020:2: ( () otherlv_1= 'InterfaceDescription' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'uses' ( ( ruleQualifiedName ) ) (otherlv_5= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_7= '{' ( ( ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* ) ) ) otherlv_37= '}' )
            {
            // InternalMnc.g:2020:2: ( () otherlv_1= 'InterfaceDescription' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'uses' ( ( ruleQualifiedName ) ) (otherlv_5= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_7= '{' ( ( ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* ) ) ) otherlv_37= '}' )
            // InternalMnc.g:2021:3: () otherlv_1= 'InterfaceDescription' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'uses' ( ( ruleQualifiedName ) ) (otherlv_5= ',' ( ( ruleQualifiedName ) ) )* )? otherlv_7= '{' ( ( ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* ) ) ) otherlv_37= '}'
            {
            // InternalMnc.g:2021:3: ()
            // InternalMnc.g:2022:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getInterfaceDescriptionAccess().getInterfaceDescriptionAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,48,FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getInterfaceDescriptionAccess().getInterfaceDescriptionKeyword_1());
              		
            }
            // InternalMnc.g:2032:3: ( (lv_name_2_0= ruleEString ) )
            // InternalMnc.g:2033:4: (lv_name_2_0= ruleEString )
            {
            // InternalMnc.g:2033:4: (lv_name_2_0= ruleEString )
            // InternalMnc.g:2034:5: lv_name_2_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getNameEStringParserRuleCall_2_0());
              				
            }
            pushFollow(FOLLOW_53);
            lv_name_2_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_2_0,
              						"com.dml.dsl.Dml.EString");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:2051:3: (otherlv_3= 'uses' ( ( ruleQualifiedName ) ) (otherlv_5= ',' ( ( ruleQualifiedName ) ) )* )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==49) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // InternalMnc.g:2052:4: otherlv_3= 'uses' ( ( ruleQualifiedName ) ) (otherlv_5= ',' ( ( ruleQualifiedName ) ) )*
                    {
                    otherlv_3=(Token)match(input,49,FOLLOW_7); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_3, grammarAccess.getInterfaceDescriptionAccess().getUsesKeyword_3_0());
                      			
                    }
                    // InternalMnc.g:2056:4: ( ( ruleQualifiedName ) )
                    // InternalMnc.g:2057:5: ( ruleQualifiedName )
                    {
                    // InternalMnc.g:2057:5: ( ruleQualifiedName )
                    // InternalMnc.g:2058:6: ruleQualifiedName
                    {
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElement(grammarAccess.getInterfaceDescriptionRule());
                      						}
                      					
                    }
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getUsesInterfaceDescriptionCrossReference_3_1_0());
                      					
                    }
                    pushFollow(FOLLOW_54);
                    ruleQualifiedName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:2072:4: (otherlv_5= ',' ( ( ruleQualifiedName ) ) )*
                    loop55:
                    do {
                        int alt55=2;
                        int LA55_0 = input.LA(1);

                        if ( (LA55_0==17) ) {
                            alt55=1;
                        }


                        switch (alt55) {
                    	case 1 :
                    	    // InternalMnc.g:2073:5: otherlv_5= ',' ( ( ruleQualifiedName ) )
                    	    {
                    	    otherlv_5=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_5, grammarAccess.getInterfaceDescriptionAccess().getCommaKeyword_3_2_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:2077:5: ( ( ruleQualifiedName ) )
                    	    // InternalMnc.g:2078:6: ( ruleQualifiedName )
                    	    {
                    	    // InternalMnc.g:2078:6: ( ruleQualifiedName )
                    	    // InternalMnc.g:2079:7: ruleQualifiedName
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElement(grammarAccess.getInterfaceDescriptionRule());
                    	      							}
                    	      						
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getUsesInterfaceDescriptionCrossReference_3_2_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_54);
                    	    ruleQualifiedName();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop55;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_7=(Token)match(input,26,FOLLOW_55); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_7, grammarAccess.getInterfaceDescriptionAccess().getLeftCurlyBracketKeyword_4());
              		
            }
            // InternalMnc.g:2099:3: ( ( ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* ) ) )
            // InternalMnc.g:2100:4: ( ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* ) )
            {
            // InternalMnc.g:2100:4: ( ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* ) )
            // InternalMnc.g:2101:5: ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* )
            {
            getUnorderedGroupHelper().enter(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());
            // InternalMnc.g:2104:5: ( ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )* )
            // InternalMnc.g:2105:6: ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )*
            {
            // InternalMnc.g:2105:6: ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )*
            loop63:
            do {
                int alt63=9;
                alt63 = dfa63.predict(input);
                switch (alt63) {
            	case 1 :
            	    // InternalMnc.g:2106:4: ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) )
            	    {
            	    // InternalMnc.g:2106:4: ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) )
            	    // InternalMnc.g:2107:5: {...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 0) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 0)");
            	    }
            	    // InternalMnc.g:2107:117: ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) )
            	    // InternalMnc.g:2108:6: ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 0);
            	    // InternalMnc.g:2111:9: ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) )
            	    // InternalMnc.g:2111:10: {...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    }
            	    // InternalMnc.g:2111:19: ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) )
            	    // InternalMnc.g:2111:20: ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) )
            	    {
            	    // InternalMnc.g:2111:20: ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) )
            	    // InternalMnc.g:2112:10: ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?)
            	    {
            	    getUnorderedGroupHelper().enter(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0());
            	    // InternalMnc.g:2115:10: ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?)
            	    // InternalMnc.g:2116:11: ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?
            	    {
            	    // InternalMnc.g:2116:11: ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+
            	    int cnt58=0;
            	    loop58:
            	    do {
            	        int alt58=3;
            	        int LA58_0 = input.LA(1);

            	        if ( (LA58_0==93) ) {
            	            int LA58_2 = input.LA(2);

            	            if ( getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0(), 0) ) {
            	                alt58=1;
            	            }


            	        }
            	        else if ( (LA58_0==50) ) {
            	            int LA58_3 = input.LA(2);

            	            if ( getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0(), 1) ) {
            	                alt58=2;
            	            }


            	        }


            	        switch (alt58) {
            	    	case 1 :
            	    	    // InternalMnc.g:2117:9: ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) )
            	    	    {
            	    	    // InternalMnc.g:2117:9: ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) )
            	    	    // InternalMnc.g:2118:10: {...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) )
            	    	    {
            	    	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0(), 0) ) {
            	    	        if (state.backtracking>0) {state.failed=true; return current;}
            	    	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0(), 0)");
            	    	    }
            	    	    // InternalMnc.g:2118:124: ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) )
            	    	    // InternalMnc.g:2119:11: ({...}? => ( (lv_port_10_0= rulePort ) ) )
            	    	    {
            	    	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0(), 0);
            	    	    // InternalMnc.g:2122:14: ({...}? => ( (lv_port_10_0= rulePort ) ) )
            	    	    // InternalMnc.g:2122:15: {...}? => ( (lv_port_10_0= rulePort ) )
            	    	    {
            	    	    if ( !((true)) ) {
            	    	        if (state.backtracking>0) {state.failed=true; return current;}
            	    	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    	    }
            	    	    // InternalMnc.g:2122:24: ( (lv_port_10_0= rulePort ) )
            	    	    // InternalMnc.g:2122:25: (lv_port_10_0= rulePort )
            	    	    {
            	    	    // InternalMnc.g:2122:25: (lv_port_10_0= rulePort )
            	    	    // InternalMnc.g:2123:15: lv_port_10_0= rulePort
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      															newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getPortPortParserRuleCall_5_0_0_0());
            	    	      														
            	    	    }
            	    	    pushFollow(FOLLOW_55);
            	    	    lv_port_10_0=rulePort();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      															if (current==null) {
            	    	      																current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	    	      															}
            	    	      															set(
            	    	      																current,
            	    	      																"port",
            	    	      																lv_port_10_0,
            	    	      																"com.mncml.dsl.Mnc.Port");
            	    	      															afterParserOrEnumRuleCall();
            	    	      														
            	    	    }

            	    	    }


            	    	    }


            	    	    }

            	    	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0());

            	    	    }


            	    	    }


            	    	    }
            	    	    break;
            	    	case 2 :
            	    	    // InternalMnc.g:2145:9: ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) )
            	    	    {
            	    	    // InternalMnc.g:2145:9: ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) )
            	    	    // InternalMnc.g:2146:10: {...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) )
            	    	    {
            	    	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0(), 1) ) {
            	    	        if (state.backtracking>0) {state.failed=true; return current;}
            	    	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0(), 1)");
            	    	    }
            	    	    // InternalMnc.g:2146:124: ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) )
            	    	    // InternalMnc.g:2147:11: ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) )
            	    	    {
            	    	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0(), 1);
            	    	    // InternalMnc.g:2150:14: ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) )
            	    	    // InternalMnc.g:2150:15: {...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' )
            	    	    {
            	    	    if ( !((true)) ) {
            	    	        if (state.backtracking>0) {state.failed=true; return current;}
            	    	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    	    }
            	    	    // InternalMnc.g:2150:24: ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' )
            	    	    // InternalMnc.g:2150:25: (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}'
            	    	    {
            	    	    // InternalMnc.g:2150:25: (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* )
            	    	    // InternalMnc.g:2151:15: otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )*
            	    	    {
            	    	    otherlv_11=(Token)match(input,50,FOLLOW_26); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      															newLeafNode(otherlv_11, grammarAccess.getInterfaceDescriptionAccess().getDataPointsKeyword_5_0_1_0_0());
            	    	      														
            	    	    }
            	    	    otherlv_12=(Token)match(input,26,FOLLOW_56); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      															newLeafNode(otherlv_12, grammarAccess.getInterfaceDescriptionAccess().getLeftCurlyBracketKeyword_5_0_1_0_1());
            	    	      														
            	    	    }
            	    	    // InternalMnc.g:2159:15: ( (lv_dataPoints_13_0= ruleDataPoint ) )*
            	    	    loop57:
            	    	    do {
            	    	        int alt57=2;
            	    	        int LA57_0 = input.LA(1);

            	    	        if ( (LA57_0==RULE_ID||LA57_0==RULE_STRING||LA57_0==19||(LA57_0>=103 && LA57_0<=108)) ) {
            	    	            alt57=1;
            	    	        }


            	    	        switch (alt57) {
            	    	    	case 1 :
            	    	    	    // InternalMnc.g:2160:16: (lv_dataPoints_13_0= ruleDataPoint )
            	    	    	    {
            	    	    	    // InternalMnc.g:2160:16: (lv_dataPoints_13_0= ruleDataPoint )
            	    	    	    // InternalMnc.g:2161:17: lv_dataPoints_13_0= ruleDataPoint
            	    	    	    {
            	    	    	    if ( state.backtracking==0 ) {

            	    	    	      																	newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getDataPointsDataPointParserRuleCall_5_0_1_0_2_0());
            	    	    	      																
            	    	    	    }
            	    	    	    pushFollow(FOLLOW_56);
            	    	    	    lv_dataPoints_13_0=ruleDataPoint();

            	    	    	    state._fsp--;
            	    	    	    if (state.failed) return current;
            	    	    	    if ( state.backtracking==0 ) {

            	    	    	      																	if (current==null) {
            	    	    	      																		current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	    	    	      																	}
            	    	    	      																	add(
            	    	    	      																		current,
            	    	    	      																		"dataPoints",
            	    	    	      																		lv_dataPoints_13_0,
            	    	    	      																		"com.mncml.dsl.Mnc.DataPoint");
            	    	    	      																	afterParserOrEnumRuleCall();
            	    	    	      																
            	    	    	    }

            	    	    	    }


            	    	    	    }
            	    	    	    break;

            	    	    	default :
            	    	    	    break loop57;
            	    	        }
            	    	    } while (true);


            	    	    }

            	    	    otherlv_14=(Token)match(input,30,FOLLOW_55); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      														newLeafNode(otherlv_14, grammarAccess.getInterfaceDescriptionAccess().getRightCurlyBracketKeyword_5_0_1_1());
            	    	      													
            	    	    }

            	    	    }


            	    	    }

            	    	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0());

            	    	    }


            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt58 >= 1 ) break loop58;
            	    	    if (state.backtracking>0) {state.failed=true; return current;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(58, input);
            	                throw eee;
            	        }
            	        cnt58++;
            	    } while (true);

            	    if ( ! getUnorderedGroupHelper().canLeave(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0()) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canLeave(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0())");
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().leave(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5_0());

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalMnc.g:2202:4: ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2202:4: ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) )
            	    // InternalMnc.g:2203:5: {...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 1) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 1)");
            	    }
            	    // InternalMnc.g:2203:117: ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) )
            	    // InternalMnc.g:2204:6: ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 1);
            	    // InternalMnc.g:2207:9: ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) )
            	    // InternalMnc.g:2207:10: {...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    }
            	    // InternalMnc.g:2207:19: (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' )
            	    // InternalMnc.g:2207:20: otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}'
            	    {
            	    otherlv_15=(Token)match(input,33,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_15, grammarAccess.getInterfaceDescriptionAccess().getAlarmsKeyword_5_1_0());
            	      								
            	    }
            	    otherlv_16=(Token)match(input,26,FOLLOW_57); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_16, grammarAccess.getInterfaceDescriptionAccess().getLeftCurlyBracketKeyword_5_1_1());
            	      								
            	    }
            	    // InternalMnc.g:2215:9: ( (lv_alarms_17_0= ruleAlarm ) )*
            	    loop59:
            	    do {
            	        int alt59=2;
            	        int LA59_0 = input.LA(1);

            	        if ( (LA59_0==RULE_ID||LA59_0==RULE_STRING||LA59_0==19) ) {
            	            alt59=1;
            	        }


            	        switch (alt59) {
            	    	case 1 :
            	    	    // InternalMnc.g:2216:10: (lv_alarms_17_0= ruleAlarm )
            	    	    {
            	    	    // InternalMnc.g:2216:10: (lv_alarms_17_0= ruleAlarm )
            	    	    // InternalMnc.g:2217:11: lv_alarms_17_0= ruleAlarm
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      											newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getAlarmsAlarmParserRuleCall_5_1_2_0());
            	    	      										
            	    	    }
            	    	    pushFollow(FOLLOW_57);
            	    	    lv_alarms_17_0=ruleAlarm();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											if (current==null) {
            	    	      												current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	    	      											}
            	    	      											add(
            	    	      												current,
            	    	      												"alarms",
            	    	      												lv_alarms_17_0,
            	    	      												"com.mncml.dsl.Mnc.Alarm");
            	    	      											afterParserOrEnumRuleCall();
            	    	      										
            	    	    }

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop59;
            	        }
            	    } while (true);

            	    otherlv_18=(Token)match(input,30,FOLLOW_55); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_18, grammarAccess.getInterfaceDescriptionAccess().getRightCurlyBracketKeyword_5_1_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalMnc.g:2244:4: ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2244:4: ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) )
            	    // InternalMnc.g:2245:5: {...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 2) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 2)");
            	    }
            	    // InternalMnc.g:2245:117: ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) )
            	    // InternalMnc.g:2246:6: ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 2);
            	    // InternalMnc.g:2249:9: ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) )
            	    // InternalMnc.g:2249:10: {...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    }
            	    // InternalMnc.g:2249:19: (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' )
            	    // InternalMnc.g:2249:20: otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}'
            	    {
            	    otherlv_19=(Token)match(input,35,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_19, grammarAccess.getInterfaceDescriptionAccess().getCommandsKeyword_5_2_0());
            	      								
            	    }
            	    otherlv_20=(Token)match(input,26,FOLLOW_58); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_20, grammarAccess.getInterfaceDescriptionAccess().getLeftCurlyBracketKeyword_5_2_1());
            	      								
            	    }
            	    // InternalMnc.g:2257:9: ( (lv_commands_21_0= ruleCommand ) )*
            	    loop60:
            	    do {
            	        int alt60=2;
            	        int LA60_0 = input.LA(1);

            	        if ( (LA60_0==RULE_ID||LA60_0==RULE_STRING||LA60_0==15) ) {
            	            alt60=1;
            	        }


            	        switch (alt60) {
            	    	case 1 :
            	    	    // InternalMnc.g:2258:10: (lv_commands_21_0= ruleCommand )
            	    	    {
            	    	    // InternalMnc.g:2258:10: (lv_commands_21_0= ruleCommand )
            	    	    // InternalMnc.g:2259:11: lv_commands_21_0= ruleCommand
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      											newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getCommandsCommandParserRuleCall_5_2_2_0());
            	    	      										
            	    	    }
            	    	    pushFollow(FOLLOW_58);
            	    	    lv_commands_21_0=ruleCommand();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											if (current==null) {
            	    	      												current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	    	      											}
            	    	      											add(
            	    	      												current,
            	    	      												"commands",
            	    	      												lv_commands_21_0,
            	    	      												"com.mncml.dsl.Mnc.Command");
            	    	      											afterParserOrEnumRuleCall();
            	    	      										
            	    	    }

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop60;
            	        }
            	    } while (true);

            	    otherlv_22=(Token)match(input,30,FOLLOW_55); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_22, grammarAccess.getInterfaceDescriptionAccess().getRightCurlyBracketKeyword_5_2_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // InternalMnc.g:2286:4: ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2286:4: ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) )
            	    // InternalMnc.g:2287:5: {...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 3) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 3)");
            	    }
            	    // InternalMnc.g:2287:117: ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) )
            	    // InternalMnc.g:2288:6: ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 3);
            	    // InternalMnc.g:2291:9: ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) )
            	    // InternalMnc.g:2291:10: {...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    }
            	    // InternalMnc.g:2291:19: (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' )
            	    // InternalMnc.g:2291:20: otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}'
            	    {
            	    otherlv_23=(Token)match(input,37,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_23, grammarAccess.getInterfaceDescriptionAccess().getEventsKeyword_5_3_0());
            	      								
            	    }
            	    otherlv_24=(Token)match(input,26,FOLLOW_57); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_24, grammarAccess.getInterfaceDescriptionAccess().getLeftCurlyBracketKeyword_5_3_1());
            	      								
            	    }
            	    // InternalMnc.g:2299:9: ( (lv_events_25_0= ruleEvent ) )*
            	    loop61:
            	    do {
            	        int alt61=2;
            	        int LA61_0 = input.LA(1);

            	        if ( (LA61_0==RULE_ID||LA61_0==RULE_STRING||LA61_0==19) ) {
            	            alt61=1;
            	        }


            	        switch (alt61) {
            	    	case 1 :
            	    	    // InternalMnc.g:2300:10: (lv_events_25_0= ruleEvent )
            	    	    {
            	    	    // InternalMnc.g:2300:10: (lv_events_25_0= ruleEvent )
            	    	    // InternalMnc.g:2301:11: lv_events_25_0= ruleEvent
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      											newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getEventsEventParserRuleCall_5_3_2_0());
            	    	      										
            	    	    }
            	    	    pushFollow(FOLLOW_57);
            	    	    lv_events_25_0=ruleEvent();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											if (current==null) {
            	    	      												current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	    	      											}
            	    	      											add(
            	    	      												current,
            	    	      												"events",
            	    	      												lv_events_25_0,
            	    	      												"com.mncml.dsl.Mnc.Event");
            	    	      											afterParserOrEnumRuleCall();
            	    	      										
            	    	    }

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop61;
            	        }
            	    } while (true);

            	    otherlv_26=(Token)match(input,30,FOLLOW_55); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_26, grammarAccess.getInterfaceDescriptionAccess().getRightCurlyBracketKeyword_5_3_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 5 :
            	    // InternalMnc.g:2328:4: ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2328:4: ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) )
            	    // InternalMnc.g:2329:5: {...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 4) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 4)");
            	    }
            	    // InternalMnc.g:2329:117: ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) )
            	    // InternalMnc.g:2330:6: ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 4);
            	    // InternalMnc.g:2333:9: ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) )
            	    // InternalMnc.g:2333:10: {...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    }
            	    // InternalMnc.g:2333:19: (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' )
            	    // InternalMnc.g:2333:20: otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}'
            	    {
            	    otherlv_27=(Token)match(input,51,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_27, grammarAccess.getInterfaceDescriptionAccess().getResponsesKeyword_5_4_0());
            	      								
            	    }
            	    otherlv_28=(Token)match(input,26,FOLLOW_59); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_28, grammarAccess.getInterfaceDescriptionAccess().getLeftCurlyBracketKeyword_5_4_1());
            	      								
            	    }
            	    // InternalMnc.g:2341:9: ( (lv_responses_29_0= ruleResponse ) )*
            	    loop62:
            	    do {
            	        int alt62=2;
            	        int LA62_0 = input.LA(1);

            	        if ( (LA62_0==RULE_ID||LA62_0==RULE_STRING) ) {
            	            alt62=1;
            	        }


            	        switch (alt62) {
            	    	case 1 :
            	    	    // InternalMnc.g:2342:10: (lv_responses_29_0= ruleResponse )
            	    	    {
            	    	    // InternalMnc.g:2342:10: (lv_responses_29_0= ruleResponse )
            	    	    // InternalMnc.g:2343:11: lv_responses_29_0= ruleResponse
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      											newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getResponsesResponseParserRuleCall_5_4_2_0());
            	    	      										
            	    	    }
            	    	    pushFollow(FOLLOW_59);
            	    	    lv_responses_29_0=ruleResponse();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											if (current==null) {
            	    	      												current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	    	      											}
            	    	      											add(
            	    	      												current,
            	    	      												"responses",
            	    	      												lv_responses_29_0,
            	    	      												"com.mncml.dsl.Mnc.Response");
            	    	      											afterParserOrEnumRuleCall();
            	    	      										
            	    	    }

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop62;
            	        }
            	    } while (true);

            	    otherlv_30=(Token)match(input,30,FOLLOW_55); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_30, grammarAccess.getInterfaceDescriptionAccess().getRightCurlyBracketKeyword_5_4_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 6 :
            	    // InternalMnc.g:2370:4: ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2370:4: ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) )
            	    // InternalMnc.g:2371:5: {...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 5) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 5)");
            	    }
            	    // InternalMnc.g:2371:117: ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) )
            	    // InternalMnc.g:2372:6: ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 5);
            	    // InternalMnc.g:2375:9: ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) )
            	    // InternalMnc.g:2375:10: {...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    }
            	    // InternalMnc.g:2375:19: (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' )
            	    // InternalMnc.g:2375:20: otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}'
            	    {
            	    otherlv_31=(Token)match(input,52,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_31, grammarAccess.getInterfaceDescriptionAccess().getOperatingStatesKeyword_5_5_0());
            	      								
            	    }
            	    otherlv_32=(Token)match(input,26,FOLLOW_60); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_32, grammarAccess.getInterfaceDescriptionAccess().getLeftCurlyBracketKeyword_5_5_1());
            	      								
            	    }
            	    // InternalMnc.g:2383:9: ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) )
            	    // InternalMnc.g:2384:10: (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility )
            	    {
            	    // InternalMnc.g:2384:10: (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility )
            	    // InternalMnc.g:2385:11: lv_operatingStatesUtility_33_0= ruleOperatingStateUtility
            	    {
            	    if ( state.backtracking==0 ) {

            	      											newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getOperatingStatesUtilityOperatingStateUtilityParserRuleCall_5_5_2_0());
            	      										
            	    }
            	    pushFollow(FOLLOW_45);
            	    lv_operatingStatesUtility_33_0=ruleOperatingStateUtility();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      											if (current==null) {
            	      												current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	      											}
            	      											set(
            	      												current,
            	      												"operatingStatesUtility",
            	      												lv_operatingStatesUtility_33_0,
            	      												"com.mncml.dsl.Mnc.OperatingStateUtility");
            	      											afterParserOrEnumRuleCall();
            	      										
            	    }

            	    }


            	    }

            	    otherlv_34=(Token)match(input,30,FOLLOW_55); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_34, grammarAccess.getInterfaceDescriptionAccess().getRightCurlyBracketKeyword_5_5_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 7 :
            	    // InternalMnc.g:2412:4: ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) )
            	    {
            	    // InternalMnc.g:2412:4: ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) )
            	    // InternalMnc.g:2413:5: {...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 6) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 6)");
            	    }
            	    // InternalMnc.g:2413:117: ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) )
            	    // InternalMnc.g:2414:6: ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 6);
            	    // InternalMnc.g:2417:9: ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) )
            	    // InternalMnc.g:2417:10: {...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    }
            	    // InternalMnc.g:2417:19: ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) )
            	    // InternalMnc.g:2417:20: (lv_subscribedItems_35_0= ruleSubscribableItemList )
            	    {
            	    // InternalMnc.g:2417:20: (lv_subscribedItems_35_0= ruleSubscribableItemList )
            	    // InternalMnc.g:2418:10: lv_subscribedItems_35_0= ruleSubscribableItemList
            	    {
            	    if ( state.backtracking==0 ) {

            	      										newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getSubscribedItemsSubscribableItemListParserRuleCall_5_6_0());
            	      									
            	    }
            	    pushFollow(FOLLOW_55);
            	    lv_subscribedItems_35_0=ruleSubscribableItemList();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      										if (current==null) {
            	      											current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	      										}
            	      										set(
            	      											current,
            	      											"subscribedItems",
            	      											lv_subscribedItems_35_0,
            	      											"com.mncml.dsl.Mnc.SubscribableItemList");
            	      										afterParserOrEnumRuleCall();
            	      									
            	    }

            	    }


            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 8 :
            	    // InternalMnc.g:2440:4: ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) )
            	    {
            	    // InternalMnc.g:2440:4: ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) )
            	    // InternalMnc.g:2441:5: {...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 7) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 7)");
            	    }
            	    // InternalMnc.g:2441:117: ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) )
            	    // InternalMnc.g:2442:6: ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 7);
            	    // InternalMnc.g:2445:9: ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) )
            	    // InternalMnc.g:2445:10: {...}? => ( (lv_ipaddress_36_0= ruleAddress ) )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleInterfaceDescription", "true");
            	    }
            	    // InternalMnc.g:2445:19: ( (lv_ipaddress_36_0= ruleAddress ) )
            	    // InternalMnc.g:2445:20: (lv_ipaddress_36_0= ruleAddress )
            	    {
            	    // InternalMnc.g:2445:20: (lv_ipaddress_36_0= ruleAddress )
            	    // InternalMnc.g:2446:10: lv_ipaddress_36_0= ruleAddress
            	    {
            	    if ( state.backtracking==0 ) {

            	      										newCompositeNode(grammarAccess.getInterfaceDescriptionAccess().getIpaddressAddressParserRuleCall_5_7_0());
            	      									
            	    }
            	    pushFollow(FOLLOW_55);
            	    lv_ipaddress_36_0=ruleAddress();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      										if (current==null) {
            	      											current = createModelElementForParent(grammarAccess.getInterfaceDescriptionRule());
            	      										}
            	      										set(
            	      											current,
            	      											"ipaddress",
            	      											lv_ipaddress_36_0,
            	      											"com.mncml.dsl.Mnc.Address");
            	      										afterParserOrEnumRuleCall();
            	      									
            	    }

            	    }


            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop63;
                }
            } while (true);


            }


            }

            getUnorderedGroupHelper().leave(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5());

            }

            otherlv_37=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_37, grammarAccess.getInterfaceDescriptionAccess().getRightCurlyBracketKeyword_6());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleInterfaceDescription"


    // $ANTLR start "entryRuleControlNode"
    // InternalMnc.g:2483:1: entryRuleControlNode returns [EObject current=null] : iv_ruleControlNode= ruleControlNode EOF ;
    public final EObject entryRuleControlNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleControlNode = null;


        try {
            // InternalMnc.g:2483:52: (iv_ruleControlNode= ruleControlNode EOF )
            // InternalMnc.g:2484:2: iv_ruleControlNode= ruleControlNode EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getControlNodeRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleControlNode=ruleControlNode();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleControlNode; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleControlNode"


    // $ANTLR start "ruleControlNode"
    // InternalMnc.g:2490:1: ruleControlNode returns [EObject current=null] : ( () otherlv_1= 'ControlNode' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'implements' otherlv_4= 'interface' ( ( ruleQualifiedName ) ) ) otherlv_6= '{' ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* ) ) ) otherlv_30= '}' ) ;
    public final EObject ruleControlNode() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_18=null;
        Token otherlv_19=null;
        Token otherlv_21=null;
        Token otherlv_22=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token otherlv_26=null;
        Token otherlv_27=null;
        Token otherlv_29=null;
        Token otherlv_30=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_commandResponseBlocks_16_0 = null;

        EObject lv_eventBlocks_20_0 = null;

        EObject lv_alarmBlocks_24_0 = null;

        EObject lv_dataPointBlocks_28_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:2496:2: ( ( () otherlv_1= 'ControlNode' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'implements' otherlv_4= 'interface' ( ( ruleQualifiedName ) ) ) otherlv_6= '{' ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* ) ) ) otherlv_30= '}' ) )
            // InternalMnc.g:2497:2: ( () otherlv_1= 'ControlNode' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'implements' otherlv_4= 'interface' ( ( ruleQualifiedName ) ) ) otherlv_6= '{' ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* ) ) ) otherlv_30= '}' )
            {
            // InternalMnc.g:2497:2: ( () otherlv_1= 'ControlNode' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'implements' otherlv_4= 'interface' ( ( ruleQualifiedName ) ) ) otherlv_6= '{' ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* ) ) ) otherlv_30= '}' )
            // InternalMnc.g:2498:3: () otherlv_1= 'ControlNode' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= 'implements' otherlv_4= 'interface' ( ( ruleQualifiedName ) ) ) otherlv_6= '{' ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* ) ) ) otherlv_30= '}'
            {
            // InternalMnc.g:2498:3: ()
            // InternalMnc.g:2499:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getControlNodeAccess().getControlNodeAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,53,FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getControlNodeAccess().getControlNodeKeyword_1());
              		
            }
            // InternalMnc.g:2509:3: ( (lv_name_2_0= ruleEString ) )
            // InternalMnc.g:2510:4: (lv_name_2_0= ruleEString )
            {
            // InternalMnc.g:2510:4: (lv_name_2_0= ruleEString )
            // InternalMnc.g:2511:5: lv_name_2_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getControlNodeAccess().getNameEStringParserRuleCall_2_0());
              				
            }
            pushFollow(FOLLOW_61);
            lv_name_2_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getControlNodeRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_2_0,
              						"com.dml.dsl.Dml.EString");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:2528:3: (otherlv_3= 'implements' otherlv_4= 'interface' ( ( ruleQualifiedName ) ) )
            // InternalMnc.g:2529:4: otherlv_3= 'implements' otherlv_4= 'interface' ( ( ruleQualifiedName ) )
            {
            otherlv_3=(Token)match(input,54,FOLLOW_62); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              				newLeafNode(otherlv_3, grammarAccess.getControlNodeAccess().getImplementsKeyword_3_0());
              			
            }
            otherlv_4=(Token)match(input,55,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              				newLeafNode(otherlv_4, grammarAccess.getControlNodeAccess().getInterfaceKeyword_3_1());
              			
            }
            // InternalMnc.g:2537:4: ( ( ruleQualifiedName ) )
            // InternalMnc.g:2538:5: ( ruleQualifiedName )
            {
            // InternalMnc.g:2538:5: ( ruleQualifiedName )
            // InternalMnc.g:2539:6: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              						if (current==null) {
              							current = createModelElement(grammarAccess.getControlNodeRule());
              						}
              					
            }
            if ( state.backtracking==0 ) {

              						newCompositeNode(grammarAccess.getControlNodeAccess().getInterfaceDescriptionInterfaceDescriptionCrossReference_3_2_0());
              					
            }
            pushFollow(FOLLOW_26);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              						afterParserOrEnumRuleCall();
              					
            }

            }


            }


            }

            otherlv_6=(Token)match(input,26,FOLLOW_63); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_6, grammarAccess.getControlNodeAccess().getLeftCurlyBracketKeyword_4());
              		
            }
            // InternalMnc.g:2558:3: ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* ) ) )
            // InternalMnc.g:2559:4: ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* ) )
            {
            // InternalMnc.g:2559:4: ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* ) )
            // InternalMnc.g:2560:5: ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* )
            {
            getUnorderedGroupHelper().enter(grammarAccess.getControlNodeAccess().getUnorderedGroup_5());
            // InternalMnc.g:2563:5: ( ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )* )
            // InternalMnc.g:2564:6: ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )*
            {
            // InternalMnc.g:2564:6: ( ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) ) )*
            loop69:
            do {
                int alt69=6;
                int LA69_0 = input.LA(1);

                if ( LA69_0 == 56 && getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 0) ) {
                    alt69=1;
                }
                else if ( LA69_0 == 57 && getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 1) ) {
                    alt69=2;
                }
                else if ( LA69_0 == 58 && getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 2) ) {
                    alt69=3;
                }
                else if ( LA69_0 == 59 && getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 3) ) {
                    alt69=4;
                }
                else if ( LA69_0 == 60 && getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 4) ) {
                    alt69=5;
                }


                switch (alt69) {
            	case 1 :
            	    // InternalMnc.g:2565:4: ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) )
            	    {
            	    // InternalMnc.g:2565:4: ({...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) ) )
            	    // InternalMnc.g:2566:5: {...}? => ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 0) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 0)");
            	    }
            	    // InternalMnc.g:2566:108: ( ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) ) )
            	    // InternalMnc.g:2567:6: ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 0);
            	    // InternalMnc.g:2570:9: ({...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' ) )
            	    // InternalMnc.g:2570:10: {...}? => (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "true");
            	    }
            	    // InternalMnc.g:2570:19: (otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')' )
            	    // InternalMnc.g:2570:20: otherlv_8= 'childNodes' otherlv_9= '(' ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* ) otherlv_13= ')'
            	    {
            	    otherlv_8=(Token)match(input,56,FOLLOW_46); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_8, grammarAccess.getControlNodeAccess().getChildNodesKeyword_5_0_0());
            	      								
            	    }
            	    otherlv_9=(Token)match(input,44,FOLLOW_7); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_9, grammarAccess.getControlNodeAccess().getLeftParenthesisKeyword_5_0_1());
            	      								
            	    }
            	    // InternalMnc.g:2578:9: ( ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )* )
            	    // InternalMnc.g:2579:10: ( ( ruleQualifiedName ) ) (otherlv_11= ',' ( ( ruleQualifiedName ) ) )*
            	    {
            	    // InternalMnc.g:2579:10: ( ( ruleQualifiedName ) )
            	    // InternalMnc.g:2580:11: ( ruleQualifiedName )
            	    {
            	    // InternalMnc.g:2580:11: ( ruleQualifiedName )
            	    // InternalMnc.g:2581:12: ruleQualifiedName
            	    {
            	    if ( state.backtracking==0 ) {

            	      												if (current==null) {
            	      													current = createModelElement(grammarAccess.getControlNodeRule());
            	      												}
            	      											
            	    }
            	    if ( state.backtracking==0 ) {

            	      												newCompositeNode(grammarAccess.getControlNodeAccess().getChildNodesControlNodeCrossReference_5_0_2_0_0());
            	      											
            	    }
            	    pushFollow(FOLLOW_64);
            	    ruleQualifiedName();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      												afterParserOrEnumRuleCall();
            	      											
            	    }

            	    }


            	    }

            	    // InternalMnc.g:2595:10: (otherlv_11= ',' ( ( ruleQualifiedName ) ) )*
            	    loop64:
            	    do {
            	        int alt64=2;
            	        int LA64_0 = input.LA(1);

            	        if ( (LA64_0==17) ) {
            	            alt64=1;
            	        }


            	        switch (alt64) {
            	    	case 1 :
            	    	    // InternalMnc.g:2596:11: otherlv_11= ',' ( ( ruleQualifiedName ) )
            	    	    {
            	    	    otherlv_11=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											newLeafNode(otherlv_11, grammarAccess.getControlNodeAccess().getCommaKeyword_5_0_2_1_0());
            	    	      										
            	    	    }
            	    	    // InternalMnc.g:2600:11: ( ( ruleQualifiedName ) )
            	    	    // InternalMnc.g:2601:12: ( ruleQualifiedName )
            	    	    {
            	    	    // InternalMnc.g:2601:12: ( ruleQualifiedName )
            	    	    // InternalMnc.g:2602:13: ruleQualifiedName
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      													if (current==null) {
            	    	      														current = createModelElement(grammarAccess.getControlNodeRule());
            	    	      													}
            	    	      												
            	    	    }
            	    	    if ( state.backtracking==0 ) {

            	    	      													newCompositeNode(grammarAccess.getControlNodeAccess().getChildNodesControlNodeCrossReference_5_0_2_1_1_0());
            	    	      												
            	    	    }
            	    	    pushFollow(FOLLOW_64);
            	    	    ruleQualifiedName();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      													afterParserOrEnumRuleCall();
            	    	      												
            	    	    }

            	    	    }


            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop64;
            	        }
            	    } while (true);


            	    }

            	    otherlv_13=(Token)match(input,45,FOLLOW_63); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_13, grammarAccess.getControlNodeAccess().getRightParenthesisKeyword_5_0_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getControlNodeAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalMnc.g:2628:4: ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2628:4: ({...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) ) )
            	    // InternalMnc.g:2629:5: {...}? => ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 1) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 1)");
            	    }
            	    // InternalMnc.g:2629:108: ( ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) ) )
            	    // InternalMnc.g:2630:6: ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 1);
            	    // InternalMnc.g:2633:9: ({...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' ) )
            	    // InternalMnc.g:2633:10: {...}? => (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "true");
            	    }
            	    // InternalMnc.g:2633:19: (otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}' )
            	    // InternalMnc.g:2633:20: otherlv_14= 'CommandResponseBlock' otherlv_15= '{' ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )* otherlv_17= '}'
            	    {
            	    otherlv_14=(Token)match(input,57,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_14, grammarAccess.getControlNodeAccess().getCommandResponseBlockKeyword_5_1_0());
            	      								
            	    }
            	    otherlv_15=(Token)match(input,26,FOLLOW_65); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_15, grammarAccess.getControlNodeAccess().getLeftCurlyBracketKeyword_5_1_1());
            	      								
            	    }
            	    // InternalMnc.g:2641:9: ( (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock ) )*
            	    loop65:
            	    do {
            	        int alt65=2;
            	        int LA65_0 = input.LA(1);

            	        if ( (LA65_0==61) ) {
            	            alt65=1;
            	        }


            	        switch (alt65) {
            	    	case 1 :
            	    	    // InternalMnc.g:2642:10: (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock )
            	    	    {
            	    	    // InternalMnc.g:2642:10: (lv_commandResponseBlocks_16_0= ruleCommandResponseBlock )
            	    	    // InternalMnc.g:2643:11: lv_commandResponseBlocks_16_0= ruleCommandResponseBlock
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      											newCompositeNode(grammarAccess.getControlNodeAccess().getCommandResponseBlocksCommandResponseBlockParserRuleCall_5_1_2_0());
            	    	      										
            	    	    }
            	    	    pushFollow(FOLLOW_65);
            	    	    lv_commandResponseBlocks_16_0=ruleCommandResponseBlock();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											if (current==null) {
            	    	      												current = createModelElementForParent(grammarAccess.getControlNodeRule());
            	    	      											}
            	    	      											add(
            	    	      												current,
            	    	      												"commandResponseBlocks",
            	    	      												lv_commandResponseBlocks_16_0,
            	    	      												"com.mncml.dsl.Mnc.CommandResponseBlock");
            	    	      											afterParserOrEnumRuleCall();
            	    	      										
            	    	    }

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop65;
            	        }
            	    } while (true);

            	    otherlv_17=(Token)match(input,30,FOLLOW_63); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_17, grammarAccess.getControlNodeAccess().getRightCurlyBracketKeyword_5_1_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getControlNodeAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalMnc.g:2670:4: ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2670:4: ({...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) ) )
            	    // InternalMnc.g:2671:5: {...}? => ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 2) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 2)");
            	    }
            	    // InternalMnc.g:2671:108: ( ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) ) )
            	    // InternalMnc.g:2672:6: ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 2);
            	    // InternalMnc.g:2675:9: ({...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' ) )
            	    // InternalMnc.g:2675:10: {...}? => (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "true");
            	    }
            	    // InternalMnc.g:2675:19: (otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}' )
            	    // InternalMnc.g:2675:20: otherlv_18= 'EventBlock' otherlv_19= '{' ( (lv_eventBlocks_20_0= ruleEventBlock ) )* otherlv_21= '}'
            	    {
            	    otherlv_18=(Token)match(input,58,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_18, grammarAccess.getControlNodeAccess().getEventBlockKeyword_5_2_0());
            	      								
            	    }
            	    otherlv_19=(Token)match(input,26,FOLLOW_66); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_19, grammarAccess.getControlNodeAccess().getLeftCurlyBracketKeyword_5_2_1());
            	      								
            	    }
            	    // InternalMnc.g:2683:9: ( (lv_eventBlocks_20_0= ruleEventBlock ) )*
            	    loop66:
            	    do {
            	        int alt66=2;
            	        int LA66_0 = input.LA(1);

            	        if ( (LA66_0==89) ) {
            	            alt66=1;
            	        }


            	        switch (alt66) {
            	    	case 1 :
            	    	    // InternalMnc.g:2684:10: (lv_eventBlocks_20_0= ruleEventBlock )
            	    	    {
            	    	    // InternalMnc.g:2684:10: (lv_eventBlocks_20_0= ruleEventBlock )
            	    	    // InternalMnc.g:2685:11: lv_eventBlocks_20_0= ruleEventBlock
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      											newCompositeNode(grammarAccess.getControlNodeAccess().getEventBlocksEventBlockParserRuleCall_5_2_2_0());
            	    	      										
            	    	    }
            	    	    pushFollow(FOLLOW_66);
            	    	    lv_eventBlocks_20_0=ruleEventBlock();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											if (current==null) {
            	    	      												current = createModelElementForParent(grammarAccess.getControlNodeRule());
            	    	      											}
            	    	      											add(
            	    	      												current,
            	    	      												"eventBlocks",
            	    	      												lv_eventBlocks_20_0,
            	    	      												"com.mncml.dsl.Mnc.EventBlock");
            	    	      											afterParserOrEnumRuleCall();
            	    	      										
            	    	    }

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop66;
            	        }
            	    } while (true);

            	    otherlv_21=(Token)match(input,30,FOLLOW_63); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_21, grammarAccess.getControlNodeAccess().getRightCurlyBracketKeyword_5_2_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getControlNodeAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // InternalMnc.g:2712:4: ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2712:4: ({...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) ) )
            	    // InternalMnc.g:2713:5: {...}? => ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 3) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 3)");
            	    }
            	    // InternalMnc.g:2713:108: ( ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) ) )
            	    // InternalMnc.g:2714:6: ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 3);
            	    // InternalMnc.g:2717:9: ({...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' ) )
            	    // InternalMnc.g:2717:10: {...}? => (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "true");
            	    }
            	    // InternalMnc.g:2717:19: (otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}' )
            	    // InternalMnc.g:2717:20: otherlv_22= 'AlarmBlock' otherlv_23= '{' ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )* otherlv_25= '}'
            	    {
            	    otherlv_22=(Token)match(input,59,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_22, grammarAccess.getControlNodeAccess().getAlarmBlockKeyword_5_3_0());
            	      								
            	    }
            	    otherlv_23=(Token)match(input,26,FOLLOW_67); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_23, grammarAccess.getControlNodeAccess().getLeftCurlyBracketKeyword_5_3_1());
            	      								
            	    }
            	    // InternalMnc.g:2725:9: ( (lv_alarmBlocks_24_0= ruleAlarmBlock ) )*
            	    loop67:
            	    do {
            	        int alt67=2;
            	        int LA67_0 = input.LA(1);

            	        if ( (LA67_0==90) ) {
            	            alt67=1;
            	        }


            	        switch (alt67) {
            	    	case 1 :
            	    	    // InternalMnc.g:2726:10: (lv_alarmBlocks_24_0= ruleAlarmBlock )
            	    	    {
            	    	    // InternalMnc.g:2726:10: (lv_alarmBlocks_24_0= ruleAlarmBlock )
            	    	    // InternalMnc.g:2727:11: lv_alarmBlocks_24_0= ruleAlarmBlock
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      											newCompositeNode(grammarAccess.getControlNodeAccess().getAlarmBlocksAlarmBlockParserRuleCall_5_3_2_0());
            	    	      										
            	    	    }
            	    	    pushFollow(FOLLOW_67);
            	    	    lv_alarmBlocks_24_0=ruleAlarmBlock();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											if (current==null) {
            	    	      												current = createModelElementForParent(grammarAccess.getControlNodeRule());
            	    	      											}
            	    	      											add(
            	    	      												current,
            	    	      												"alarmBlocks",
            	    	      												lv_alarmBlocks_24_0,
            	    	      												"com.mncml.dsl.Mnc.AlarmBlock");
            	    	      											afterParserOrEnumRuleCall();
            	    	      										
            	    	    }

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop67;
            	        }
            	    } while (true);

            	    otherlv_25=(Token)match(input,30,FOLLOW_63); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_25, grammarAccess.getControlNodeAccess().getRightCurlyBracketKeyword_5_3_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getControlNodeAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;
            	case 5 :
            	    // InternalMnc.g:2754:4: ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:2754:4: ({...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) ) )
            	    // InternalMnc.g:2755:5: {...}? => ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 4) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "getUnorderedGroupHelper().canSelect(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 4)");
            	    }
            	    // InternalMnc.g:2755:108: ( ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) ) )
            	    // InternalMnc.g:2756:6: ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getControlNodeAccess().getUnorderedGroup_5(), 4);
            	    // InternalMnc.g:2759:9: ({...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' ) )
            	    // InternalMnc.g:2759:10: {...}? => (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleControlNode", "true");
            	    }
            	    // InternalMnc.g:2759:19: (otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}' )
            	    // InternalMnc.g:2759:20: otherlv_26= 'DataPointBlock' otherlv_27= '{' ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )* otherlv_29= '}'
            	    {
            	    otherlv_26=(Token)match(input,60,FOLLOW_26); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_26, grammarAccess.getControlNodeAccess().getDataPointBlockKeyword_5_4_0());
            	      								
            	    }
            	    otherlv_27=(Token)match(input,26,FOLLOW_68); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_27, grammarAccess.getControlNodeAccess().getLeftCurlyBracketKeyword_5_4_1());
            	      								
            	    }
            	    // InternalMnc.g:2767:9: ( (lv_dataPointBlocks_28_0= ruleDataPointBlock ) )*
            	    loop68:
            	    do {
            	        int alt68=2;
            	        int LA68_0 = input.LA(1);

            	        if ( (LA68_0==91) ) {
            	            alt68=1;
            	        }


            	        switch (alt68) {
            	    	case 1 :
            	    	    // InternalMnc.g:2768:10: (lv_dataPointBlocks_28_0= ruleDataPointBlock )
            	    	    {
            	    	    // InternalMnc.g:2768:10: (lv_dataPointBlocks_28_0= ruleDataPointBlock )
            	    	    // InternalMnc.g:2769:11: lv_dataPointBlocks_28_0= ruleDataPointBlock
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      											newCompositeNode(grammarAccess.getControlNodeAccess().getDataPointBlocksDataPointBlockParserRuleCall_5_4_2_0());
            	    	      										
            	    	    }
            	    	    pushFollow(FOLLOW_68);
            	    	    lv_dataPointBlocks_28_0=ruleDataPointBlock();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      											if (current==null) {
            	    	      												current = createModelElementForParent(grammarAccess.getControlNodeRule());
            	    	      											}
            	    	      											add(
            	    	      												current,
            	    	      												"dataPointBlocks",
            	    	      												lv_dataPointBlocks_28_0,
            	    	      												"com.mncml.dsl.Mnc.DataPointBlock");
            	    	      											afterParserOrEnumRuleCall();
            	    	      										
            	    	    }

            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop68;
            	        }
            	    } while (true);

            	    otherlv_29=(Token)match(input,30,FOLLOW_63); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_29, grammarAccess.getControlNodeAccess().getRightCurlyBracketKeyword_5_4_3());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getControlNodeAccess().getUnorderedGroup_5());

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop69;
                }
            } while (true);


            }


            }

            getUnorderedGroupHelper().leave(grammarAccess.getControlNodeAccess().getUnorderedGroup_5());

            }

            otherlv_30=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_30, grammarAccess.getControlNodeAccess().getRightCurlyBracketKeyword_6());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleControlNode"


    // $ANTLR start "entryRuleCommandResponseBlock"
    // InternalMnc.g:2811:1: entryRuleCommandResponseBlock returns [EObject current=null] : iv_ruleCommandResponseBlock= ruleCommandResponseBlock EOF ;
    public final EObject entryRuleCommandResponseBlock() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCommandResponseBlock = null;


        try {
            // InternalMnc.g:2811:61: (iv_ruleCommandResponseBlock= ruleCommandResponseBlock EOF )
            // InternalMnc.g:2812:2: iv_ruleCommandResponseBlock= ruleCommandResponseBlock EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getCommandResponseBlockRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleCommandResponseBlock=ruleCommandResponseBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleCommandResponseBlock; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleCommandResponseBlock"


    // $ANTLR start "ruleCommandResponseBlock"
    // InternalMnc.g:2818:1: ruleCommandResponseBlock returns [EObject current=null] : ( () otherlv_1= 'Command' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'Generate' otherlv_7= 'Response' otherlv_8= '{' ( (lv_responseBlock_9_0= ruleResponseBlock ) )* otherlv_10= '}' )? otherlv_11= '}' ) ;
    public final EObject ruleCommandResponseBlock() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        EObject lv_action_4_0 = null;

        EObject lv_validationRules_5_0 = null;

        EObject lv_responseBlock_9_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:2824:2: ( ( () otherlv_1= 'Command' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'Generate' otherlv_7= 'Response' otherlv_8= '{' ( (lv_responseBlock_9_0= ruleResponseBlock ) )* otherlv_10= '}' )? otherlv_11= '}' ) )
            // InternalMnc.g:2825:2: ( () otherlv_1= 'Command' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'Generate' otherlv_7= 'Response' otherlv_8= '{' ( (lv_responseBlock_9_0= ruleResponseBlock ) )* otherlv_10= '}' )? otherlv_11= '}' )
            {
            // InternalMnc.g:2825:2: ( () otherlv_1= 'Command' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'Generate' otherlv_7= 'Response' otherlv_8= '{' ( (lv_responseBlock_9_0= ruleResponseBlock ) )* otherlv_10= '}' )? otherlv_11= '}' )
            // InternalMnc.g:2826:3: () otherlv_1= 'Command' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'Generate' otherlv_7= 'Response' otherlv_8= '{' ( (lv_responseBlock_9_0= ruleResponseBlock ) )* otherlv_10= '}' )? otherlv_11= '}'
            {
            // InternalMnc.g:2826:3: ()
            // InternalMnc.g:2827:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getCommandResponseBlockAccess().getCommandResponseBlockAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,61,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getCommandResponseBlockAccess().getCommandKeyword_1());
              		
            }
            // InternalMnc.g:2837:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:2838:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:2838:4: ( ruleQualifiedName )
            // InternalMnc.g:2839:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getCommandResponseBlockRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getCommandResponseBlockAccess().getCommandCommandCrossReference_2_0());
              				
            }
            pushFollow(FOLLOW_26);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_3=(Token)match(input,26,FOLLOW_69); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getCommandResponseBlockAccess().getLeftCurlyBracketKeyword_3());
              		
            }
            // InternalMnc.g:2857:3: ( (lv_action_4_0= ruleAction ) )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==31) ) {
                alt70=1;
            }
            switch (alt70) {
                case 1 :
                    // InternalMnc.g:2858:4: (lv_action_4_0= ruleAction )
                    {
                    // InternalMnc.g:2858:4: (lv_action_4_0= ruleAction )
                    // InternalMnc.g:2859:5: lv_action_4_0= ruleAction
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getCommandResponseBlockAccess().getActionActionParserRuleCall_4_0());
                      				
                    }
                    pushFollow(FOLLOW_70);
                    lv_action_4_0=ruleAction();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getCommandResponseBlockRule());
                      					}
                      					set(
                      						current,
                      						"action",
                      						lv_action_4_0,
                      						"com.mncml.dsl.Mnc.Action");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:2876:3: ( (lv_validationRules_5_0= ruleValidation ) )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==78) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // InternalMnc.g:2877:4: (lv_validationRules_5_0= ruleValidation )
            	    {
            	    // InternalMnc.g:2877:4: (lv_validationRules_5_0= ruleValidation )
            	    // InternalMnc.g:2878:5: lv_validationRules_5_0= ruleValidation
            	    {
            	    if ( state.backtracking==0 ) {

            	      					newCompositeNode(grammarAccess.getCommandResponseBlockAccess().getValidationRulesValidationParserRuleCall_5_0());
            	      				
            	    }
            	    pushFollow(FOLLOW_70);
            	    lv_validationRules_5_0=ruleValidation();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					if (current==null) {
            	      						current = createModelElementForParent(grammarAccess.getCommandResponseBlockRule());
            	      					}
            	      					add(
            	      						current,
            	      						"validationRules",
            	      						lv_validationRules_5_0,
            	      						"com.mncml.dsl.Mnc.Validation");
            	      					afterParserOrEnumRuleCall();
            	      				
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop71;
                }
            } while (true);

            // InternalMnc.g:2895:3: (otherlv_6= 'Generate' otherlv_7= 'Response' otherlv_8= '{' ( (lv_responseBlock_9_0= ruleResponseBlock ) )* otherlv_10= '}' )?
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==62) ) {
                alt73=1;
            }
            switch (alt73) {
                case 1 :
                    // InternalMnc.g:2896:4: otherlv_6= 'Generate' otherlv_7= 'Response' otherlv_8= '{' ( (lv_responseBlock_9_0= ruleResponseBlock ) )* otherlv_10= '}'
                    {
                    otherlv_6=(Token)match(input,62,FOLLOW_71); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_6, grammarAccess.getCommandResponseBlockAccess().getGenerateKeyword_6_0());
                      			
                    }
                    otherlv_7=(Token)match(input,63,FOLLOW_26); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_7, grammarAccess.getCommandResponseBlockAccess().getResponseKeyword_6_1());
                      			
                    }
                    otherlv_8=(Token)match(input,26,FOLLOW_72); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_8, grammarAccess.getCommandResponseBlockAccess().getLeftCurlyBracketKeyword_6_2());
                      			
                    }
                    // InternalMnc.g:2908:4: ( (lv_responseBlock_9_0= ruleResponseBlock ) )*
                    loop72:
                    do {
                        int alt72=2;
                        int LA72_0 = input.LA(1);

                        if ( (LA72_0==64) ) {
                            alt72=1;
                        }


                        switch (alt72) {
                    	case 1 :
                    	    // InternalMnc.g:2909:5: (lv_responseBlock_9_0= ruleResponseBlock )
                    	    {
                    	    // InternalMnc.g:2909:5: (lv_responseBlock_9_0= ruleResponseBlock )
                    	    // InternalMnc.g:2910:6: lv_responseBlock_9_0= ruleResponseBlock
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getCommandResponseBlockAccess().getResponseBlockResponseBlockParserRuleCall_6_3_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_72);
                    	    lv_responseBlock_9_0=ruleResponseBlock();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getCommandResponseBlockRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"responseBlock",
                    	      							lv_responseBlock_9_0,
                    	      							"com.mncml.dsl.Mnc.ResponseBlock");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop72;
                        }
                    } while (true);

                    otherlv_10=(Token)match(input,30,FOLLOW_45); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_10, grammarAccess.getCommandResponseBlockAccess().getRightCurlyBracketKeyword_6_4());
                      			
                    }

                    }
                    break;

            }

            otherlv_11=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_11, grammarAccess.getCommandResponseBlockAccess().getRightCurlyBracketKeyword_7());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleCommandResponseBlock"


    // $ANTLR start "entryRuleResponseBlock"
    // InternalMnc.g:2940:1: entryRuleResponseBlock returns [EObject current=null] : iv_ruleResponseBlock= ruleResponseBlock EOF ;
    public final EObject entryRuleResponseBlock() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleResponseBlock = null;


        try {
            // InternalMnc.g:2940:54: (iv_ruleResponseBlock= ruleResponseBlock EOF )
            // InternalMnc.g:2941:2: iv_ruleResponseBlock= ruleResponseBlock EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getResponseBlockRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleResponseBlock=ruleResponseBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleResponseBlock; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleResponseBlock"


    // $ANTLR start "ruleResponseBlock"
    // InternalMnc.g:2947:1: ruleResponseBlock returns [EObject current=null] : ( () otherlv_1= 'expectedResponse' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'ResponseAggregation' otherlv_7= '{' ( (lv_responseAggregationRules_8_0= ruleResponseAggregationRule ) )* otherlv_9= '}' )? otherlv_10= '}' ) ;
    public final EObject ruleResponseBlock() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        EObject lv_action_4_0 = null;

        EObject lv_validationRules_5_0 = null;

        EObject lv_responseAggregationRules_8_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:2953:2: ( ( () otherlv_1= 'expectedResponse' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'ResponseAggregation' otherlv_7= '{' ( (lv_responseAggregationRules_8_0= ruleResponseAggregationRule ) )* otherlv_9= '}' )? otherlv_10= '}' ) )
            // InternalMnc.g:2954:2: ( () otherlv_1= 'expectedResponse' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'ResponseAggregation' otherlv_7= '{' ( (lv_responseAggregationRules_8_0= ruleResponseAggregationRule ) )* otherlv_9= '}' )? otherlv_10= '}' )
            {
            // InternalMnc.g:2954:2: ( () otherlv_1= 'expectedResponse' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'ResponseAggregation' otherlv_7= '{' ( (lv_responseAggregationRules_8_0= ruleResponseAggregationRule ) )* otherlv_9= '}' )? otherlv_10= '}' )
            // InternalMnc.g:2955:3: () otherlv_1= 'expectedResponse' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* (otherlv_6= 'ResponseAggregation' otherlv_7= '{' ( (lv_responseAggregationRules_8_0= ruleResponseAggregationRule ) )* otherlv_9= '}' )? otherlv_10= '}'
            {
            // InternalMnc.g:2955:3: ()
            // InternalMnc.g:2956:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getResponseBlockAccess().getResponseBlockAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,64,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getResponseBlockAccess().getExpectedResponseKeyword_1());
              		
            }
            // InternalMnc.g:2966:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:2967:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:2967:4: ( ruleQualifiedName )
            // InternalMnc.g:2968:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getResponseBlockRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getResponseBlockAccess().getResponseResponseCrossReference_2_0());
              				
            }
            pushFollow(FOLLOW_26);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_3=(Token)match(input,26,FOLLOW_73); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getResponseBlockAccess().getLeftCurlyBracketKeyword_3());
              		
            }
            // InternalMnc.g:2986:3: ( (lv_action_4_0= ruleAction ) )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==31) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // InternalMnc.g:2987:4: (lv_action_4_0= ruleAction )
                    {
                    // InternalMnc.g:2987:4: (lv_action_4_0= ruleAction )
                    // InternalMnc.g:2988:5: lv_action_4_0= ruleAction
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getResponseBlockAccess().getActionActionParserRuleCall_4_0());
                      				
                    }
                    pushFollow(FOLLOW_74);
                    lv_action_4_0=ruleAction();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getResponseBlockRule());
                      					}
                      					set(
                      						current,
                      						"action",
                      						lv_action_4_0,
                      						"com.mncml.dsl.Mnc.Action");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:3005:3: ( (lv_validationRules_5_0= ruleValidation ) )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==78) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // InternalMnc.g:3006:4: (lv_validationRules_5_0= ruleValidation )
            	    {
            	    // InternalMnc.g:3006:4: (lv_validationRules_5_0= ruleValidation )
            	    // InternalMnc.g:3007:5: lv_validationRules_5_0= ruleValidation
            	    {
            	    if ( state.backtracking==0 ) {

            	      					newCompositeNode(grammarAccess.getResponseBlockAccess().getValidationRulesValidationParserRuleCall_5_0());
            	      				
            	    }
            	    pushFollow(FOLLOW_74);
            	    lv_validationRules_5_0=ruleValidation();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					if (current==null) {
            	      						current = createModelElementForParent(grammarAccess.getResponseBlockRule());
            	      					}
            	      					add(
            	      						current,
            	      						"validationRules",
            	      						lv_validationRules_5_0,
            	      						"com.mncml.dsl.Mnc.Validation");
            	      					afterParserOrEnumRuleCall();
            	      				
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);

            // InternalMnc.g:3024:3: (otherlv_6= 'ResponseAggregation' otherlv_7= '{' ( (lv_responseAggregationRules_8_0= ruleResponseAggregationRule ) )* otherlv_9= '}' )?
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==65) ) {
                alt77=1;
            }
            switch (alt77) {
                case 1 :
                    // InternalMnc.g:3025:4: otherlv_6= 'ResponseAggregation' otherlv_7= '{' ( (lv_responseAggregationRules_8_0= ruleResponseAggregationRule ) )* otherlv_9= '}'
                    {
                    otherlv_6=(Token)match(input,65,FOLLOW_26); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_6, grammarAccess.getResponseBlockAccess().getResponseAggregationKeyword_6_0());
                      			
                    }
                    otherlv_7=(Token)match(input,26,FOLLOW_75); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_7, grammarAccess.getResponseBlockAccess().getLeftCurlyBracketKeyword_6_1());
                      			
                    }
                    // InternalMnc.g:3033:4: ( (lv_responseAggregationRules_8_0= ruleResponseAggregationRule ) )*
                    loop76:
                    do {
                        int alt76=2;
                        int LA76_0 = input.LA(1);

                        if ( (LA76_0==66) ) {
                            alt76=1;
                        }


                        switch (alt76) {
                    	case 1 :
                    	    // InternalMnc.g:3034:5: (lv_responseAggregationRules_8_0= ruleResponseAggregationRule )
                    	    {
                    	    // InternalMnc.g:3034:5: (lv_responseAggregationRules_8_0= ruleResponseAggregationRule )
                    	    // InternalMnc.g:3035:6: lv_responseAggregationRules_8_0= ruleResponseAggregationRule
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getResponseBlockAccess().getResponseAggregationRulesResponseAggregationRuleParserRuleCall_6_2_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_75);
                    	    lv_responseAggregationRules_8_0=ruleResponseAggregationRule();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getResponseBlockRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"responseAggregationRules",
                    	      							lv_responseAggregationRules_8_0,
                    	      							"com.mncml.dsl.Mnc.ResponseAggregationRule");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop76;
                        }
                    } while (true);

                    otherlv_9=(Token)match(input,30,FOLLOW_45); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_9, grammarAccess.getResponseBlockAccess().getRightCurlyBracketKeyword_6_3());
                      			
                    }

                    }
                    break;

            }

            otherlv_10=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_10, grammarAccess.getResponseBlockAccess().getRightCurlyBracketKeyword_7());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleResponseBlock"


    // $ANTLR start "entryRuleResponseAggregationRule"
    // InternalMnc.g:3065:1: entryRuleResponseAggregationRule returns [EObject current=null] : iv_ruleResponseAggregationRule= ruleResponseAggregationRule EOF ;
    public final EObject entryRuleResponseAggregationRule() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleResponseAggregationRule = null;


        try {
            // InternalMnc.g:3065:64: (iv_ruleResponseAggregationRule= ruleResponseAggregationRule EOF )
            // InternalMnc.g:3066:2: iv_ruleResponseAggregationRule= ruleResponseAggregationRule EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getResponseAggregationRuleRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleResponseAggregationRule=ruleResponseAggregationRule();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleResponseAggregationRule; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleResponseAggregationRule"


    // $ANTLR start "ruleResponseAggregationRule"
    // InternalMnc.g:3072:1: ruleResponseAggregationRule returns [EObject current=null] : ( () otherlv_1= 'received' otherlv_2= 'Responses' otherlv_3= '(' ( ( ruleQualifiedName ) ) ( ruleBooleanOps ( ( ruleQualifiedName ) ) )* otherlv_7= ')' (otherlv_8= '{' otherlv_9= 'parameterTranslations' otherlv_10= '{' ( (lv_parameterTranslations_11_0= ruleParameterTranslation ) )* otherlv_12= '}' otherlv_13= '}' )? ) ;
    public final EObject ruleResponseAggregationRule() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_12=null;
        Token otherlv_13=null;
        EObject lv_parameterTranslations_11_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:3078:2: ( ( () otherlv_1= 'received' otherlv_2= 'Responses' otherlv_3= '(' ( ( ruleQualifiedName ) ) ( ruleBooleanOps ( ( ruleQualifiedName ) ) )* otherlv_7= ')' (otherlv_8= '{' otherlv_9= 'parameterTranslations' otherlv_10= '{' ( (lv_parameterTranslations_11_0= ruleParameterTranslation ) )* otherlv_12= '}' otherlv_13= '}' )? ) )
            // InternalMnc.g:3079:2: ( () otherlv_1= 'received' otherlv_2= 'Responses' otherlv_3= '(' ( ( ruleQualifiedName ) ) ( ruleBooleanOps ( ( ruleQualifiedName ) ) )* otherlv_7= ')' (otherlv_8= '{' otherlv_9= 'parameterTranslations' otherlv_10= '{' ( (lv_parameterTranslations_11_0= ruleParameterTranslation ) )* otherlv_12= '}' otherlv_13= '}' )? )
            {
            // InternalMnc.g:3079:2: ( () otherlv_1= 'received' otherlv_2= 'Responses' otherlv_3= '(' ( ( ruleQualifiedName ) ) ( ruleBooleanOps ( ( ruleQualifiedName ) ) )* otherlv_7= ')' (otherlv_8= '{' otherlv_9= 'parameterTranslations' otherlv_10= '{' ( (lv_parameterTranslations_11_0= ruleParameterTranslation ) )* otherlv_12= '}' otherlv_13= '}' )? )
            // InternalMnc.g:3080:3: () otherlv_1= 'received' otherlv_2= 'Responses' otherlv_3= '(' ( ( ruleQualifiedName ) ) ( ruleBooleanOps ( ( ruleQualifiedName ) ) )* otherlv_7= ')' (otherlv_8= '{' otherlv_9= 'parameterTranslations' otherlv_10= '{' ( (lv_parameterTranslations_11_0= ruleParameterTranslation ) )* otherlv_12= '}' otherlv_13= '}' )?
            {
            // InternalMnc.g:3080:3: ()
            // InternalMnc.g:3081:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getResponseAggregationRuleAccess().getResponseAggregationRuleAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,66,FOLLOW_76); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getResponseAggregationRuleAccess().getReceivedKeyword_1());
              		
            }
            otherlv_2=(Token)match(input,67,FOLLOW_46); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getResponseAggregationRuleAccess().getResponsesKeyword_2());
              		
            }
            otherlv_3=(Token)match(input,44,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getResponseAggregationRuleAccess().getLeftParenthesisKeyword_3());
              		
            }
            // InternalMnc.g:3099:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:3100:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:3100:4: ( ruleQualifiedName )
            // InternalMnc.g:3101:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getResponseAggregationRuleRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getResponseAggregationRuleAccess().getInputResponsesResponseCrossReference_4_0());
              				
            }
            pushFollow(FOLLOW_77);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:3115:3: ( ruleBooleanOps ( ( ruleQualifiedName ) ) )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( ((LA78_0>=69 && LA78_0<=70)) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // InternalMnc.g:3116:4: ruleBooleanOps ( ( ruleQualifiedName ) )
            	    {
            	    if ( state.backtracking==0 ) {

            	      				newCompositeNode(grammarAccess.getResponseAggregationRuleAccess().getBooleanOpsParserRuleCall_5_0());
            	      			
            	    }
            	    pushFollow(FOLLOW_7);
            	    ruleBooleanOps();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				afterParserOrEnumRuleCall();
            	      			
            	    }
            	    // InternalMnc.g:3123:4: ( ( ruleQualifiedName ) )
            	    // InternalMnc.g:3124:5: ( ruleQualifiedName )
            	    {
            	    // InternalMnc.g:3124:5: ( ruleQualifiedName )
            	    // InternalMnc.g:3125:6: ruleQualifiedName
            	    {
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElement(grammarAccess.getResponseAggregationRuleRule());
            	      						}
            	      					
            	    }
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getResponseAggregationRuleAccess().getInputResponsesResponseCrossReference_5_1_0());
            	      					
            	    }
            	    pushFollow(FOLLOW_77);
            	    ruleQualifiedName();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);

            otherlv_7=(Token)match(input,45,FOLLOW_78); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_7, grammarAccess.getResponseAggregationRuleAccess().getRightParenthesisKeyword_6());
              		
            }
            // InternalMnc.g:3144:3: (otherlv_8= '{' otherlv_9= 'parameterTranslations' otherlv_10= '{' ( (lv_parameterTranslations_11_0= ruleParameterTranslation ) )* otherlv_12= '}' otherlv_13= '}' )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==26) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // InternalMnc.g:3145:4: otherlv_8= '{' otherlv_9= 'parameterTranslations' otherlv_10= '{' ( (lv_parameterTranslations_11_0= ruleParameterTranslation ) )* otherlv_12= '}' otherlv_13= '}'
                    {
                    otherlv_8=(Token)match(input,26,FOLLOW_79); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_8, grammarAccess.getResponseAggregationRuleAccess().getLeftCurlyBracketKeyword_7_0());
                      			
                    }
                    otherlv_9=(Token)match(input,68,FOLLOW_26); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_9, grammarAccess.getResponseAggregationRuleAccess().getParameterTranslationsKeyword_7_1());
                      			
                    }
                    otherlv_10=(Token)match(input,26,FOLLOW_80); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_10, grammarAccess.getResponseAggregationRuleAccess().getLeftCurlyBracketKeyword_7_2());
                      			
                    }
                    // InternalMnc.g:3157:4: ( (lv_parameterTranslations_11_0= ruleParameterTranslation ) )*
                    loop79:
                    do {
                        int alt79=2;
                        int LA79_0 = input.LA(1);

                        if ( (LA79_0==87) ) {
                            alt79=1;
                        }


                        switch (alt79) {
                    	case 1 :
                    	    // InternalMnc.g:3158:5: (lv_parameterTranslations_11_0= ruleParameterTranslation )
                    	    {
                    	    // InternalMnc.g:3158:5: (lv_parameterTranslations_11_0= ruleParameterTranslation )
                    	    // InternalMnc.g:3159:6: lv_parameterTranslations_11_0= ruleParameterTranslation
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getResponseAggregationRuleAccess().getParameterTranslationsParameterTranslationParserRuleCall_7_3_0());
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_80);
                    	    lv_parameterTranslations_11_0=ruleParameterTranslation();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getResponseAggregationRuleRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"parameterTranslations",
                    	      							lv_parameterTranslations_11_0,
                    	      							"com.mncml.dsl.Mnc.ParameterTranslation");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop79;
                        }
                    } while (true);

                    otherlv_12=(Token)match(input,30,FOLLOW_45); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_12, grammarAccess.getResponseAggregationRuleAccess().getRightCurlyBracketKeyword_7_4());
                      			
                    }
                    otherlv_13=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_13, grammarAccess.getResponseAggregationRuleAccess().getRightCurlyBracketKeyword_7_5());
                      			
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleResponseAggregationRule"


    // $ANTLR start "entryRuleBooleanOps"
    // InternalMnc.g:3189:1: entryRuleBooleanOps returns [String current=null] : iv_ruleBooleanOps= ruleBooleanOps EOF ;
    public final String entryRuleBooleanOps() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBooleanOps = null;


        try {
            // InternalMnc.g:3189:50: (iv_ruleBooleanOps= ruleBooleanOps EOF )
            // InternalMnc.g:3190:2: iv_ruleBooleanOps= ruleBooleanOps EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBooleanOpsRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleBooleanOps=ruleBooleanOps();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBooleanOps.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleBooleanOps"


    // $ANTLR start "ruleBooleanOps"
    // InternalMnc.g:3196:1: ruleBooleanOps returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'and' | kw= 'or' ) ;
    public final AntlrDatatypeRuleToken ruleBooleanOps() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalMnc.g:3202:2: ( (kw= 'and' | kw= 'or' ) )
            // InternalMnc.g:3203:2: (kw= 'and' | kw= 'or' )
            {
            // InternalMnc.g:3203:2: (kw= 'and' | kw= 'or' )
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==69) ) {
                alt81=1;
            }
            else if ( (LA81_0==70) ) {
                alt81=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // InternalMnc.g:3204:3: kw= 'and'
                    {
                    kw=(Token)match(input,69,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(kw);
                      			newLeafNode(kw, grammarAccess.getBooleanOpsAccess().getAndKeyword_0());
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalMnc.g:3210:3: kw= 'or'
                    {
                    kw=(Token)match(input,70,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(kw);
                      			newLeafNode(kw, grammarAccess.getBooleanOpsAccess().getOrKeyword_1());
                      		
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleBooleanOps"


    // $ANTLR start "entryRuleCheckParameterCondition"
    // InternalMnc.g:3219:1: entryRuleCheckParameterCondition returns [EObject current=null] : iv_ruleCheckParameterCondition= ruleCheckParameterCondition EOF ;
    public final EObject entryRuleCheckParameterCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCheckParameterCondition = null;


        try {
            // InternalMnc.g:3219:64: (iv_ruleCheckParameterCondition= ruleCheckParameterCondition EOF )
            // InternalMnc.g:3220:2: iv_ruleCheckParameterCondition= ruleCheckParameterCondition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getCheckParameterConditionRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleCheckParameterCondition=ruleCheckParameterCondition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleCheckParameterCondition; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:3226:1: ruleCheckParameterCondition returns [EObject current=null] : ( () ( ( (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )* ) | ( (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')' ) ) otherlv_13= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* ) ) ) otherlv_31= ']' ) ;
    public final EObject ruleCheckParameterCondition() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_12=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token otherlv_16=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_21=null;
        Token otherlv_23=null;
        Token otherlv_24=null;
        Token otherlv_25=null;
        Token otherlv_26=null;
        Token otherlv_28=null;
        Token otherlv_30=null;
        Token otherlv_31=null;
        EObject lv_checkMaxValue_18_0 = null;

        EObject lv_checkMinValue_22_0 = null;

        EObject lv_checkValues_27_0 = null;

        EObject lv_checkValues_29_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:3232:2: ( ( () ( ( (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )* ) | ( (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')' ) ) otherlv_13= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* ) ) ) otherlv_31= ']' ) )
            // InternalMnc.g:3233:2: ( () ( ( (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )* ) | ( (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')' ) ) otherlv_13= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* ) ) ) otherlv_31= ']' )
            {
            // InternalMnc.g:3233:2: ( () ( ( (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )* ) | ( (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')' ) ) otherlv_13= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* ) ) ) otherlv_31= ']' )
            // InternalMnc.g:3234:3: () ( ( (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )* ) | ( (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')' ) ) otherlv_13= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* ) ) ) otherlv_31= ']'
            {
            // InternalMnc.g:3234:3: ()
            // InternalMnc.g:3235:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getCheckParameterConditionAccess().getCheckParameterConditionAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:3241:3: ( ( (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )* ) | ( (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')' ) )
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==71) ) {
                alt85=1;
            }
            else if ( (LA85_0==72) ) {
                alt85=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);

                throw nvae;
            }
            switch (alt85) {
                case 1 :
                    // InternalMnc.g:3242:4: ( (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )* )
                    {
                    // InternalMnc.g:3242:4: ( (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )* )
                    // InternalMnc.g:3243:5: (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) ) ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )*
                    {
                    // InternalMnc.g:3243:5: (otherlv_1= 'parameters' ( ( ruleQualifiedName ) ) )
                    // InternalMnc.g:3244:6: otherlv_1= 'parameters' ( ( ruleQualifiedName ) )
                    {
                    otherlv_1=(Token)match(input,71,FOLLOW_7); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						newLeafNode(otherlv_1, grammarAccess.getCheckParameterConditionAccess().getParametersKeyword_1_0_0_0());
                      					
                    }
                    // InternalMnc.g:3248:6: ( ( ruleQualifiedName ) )
                    // InternalMnc.g:3249:7: ( ruleQualifiedName )
                    {
                    // InternalMnc.g:3249:7: ( ruleQualifiedName )
                    // InternalMnc.g:3250:8: ruleQualifiedName
                    {
                    if ( state.backtracking==0 ) {

                      								if (current==null) {
                      									current = createModelElement(grammarAccess.getCheckParameterConditionRule());
                      								}
                      							
                    }
                    if ( state.backtracking==0 ) {

                      								newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getParameterParameterCrossReference_1_0_0_1_0());
                      							
                    }
                    pushFollow(FOLLOW_81);
                    ruleQualifiedName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      								afterParserOrEnumRuleCall();
                      							
                    }

                    }


                    }


                    }

                    // InternalMnc.g:3265:5: ( ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) ) )*
                    loop83:
                    do {
                        int alt83=2;
                        int LA83_0 = input.LA(1);

                        if ( (LA83_0==17||(LA83_0>=69 && LA83_0<=70)) ) {
                            alt83=1;
                        }


                        switch (alt83) {
                    	case 1 :
                    	    // InternalMnc.g:3266:6: ( ruleBooleanOps | otherlv_4= ',' ) ( ( ruleQualifiedName ) )
                    	    {
                    	    // InternalMnc.g:3266:6: ( ruleBooleanOps | otherlv_4= ',' )
                    	    int alt82=2;
                    	    int LA82_0 = input.LA(1);

                    	    if ( ((LA82_0>=69 && LA82_0<=70)) ) {
                    	        alt82=1;
                    	    }
                    	    else if ( (LA82_0==17) ) {
                    	        alt82=2;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return current;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("", 82, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt82) {
                    	        case 1 :
                    	            // InternalMnc.g:3267:7: ruleBooleanOps
                    	            {
                    	            if ( state.backtracking==0 ) {

                    	              							newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getBooleanOpsParserRuleCall_1_0_1_0_0());
                    	              						
                    	            }
                    	            pushFollow(FOLLOW_7);
                    	            ruleBooleanOps();

                    	            state._fsp--;
                    	            if (state.failed) return current;
                    	            if ( state.backtracking==0 ) {

                    	              							afterParserOrEnumRuleCall();
                    	              						
                    	            }

                    	            }
                    	            break;
                    	        case 2 :
                    	            // InternalMnc.g:3275:7: otherlv_4= ','
                    	            {
                    	            otherlv_4=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	            if ( state.backtracking==0 ) {

                    	              							newLeafNode(otherlv_4, grammarAccess.getCheckParameterConditionAccess().getCommaKeyword_1_0_1_0_1());
                    	              						
                    	            }

                    	            }
                    	            break;

                    	    }

                    	    // InternalMnc.g:3280:6: ( ( ruleQualifiedName ) )
                    	    // InternalMnc.g:3281:7: ( ruleQualifiedName )
                    	    {
                    	    // InternalMnc.g:3281:7: ( ruleQualifiedName )
                    	    // InternalMnc.g:3282:8: ruleQualifiedName
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      								if (current==null) {
                    	      									current = createModelElement(grammarAccess.getCheckParameterConditionRule());
                    	      								}
                    	      							
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      								newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getParameterParameterCrossReference_1_0_1_1_0());
                    	      							
                    	    }
                    	    pushFollow(FOLLOW_81);
                    	    ruleQualifiedName();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      								afterParserOrEnumRuleCall();
                    	      							
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop83;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // InternalMnc.g:3299:4: ( (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')' )
                    {
                    // InternalMnc.g:3299:4: ( (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')' )
                    // InternalMnc.g:3300:5: (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) ) (otherlv_10= ',' ( ( ruleQualifiedName ) ) )* otherlv_12= ')'
                    {
                    // InternalMnc.g:3300:5: (otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) ) )
                    // InternalMnc.g:3301:6: otherlv_6= 'operation' ( ( ruleQualifiedName ) ) otherlv_8= '(' ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) )
                    {
                    otherlv_6=(Token)match(input,72,FOLLOW_7); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						newLeafNode(otherlv_6, grammarAccess.getCheckParameterConditionAccess().getOperationKeyword_1_1_0_0());
                      					
                    }
                    // InternalMnc.g:3305:6: ( ( ruleQualifiedName ) )
                    // InternalMnc.g:3306:7: ( ruleQualifiedName )
                    {
                    // InternalMnc.g:3306:7: ( ruleQualifiedName )
                    // InternalMnc.g:3307:8: ruleQualifiedName
                    {
                    if ( state.backtracking==0 ) {

                      								if (current==null) {
                      									current = createModelElement(grammarAccess.getCheckParameterConditionRule());
                      								}
                      							
                    }
                    if ( state.backtracking==0 ) {

                      								newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getOperationsOperationCrossReference_1_1_0_1_0());
                      							
                    }
                    pushFollow(FOLLOW_46);
                    ruleQualifiedName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      								afterParserOrEnumRuleCall();
                      							
                    }

                    }


                    }

                    otherlv_8=(Token)match(input,44,FOLLOW_7); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						newLeafNode(otherlv_8, grammarAccess.getCheckParameterConditionAccess().getLeftParenthesisKeyword_1_1_0_2());
                      					
                    }
                    // InternalMnc.g:3325:6: ( ( ( ruleQualifiedName ) )=> ( ruleQualifiedName ) )
                    // InternalMnc.g:3326:7: ( ( ruleQualifiedName ) )=> ( ruleQualifiedName )
                    {
                    // InternalMnc.g:3330:7: ( ruleQualifiedName )
                    // InternalMnc.g:3331:8: ruleQualifiedName
                    {
                    if ( state.backtracking==0 ) {

                      								if (current==null) {
                      									current = createModelElement(grammarAccess.getCheckParameterConditionRule());
                      								}
                      							
                    }
                    if ( state.backtracking==0 ) {

                      								newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getParameterParameterCrossReference_1_1_0_3_0());
                      							
                    }
                    pushFollow(FOLLOW_64);
                    ruleQualifiedName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      								afterParserOrEnumRuleCall();
                      							
                    }

                    }


                    }


                    }

                    // InternalMnc.g:3346:5: (otherlv_10= ',' ( ( ruleQualifiedName ) ) )*
                    loop84:
                    do {
                        int alt84=2;
                        int LA84_0 = input.LA(1);

                        if ( (LA84_0==17) ) {
                            alt84=1;
                        }


                        switch (alt84) {
                    	case 1 :
                    	    // InternalMnc.g:3347:6: otherlv_10= ',' ( ( ruleQualifiedName ) )
                    	    {
                    	    otherlv_10=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						newLeafNode(otherlv_10, grammarAccess.getCheckParameterConditionAccess().getCommaKeyword_1_1_1_0());
                    	      					
                    	    }
                    	    // InternalMnc.g:3351:6: ( ( ruleQualifiedName ) )
                    	    // InternalMnc.g:3352:7: ( ruleQualifiedName )
                    	    {
                    	    // InternalMnc.g:3352:7: ( ruleQualifiedName )
                    	    // InternalMnc.g:3353:8: ruleQualifiedName
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      								if (current==null) {
                    	      									current = createModelElement(grammarAccess.getCheckParameterConditionRule());
                    	      								}
                    	      							
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      								newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getParameterParameterCrossReference_1_1_1_1_0());
                    	      							
                    	    }
                    	    pushFollow(FOLLOW_64);
                    	    ruleQualifiedName();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      								afterParserOrEnumRuleCall();
                    	      							
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop84;
                        }
                    } while (true);

                    otherlv_12=(Token)match(input,45,FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(otherlv_12, grammarAccess.getCheckParameterConditionAccess().getRightParenthesisKeyword_1_1_2());
                      				
                    }

                    }


                    }
                    break;

            }

            otherlv_13=(Token)match(input,16,FOLLOW_82); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_13, grammarAccess.getCheckParameterConditionAccess().getLeftSquareBracketKeyword_2());
              		
            }
            // InternalMnc.g:3378:3: ( ( ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* ) ) )
            // InternalMnc.g:3379:4: ( ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* ) )
            {
            // InternalMnc.g:3379:4: ( ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* ) )
            // InternalMnc.g:3380:5: ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* )
            {
            getUnorderedGroupHelper().enter(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3());
            // InternalMnc.g:3383:5: ( ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )* )
            // InternalMnc.g:3384:6: ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )*
            {
            // InternalMnc.g:3384:6: ( ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) ) )*
            loop87:
            do {
                int alt87=4;
                int LA87_0 = input.LA(1);

                if ( LA87_0 == 73 && getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 0) ) {
                    alt87=1;
                }
                else if ( LA87_0 == 75 && getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 1) ) {
                    alt87=2;
                }
                else if ( LA87_0 == 76 && getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 2) ) {
                    alt87=3;
                }


                switch (alt87) {
            	case 1 :
            	    // InternalMnc.g:3385:4: ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) )
            	    {
            	    // InternalMnc.g:3385:4: ({...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) ) )
            	    // InternalMnc.g:3386:5: {...}? => ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 0) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 0)");
            	    }
            	    // InternalMnc.g:3386:120: ( ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) ) )
            	    // InternalMnc.g:3387:6: ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 0);
            	    // InternalMnc.g:3390:9: ({...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) ) )
            	    // InternalMnc.g:3390:10: {...}? => (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "true");
            	    }
            	    // InternalMnc.g:3390:19: (otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) ) )
            	    // InternalMnc.g:3390:20: otherlv_15= 'Max' otherlv_16= 'Value' otherlv_17= '=' ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) )
            	    {
            	    otherlv_15=(Token)match(input,73,FOLLOW_83); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_15, grammarAccess.getCheckParameterConditionAccess().getMaxKeyword_3_0_0());
            	      								
            	    }
            	    otherlv_16=(Token)match(input,74,FOLLOW_15); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_16, grammarAccess.getCheckParameterConditionAccess().getValueKeyword_3_0_1());
            	      								
            	    }
            	    otherlv_17=(Token)match(input,21,FOLLOW_20); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_17, grammarAccess.getCheckParameterConditionAccess().getEqualsSignKeyword_3_0_2());
            	      								
            	    }
            	    // InternalMnc.g:3402:9: ( (lv_checkMaxValue_18_0= rulePrimitiveValue ) )
            	    // InternalMnc.g:3403:10: (lv_checkMaxValue_18_0= rulePrimitiveValue )
            	    {
            	    // InternalMnc.g:3403:10: (lv_checkMaxValue_18_0= rulePrimitiveValue )
            	    // InternalMnc.g:3404:11: lv_checkMaxValue_18_0= rulePrimitiveValue
            	    {
            	    if ( state.backtracking==0 ) {

            	      											newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getCheckMaxValuePrimitiveValueParserRuleCall_3_0_3_0());
            	      										
            	    }
            	    pushFollow(FOLLOW_82);
            	    lv_checkMaxValue_18_0=rulePrimitiveValue();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      											if (current==null) {
            	      												current = createModelElementForParent(grammarAccess.getCheckParameterConditionRule());
            	      											}
            	      											set(
            	      												current,
            	      												"checkMaxValue",
            	      												lv_checkMaxValue_18_0,
            	      												"com.dml.dsl.Dml.PrimitiveValue");
            	      											afterParserOrEnumRuleCall();
            	      										
            	    }

            	    }


            	    }


            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3());

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalMnc.g:3427:4: ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) )
            	    {
            	    // InternalMnc.g:3427:4: ({...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) ) )
            	    // InternalMnc.g:3428:5: {...}? => ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 1) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 1)");
            	    }
            	    // InternalMnc.g:3428:120: ( ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) ) )
            	    // InternalMnc.g:3429:6: ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 1);
            	    // InternalMnc.g:3432:9: ({...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) ) )
            	    // InternalMnc.g:3432:10: {...}? => (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "true");
            	    }
            	    // InternalMnc.g:3432:19: (otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) ) )
            	    // InternalMnc.g:3432:20: otherlv_19= 'Min' otherlv_20= 'Value' otherlv_21= '=' ( (lv_checkMinValue_22_0= rulePrimitiveValue ) )
            	    {
            	    otherlv_19=(Token)match(input,75,FOLLOW_83); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_19, grammarAccess.getCheckParameterConditionAccess().getMinKeyword_3_1_0());
            	      								
            	    }
            	    otherlv_20=(Token)match(input,74,FOLLOW_15); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_20, grammarAccess.getCheckParameterConditionAccess().getValueKeyword_3_1_1());
            	      								
            	    }
            	    otherlv_21=(Token)match(input,21,FOLLOW_20); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_21, grammarAccess.getCheckParameterConditionAccess().getEqualsSignKeyword_3_1_2());
            	      								
            	    }
            	    // InternalMnc.g:3444:9: ( (lv_checkMinValue_22_0= rulePrimitiveValue ) )
            	    // InternalMnc.g:3445:10: (lv_checkMinValue_22_0= rulePrimitiveValue )
            	    {
            	    // InternalMnc.g:3445:10: (lv_checkMinValue_22_0= rulePrimitiveValue )
            	    // InternalMnc.g:3446:11: lv_checkMinValue_22_0= rulePrimitiveValue
            	    {
            	    if ( state.backtracking==0 ) {

            	      											newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getCheckMinValuePrimitiveValueParserRuleCall_3_1_3_0());
            	      										
            	    }
            	    pushFollow(FOLLOW_82);
            	    lv_checkMinValue_22_0=rulePrimitiveValue();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      											if (current==null) {
            	      												current = createModelElementForParent(grammarAccess.getCheckParameterConditionRule());
            	      											}
            	      											set(
            	      												current,
            	      												"checkMinValue",
            	      												lv_checkMinValue_22_0,
            	      												"com.dml.dsl.Dml.PrimitiveValue");
            	      											afterParserOrEnumRuleCall();
            	      										
            	    }

            	    }


            	    }


            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3());

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalMnc.g:3469:4: ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) )
            	    {
            	    // InternalMnc.g:3469:4: ({...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) ) )
            	    // InternalMnc.g:3470:5: {...}? => ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 2) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "getUnorderedGroupHelper().canSelect(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 2)");
            	    }
            	    // InternalMnc.g:3470:120: ( ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) ) )
            	    // InternalMnc.g:3471:6: ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3(), 2);
            	    // InternalMnc.g:3474:9: ({...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' ) )
            	    // InternalMnc.g:3474:10: {...}? => (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleCheckParameterCondition", "true");
            	    }
            	    // InternalMnc.g:3474:19: (otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')' )
            	    // InternalMnc.g:3474:20: otherlv_23= 'Possible' otherlv_24= 'Values' otherlv_25= '=' otherlv_26= '(' ( (lv_checkValues_27_0= rulePrimitiveValue ) ) (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )* otherlv_30= ')'
            	    {
            	    otherlv_23=(Token)match(input,76,FOLLOW_84); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_23, grammarAccess.getCheckParameterConditionAccess().getPossibleKeyword_3_2_0());
            	      								
            	    }
            	    otherlv_24=(Token)match(input,77,FOLLOW_15); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_24, grammarAccess.getCheckParameterConditionAccess().getValuesKeyword_3_2_1());
            	      								
            	    }
            	    otherlv_25=(Token)match(input,21,FOLLOW_46); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_25, grammarAccess.getCheckParameterConditionAccess().getEqualsSignKeyword_3_2_2());
            	      								
            	    }
            	    otherlv_26=(Token)match(input,44,FOLLOW_20); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_26, grammarAccess.getCheckParameterConditionAccess().getLeftParenthesisKeyword_3_2_3());
            	      								
            	    }
            	    // InternalMnc.g:3490:9: ( (lv_checkValues_27_0= rulePrimitiveValue ) )
            	    // InternalMnc.g:3491:10: (lv_checkValues_27_0= rulePrimitiveValue )
            	    {
            	    // InternalMnc.g:3491:10: (lv_checkValues_27_0= rulePrimitiveValue )
            	    // InternalMnc.g:3492:11: lv_checkValues_27_0= rulePrimitiveValue
            	    {
            	    if ( state.backtracking==0 ) {

            	      											newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getCheckValuesPrimitiveValueParserRuleCall_3_2_4_0());
            	      										
            	    }
            	    pushFollow(FOLLOW_64);
            	    lv_checkValues_27_0=rulePrimitiveValue();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      											if (current==null) {
            	      												current = createModelElementForParent(grammarAccess.getCheckParameterConditionRule());
            	      											}
            	      											add(
            	      												current,
            	      												"checkValues",
            	      												lv_checkValues_27_0,
            	      												"com.dml.dsl.Dml.PrimitiveValue");
            	      											afterParserOrEnumRuleCall();
            	      										
            	    }

            	    }


            	    }

            	    // InternalMnc.g:3509:9: (otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) ) )*
            	    loop86:
            	    do {
            	        int alt86=2;
            	        int LA86_0 = input.LA(1);

            	        if ( (LA86_0==17) ) {
            	            alt86=1;
            	        }


            	        switch (alt86) {
            	    	case 1 :
            	    	    // InternalMnc.g:3510:10: otherlv_28= ',' ( (lv_checkValues_29_0= rulePrimitiveValue ) )
            	    	    {
            	    	    otherlv_28=(Token)match(input,17,FOLLOW_20); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      										newLeafNode(otherlv_28, grammarAccess.getCheckParameterConditionAccess().getCommaKeyword_3_2_5_0());
            	    	      									
            	    	    }
            	    	    // InternalMnc.g:3514:10: ( (lv_checkValues_29_0= rulePrimitiveValue ) )
            	    	    // InternalMnc.g:3515:11: (lv_checkValues_29_0= rulePrimitiveValue )
            	    	    {
            	    	    // InternalMnc.g:3515:11: (lv_checkValues_29_0= rulePrimitiveValue )
            	    	    // InternalMnc.g:3516:12: lv_checkValues_29_0= rulePrimitiveValue
            	    	    {
            	    	    if ( state.backtracking==0 ) {

            	    	      												newCompositeNode(grammarAccess.getCheckParameterConditionAccess().getCheckValuesPrimitiveValueParserRuleCall_3_2_5_1_0());
            	    	      											
            	    	    }
            	    	    pushFollow(FOLLOW_64);
            	    	    lv_checkValues_29_0=rulePrimitiveValue();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      												if (current==null) {
            	    	      													current = createModelElementForParent(grammarAccess.getCheckParameterConditionRule());
            	    	      												}
            	    	      												add(
            	    	      													current,
            	    	      													"checkValues",
            	    	      													lv_checkValues_29_0,
            	    	      													"com.dml.dsl.Dml.PrimitiveValue");
            	    	      												afterParserOrEnumRuleCall();
            	    	      											
            	    	    }

            	    	    }


            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop86;
            	        }
            	    } while (true);

            	    otherlv_30=(Token)match(input,45,FOLLOW_82); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_30, grammarAccess.getCheckParameterConditionAccess().getRightParenthesisKeyword_3_2_6());
            	      								
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3());

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop87;
                }
            } while (true);


            }


            }

            getUnorderedGroupHelper().leave(grammarAccess.getCheckParameterConditionAccess().getUnorderedGroup_3());

            }

            otherlv_31=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_31, grammarAccess.getCheckParameterConditionAccess().getRightSquareBracketKeyword_4());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleCheckParameterCondition"


    // $ANTLR start "entryRuleValidation"
    // InternalMnc.g:3559:1: entryRuleValidation returns [EObject current=null] : iv_ruleValidation= ruleValidation EOF ;
    public final EObject entryRuleValidation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleValidation = null;


        try {
            // InternalMnc.g:3559:51: (iv_ruleValidation= ruleValidation EOF )
            // InternalMnc.g:3560:2: iv_ruleValidation= ruleValidation EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getValidationRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleValidation=ruleValidation();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleValidation; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleValidation"


    // $ANTLR start "ruleValidation"
    // InternalMnc.g:3566:1: ruleValidation returns [EObject current=null] : ( () otherlv_1= 'Validate' otherlv_2= '{' ( ( (lv_parametersValidationRules_3_0= ruleCheckParameterCondition ) ) ( ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) ) )* )? ( ( ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* ) ) ) otherlv_11= '}' ) ;
    public final EObject ruleValidation() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        EObject lv_parametersValidationRules_3_0 = null;

        EObject lv_parametersValidationRules_5_0 = null;

        EObject lv_onFailedAction_8_0 = null;

        EObject lv_onSuccessAction_10_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:3572:2: ( ( () otherlv_1= 'Validate' otherlv_2= '{' ( ( (lv_parametersValidationRules_3_0= ruleCheckParameterCondition ) ) ( ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) ) )* )? ( ( ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* ) ) ) otherlv_11= '}' ) )
            // InternalMnc.g:3573:2: ( () otherlv_1= 'Validate' otherlv_2= '{' ( ( (lv_parametersValidationRules_3_0= ruleCheckParameterCondition ) ) ( ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) ) )* )? ( ( ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* ) ) ) otherlv_11= '}' )
            {
            // InternalMnc.g:3573:2: ( () otherlv_1= 'Validate' otherlv_2= '{' ( ( (lv_parametersValidationRules_3_0= ruleCheckParameterCondition ) ) ( ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) ) )* )? ( ( ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* ) ) ) otherlv_11= '}' )
            // InternalMnc.g:3574:3: () otherlv_1= 'Validate' otherlv_2= '{' ( ( (lv_parametersValidationRules_3_0= ruleCheckParameterCondition ) ) ( ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) ) )* )? ( ( ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* ) ) ) otherlv_11= '}'
            {
            // InternalMnc.g:3574:3: ()
            // InternalMnc.g:3575:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getValidationAccess().getValidationAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,78,FOLLOW_26); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getValidationAccess().getValidateKeyword_1());
              		
            }
            otherlv_2=(Token)match(input,26,FOLLOW_85); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getValidationAccess().getLeftCurlyBracketKeyword_2());
              		
            }
            // InternalMnc.g:3589:3: ( ( (lv_parametersValidationRules_3_0= ruleCheckParameterCondition ) ) ( ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) ) )* )?
            int alt89=2;
            int LA89_0 = input.LA(1);

            if ( ((LA89_0>=71 && LA89_0<=72)) ) {
                alt89=1;
            }
            switch (alt89) {
                case 1 :
                    // InternalMnc.g:3590:4: ( (lv_parametersValidationRules_3_0= ruleCheckParameterCondition ) ) ( ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) ) )*
                    {
                    // InternalMnc.g:3590:4: ( (lv_parametersValidationRules_3_0= ruleCheckParameterCondition ) )
                    // InternalMnc.g:3591:5: (lv_parametersValidationRules_3_0= ruleCheckParameterCondition )
                    {
                    // InternalMnc.g:3591:5: (lv_parametersValidationRules_3_0= ruleCheckParameterCondition )
                    // InternalMnc.g:3592:6: lv_parametersValidationRules_3_0= ruleCheckParameterCondition
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getValidationAccess().getParametersValidationRulesCheckParameterConditionParserRuleCall_3_0_0());
                      					
                    }
                    pushFollow(FOLLOW_86);
                    lv_parametersValidationRules_3_0=ruleCheckParameterCondition();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getValidationRule());
                      						}
                      						add(
                      							current,
                      							"parametersValidationRules",
                      							lv_parametersValidationRules_3_0,
                      							"com.mncml.dsl.Mnc.CheckParameterCondition");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalMnc.g:3609:4: ( ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) ) )*
                    loop88:
                    do {
                        int alt88=2;
                        int LA88_0 = input.LA(1);

                        if ( ((LA88_0>=69 && LA88_0<=70)) ) {
                            alt88=1;
                        }


                        switch (alt88) {
                    	case 1 :
                    	    // InternalMnc.g:3610:5: ruleBooleanOps ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) )
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      					newCompositeNode(grammarAccess.getValidationAccess().getBooleanOpsParserRuleCall_3_1_0());
                    	      				
                    	    }
                    	    pushFollow(FOLLOW_87);
                    	    ruleBooleanOps();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					afterParserOrEnumRuleCall();
                    	      				
                    	    }
                    	    // InternalMnc.g:3617:5: ( (lv_parametersValidationRules_5_0= ruleCheckParameterCondition ) )
                    	    // InternalMnc.g:3618:6: (lv_parametersValidationRules_5_0= ruleCheckParameterCondition )
                    	    {
                    	    // InternalMnc.g:3618:6: (lv_parametersValidationRules_5_0= ruleCheckParameterCondition )
                    	    // InternalMnc.g:3619:7: lv_parametersValidationRules_5_0= ruleCheckParameterCondition
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getValidationAccess().getParametersValidationRulesCheckParameterConditionParserRuleCall_3_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_86);
                    	    lv_parametersValidationRules_5_0=ruleCheckParameterCondition();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      							if (current==null) {
                    	      								current = createModelElementForParent(grammarAccess.getValidationRule());
                    	      							}
                    	      							add(
                    	      								current,
                    	      								"parametersValidationRules",
                    	      								lv_parametersValidationRules_5_0,
                    	      								"com.mncml.dsl.Mnc.CheckParameterCondition");
                    	      							afterParserOrEnumRuleCall();
                    	      						
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop88;
                        }
                    } while (true);


                    }
                    break;

            }

            // InternalMnc.g:3638:3: ( ( ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* ) ) )
            // InternalMnc.g:3639:4: ( ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* ) )
            {
            // InternalMnc.g:3639:4: ( ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* ) )
            // InternalMnc.g:3640:5: ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* )
            {
            getUnorderedGroupHelper().enter(grammarAccess.getValidationAccess().getUnorderedGroup_4());
            // InternalMnc.g:3643:5: ( ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )* )
            // InternalMnc.g:3644:6: ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )*
            {
            // InternalMnc.g:3644:6: ( ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) ) )*
            loop90:
            do {
                int alt90=3;
                int LA90_0 = input.LA(1);

                if ( LA90_0 == 79 && getUnorderedGroupHelper().canSelect(grammarAccess.getValidationAccess().getUnorderedGroup_4(), 0) ) {
                    alt90=1;
                }
                else if ( LA90_0 == 80 && getUnorderedGroupHelper().canSelect(grammarAccess.getValidationAccess().getUnorderedGroup_4(), 1) ) {
                    alt90=2;
                }


                switch (alt90) {
            	case 1 :
            	    // InternalMnc.g:3645:4: ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) )
            	    {
            	    // InternalMnc.g:3645:4: ({...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) ) )
            	    // InternalMnc.g:3646:5: {...}? => ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getValidationAccess().getUnorderedGroup_4(), 0) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleValidation", "getUnorderedGroupHelper().canSelect(grammarAccess.getValidationAccess().getUnorderedGroup_4(), 0)");
            	    }
            	    // InternalMnc.g:3646:107: ( ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) ) )
            	    // InternalMnc.g:3647:6: ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getValidationAccess().getUnorderedGroup_4(), 0);
            	    // InternalMnc.g:3650:9: ({...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) ) )
            	    // InternalMnc.g:3650:10: {...}? => (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleValidation", "true");
            	    }
            	    // InternalMnc.g:3650:19: (otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) ) )
            	    // InternalMnc.g:3650:20: otherlv_7= 'onFail' ( (lv_onFailedAction_8_0= ruleAction ) )
            	    {
            	    otherlv_7=(Token)match(input,79,FOLLOW_88); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_7, grammarAccess.getValidationAccess().getOnFailKeyword_4_0_0());
            	      								
            	    }
            	    // InternalMnc.g:3654:9: ( (lv_onFailedAction_8_0= ruleAction ) )
            	    // InternalMnc.g:3655:10: (lv_onFailedAction_8_0= ruleAction )
            	    {
            	    // InternalMnc.g:3655:10: (lv_onFailedAction_8_0= ruleAction )
            	    // InternalMnc.g:3656:11: lv_onFailedAction_8_0= ruleAction
            	    {
            	    if ( state.backtracking==0 ) {

            	      											newCompositeNode(grammarAccess.getValidationAccess().getOnFailedActionActionParserRuleCall_4_0_1_0());
            	      										
            	    }
            	    pushFollow(FOLLOW_89);
            	    lv_onFailedAction_8_0=ruleAction();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      											if (current==null) {
            	      												current = createModelElementForParent(grammarAccess.getValidationRule());
            	      											}
            	      											set(
            	      												current,
            	      												"onFailedAction",
            	      												lv_onFailedAction_8_0,
            	      												"com.mncml.dsl.Mnc.Action");
            	      											afterParserOrEnumRuleCall();
            	      										
            	    }

            	    }


            	    }


            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getValidationAccess().getUnorderedGroup_4());

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalMnc.g:3679:4: ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) )
            	    {
            	    // InternalMnc.g:3679:4: ({...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) ) )
            	    // InternalMnc.g:3680:5: {...}? => ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getValidationAccess().getUnorderedGroup_4(), 1) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleValidation", "getUnorderedGroupHelper().canSelect(grammarAccess.getValidationAccess().getUnorderedGroup_4(), 1)");
            	    }
            	    // InternalMnc.g:3680:107: ( ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) ) )
            	    // InternalMnc.g:3681:6: ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getValidationAccess().getUnorderedGroup_4(), 1);
            	    // InternalMnc.g:3684:9: ({...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) ) )
            	    // InternalMnc.g:3684:10: {...}? => (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleValidation", "true");
            	    }
            	    // InternalMnc.g:3684:19: (otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) ) )
            	    // InternalMnc.g:3684:20: otherlv_9= 'onSuccess' ( (lv_onSuccessAction_10_0= ruleAction ) )
            	    {
            	    otherlv_9=(Token)match(input,80,FOLLOW_88); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      									newLeafNode(otherlv_9, grammarAccess.getValidationAccess().getOnSuccessKeyword_4_1_0());
            	      								
            	    }
            	    // InternalMnc.g:3688:9: ( (lv_onSuccessAction_10_0= ruleAction ) )
            	    // InternalMnc.g:3689:10: (lv_onSuccessAction_10_0= ruleAction )
            	    {
            	    // InternalMnc.g:3689:10: (lv_onSuccessAction_10_0= ruleAction )
            	    // InternalMnc.g:3690:11: lv_onSuccessAction_10_0= ruleAction
            	    {
            	    if ( state.backtracking==0 ) {

            	      											newCompositeNode(grammarAccess.getValidationAccess().getOnSuccessActionActionParserRuleCall_4_1_1_0());
            	      										
            	    }
            	    pushFollow(FOLLOW_89);
            	    lv_onSuccessAction_10_0=ruleAction();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      											if (current==null) {
            	      												current = createModelElementForParent(grammarAccess.getValidationRule());
            	      											}
            	      											set(
            	      												current,
            	      												"onSuccessAction",
            	      												lv_onSuccessAction_10_0,
            	      												"com.mncml.dsl.Mnc.Action");
            	      											afterParserOrEnumRuleCall();
            	      										
            	    }

            	    }


            	    }


            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getValidationAccess().getUnorderedGroup_4());

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop90;
                }
            } while (true);


            }


            }

            getUnorderedGroupHelper().leave(grammarAccess.getValidationAccess().getUnorderedGroup_4());

            }

            otherlv_11=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_11, grammarAccess.getValidationAccess().getRightCurlyBracketKeyword_5());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleValidation"


    // $ANTLR start "entryRuleTransition"
    // InternalMnc.g:3728:1: entryRuleTransition returns [EObject current=null] : iv_ruleTransition= ruleTransition EOF ;
    public final EObject entryRuleTransition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTransition = null;


        try {
            // InternalMnc.g:3728:51: (iv_ruleTransition= ruleTransition EOF )
            // InternalMnc.g:3729:2: iv_ruleTransition= ruleTransition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTransitionRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleTransition=ruleTransition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTransition; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleTransition"


    // $ANTLR start "ruleTransition"
    // InternalMnc.g:3735:1: ruleTransition returns [EObject current=null] : ( () otherlv_1= 'currentState' ( ( ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )* ) | otherlv_5= 'any' ) (otherlv_6= '(' otherlv_7= 'exitAction' ( (lv_exitAction_8_0= ruleAction ) ) otherlv_9= ')' )? otherlv_10= '=>' otherlv_11= 'nextState' ( (otherlv_12= RULE_ID ) ) (otherlv_13= '(' otherlv_14= 'entryAction' ( (lv_entryAction_15_0= ruleAction ) ) otherlv_16= ')' )? ) ;
    public final EObject ruleTransition() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_16=null;
        EObject lv_exitAction_8_0 = null;

        EObject lv_entryAction_15_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:3741:2: ( ( () otherlv_1= 'currentState' ( ( ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )* ) | otherlv_5= 'any' ) (otherlv_6= '(' otherlv_7= 'exitAction' ( (lv_exitAction_8_0= ruleAction ) ) otherlv_9= ')' )? otherlv_10= '=>' otherlv_11= 'nextState' ( (otherlv_12= RULE_ID ) ) (otherlv_13= '(' otherlv_14= 'entryAction' ( (lv_entryAction_15_0= ruleAction ) ) otherlv_16= ')' )? ) )
            // InternalMnc.g:3742:2: ( () otherlv_1= 'currentState' ( ( ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )* ) | otherlv_5= 'any' ) (otherlv_6= '(' otherlv_7= 'exitAction' ( (lv_exitAction_8_0= ruleAction ) ) otherlv_9= ')' )? otherlv_10= '=>' otherlv_11= 'nextState' ( (otherlv_12= RULE_ID ) ) (otherlv_13= '(' otherlv_14= 'entryAction' ( (lv_entryAction_15_0= ruleAction ) ) otherlv_16= ')' )? )
            {
            // InternalMnc.g:3742:2: ( () otherlv_1= 'currentState' ( ( ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )* ) | otherlv_5= 'any' ) (otherlv_6= '(' otherlv_7= 'exitAction' ( (lv_exitAction_8_0= ruleAction ) ) otherlv_9= ')' )? otherlv_10= '=>' otherlv_11= 'nextState' ( (otherlv_12= RULE_ID ) ) (otherlv_13= '(' otherlv_14= 'entryAction' ( (lv_entryAction_15_0= ruleAction ) ) otherlv_16= ')' )? )
            // InternalMnc.g:3743:3: () otherlv_1= 'currentState' ( ( ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )* ) | otherlv_5= 'any' ) (otherlv_6= '(' otherlv_7= 'exitAction' ( (lv_exitAction_8_0= ruleAction ) ) otherlv_9= ')' )? otherlv_10= '=>' otherlv_11= 'nextState' ( (otherlv_12= RULE_ID ) ) (otherlv_13= '(' otherlv_14= 'entryAction' ( (lv_entryAction_15_0= ruleAction ) ) otherlv_16= ')' )?
            {
            // InternalMnc.g:3743:3: ()
            // InternalMnc.g:3744:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getTransitionAccess().getTransitionAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,81,FOLLOW_90); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getTransitionAccess().getCurrentStateKeyword_1());
              		
            }
            // InternalMnc.g:3754:3: ( ( ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )* ) | otherlv_5= 'any' )
            int alt92=2;
            int LA92_0 = input.LA(1);

            if ( (LA92_0==RULE_ID) ) {
                alt92=1;
            }
            else if ( (LA92_0==82) ) {
                alt92=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }
            switch (alt92) {
                case 1 :
                    // InternalMnc.g:3755:4: ( ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )* )
                    {
                    // InternalMnc.g:3755:4: ( ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )* )
                    // InternalMnc.g:3756:5: ( (otherlv_2= RULE_ID ) ) (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )*
                    {
                    // InternalMnc.g:3756:5: ( (otherlv_2= RULE_ID ) )
                    // InternalMnc.g:3757:6: (otherlv_2= RULE_ID )
                    {
                    // InternalMnc.g:3757:6: (otherlv_2= RULE_ID )
                    // InternalMnc.g:3758:7: otherlv_2= RULE_ID
                    {
                    if ( state.backtracking==0 ) {

                      							if (current==null) {
                      								current = createModelElement(grammarAccess.getTransitionRule());
                      							}
                      						
                    }
                    otherlv_2=(Token)match(input,RULE_ID,FOLLOW_91); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      							newLeafNode(otherlv_2, grammarAccess.getTransitionAccess().getCurrentStateOperatingStateCrossReference_2_0_0_0());
                      						
                    }

                    }


                    }

                    // InternalMnc.g:3769:5: (otherlv_3= ',' ( (otherlv_4= RULE_ID ) ) )*
                    loop91:
                    do {
                        int alt91=2;
                        int LA91_0 = input.LA(1);

                        if ( (LA91_0==17) ) {
                            alt91=1;
                        }


                        switch (alt91) {
                    	case 1 :
                    	    // InternalMnc.g:3770:6: otherlv_3= ',' ( (otherlv_4= RULE_ID ) )
                    	    {
                    	    otherlv_3=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						newLeafNode(otherlv_3, grammarAccess.getTransitionAccess().getCommaKeyword_2_0_1_0());
                    	      					
                    	    }
                    	    // InternalMnc.g:3774:6: ( (otherlv_4= RULE_ID ) )
                    	    // InternalMnc.g:3775:7: (otherlv_4= RULE_ID )
                    	    {
                    	    // InternalMnc.g:3775:7: (otherlv_4= RULE_ID )
                    	    // InternalMnc.g:3776:8: otherlv_4= RULE_ID
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      								if (current==null) {
                    	      									current = createModelElement(grammarAccess.getTransitionRule());
                    	      								}
                    	      							
                    	    }
                    	    otherlv_4=(Token)match(input,RULE_ID,FOLLOW_91); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      								newLeafNode(otherlv_4, grammarAccess.getTransitionAccess().getCurrentStateOperatingStateCrossReference_2_0_1_1_0());
                    	      							
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop91;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // InternalMnc.g:3790:4: otherlv_5= 'any'
                    {
                    otherlv_5=(Token)match(input,82,FOLLOW_92); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_5, grammarAccess.getTransitionAccess().getAnyKeyword_2_1());
                      			
                    }

                    }
                    break;

            }

            // InternalMnc.g:3795:3: (otherlv_6= '(' otherlv_7= 'exitAction' ( (lv_exitAction_8_0= ruleAction ) ) otherlv_9= ')' )?
            int alt93=2;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==44) ) {
                alt93=1;
            }
            switch (alt93) {
                case 1 :
                    // InternalMnc.g:3796:4: otherlv_6= '(' otherlv_7= 'exitAction' ( (lv_exitAction_8_0= ruleAction ) ) otherlv_9= ')'
                    {
                    otherlv_6=(Token)match(input,44,FOLLOW_93); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_6, grammarAccess.getTransitionAccess().getLeftParenthesisKeyword_3_0());
                      			
                    }
                    otherlv_7=(Token)match(input,83,FOLLOW_88); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_7, grammarAccess.getTransitionAccess().getExitActionKeyword_3_1());
                      			
                    }
                    // InternalMnc.g:3804:4: ( (lv_exitAction_8_0= ruleAction ) )
                    // InternalMnc.g:3805:5: (lv_exitAction_8_0= ruleAction )
                    {
                    // InternalMnc.g:3805:5: (lv_exitAction_8_0= ruleAction )
                    // InternalMnc.g:3806:6: lv_exitAction_8_0= ruleAction
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getTransitionAccess().getExitActionActionParserRuleCall_3_2_0());
                      					
                    }
                    pushFollow(FOLLOW_48);
                    lv_exitAction_8_0=ruleAction();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getTransitionRule());
                      						}
                      						set(
                      							current,
                      							"exitAction",
                      							lv_exitAction_8_0,
                      							"com.mncml.dsl.Mnc.Action");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    otherlv_9=(Token)match(input,45,FOLLOW_94); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_9, grammarAccess.getTransitionAccess().getRightParenthesisKeyword_3_3());
                      			
                    }

                    }
                    break;

            }

            otherlv_10=(Token)match(input,84,FOLLOW_95); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_10, grammarAccess.getTransitionAccess().getEqualsSignGreaterThanSignKeyword_4());
              		
            }
            otherlv_11=(Token)match(input,85,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_11, grammarAccess.getTransitionAccess().getNextStateKeyword_5());
              		
            }
            // InternalMnc.g:3836:3: ( (otherlv_12= RULE_ID ) )
            // InternalMnc.g:3837:4: (otherlv_12= RULE_ID )
            {
            // InternalMnc.g:3837:4: (otherlv_12= RULE_ID )
            // InternalMnc.g:3838:5: otherlv_12= RULE_ID
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getTransitionRule());
              					}
              				
            }
            otherlv_12=(Token)match(input,RULE_ID,FOLLOW_96); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					newLeafNode(otherlv_12, grammarAccess.getTransitionAccess().getNextStateOperatingStateCrossReference_6_0());
              				
            }

            }


            }

            // InternalMnc.g:3849:3: (otherlv_13= '(' otherlv_14= 'entryAction' ( (lv_entryAction_15_0= ruleAction ) ) otherlv_16= ')' )?
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( (LA94_0==44) ) {
                alt94=1;
            }
            switch (alt94) {
                case 1 :
                    // InternalMnc.g:3850:4: otherlv_13= '(' otherlv_14= 'entryAction' ( (lv_entryAction_15_0= ruleAction ) ) otherlv_16= ')'
                    {
                    otherlv_13=(Token)match(input,44,FOLLOW_97); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_13, grammarAccess.getTransitionAccess().getLeftParenthesisKeyword_7_0());
                      			
                    }
                    otherlv_14=(Token)match(input,86,FOLLOW_88); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_14, grammarAccess.getTransitionAccess().getEntryActionKeyword_7_1());
                      			
                    }
                    // InternalMnc.g:3858:4: ( (lv_entryAction_15_0= ruleAction ) )
                    // InternalMnc.g:3859:5: (lv_entryAction_15_0= ruleAction )
                    {
                    // InternalMnc.g:3859:5: (lv_entryAction_15_0= ruleAction )
                    // InternalMnc.g:3860:6: lv_entryAction_15_0= ruleAction
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getTransitionAccess().getEntryActionActionParserRuleCall_7_2_0());
                      					
                    }
                    pushFollow(FOLLOW_48);
                    lv_entryAction_15_0=ruleAction();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getTransitionRule());
                      						}
                      						set(
                      							current,
                      							"entryAction",
                      							lv_entryAction_15_0,
                      							"com.mncml.dsl.Mnc.Action");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    otherlv_16=(Token)match(input,45,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_16, grammarAccess.getTransitionAccess().getRightParenthesisKeyword_7_3());
                      			
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleTransition"


    // $ANTLR start "entryRuleParameterTranslation"
    // InternalMnc.g:3886:1: entryRuleParameterTranslation returns [EObject current=null] : iv_ruleParameterTranslation= ruleParameterTranslation EOF ;
    public final EObject entryRuleParameterTranslation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParameterTranslation = null;


        try {
            // InternalMnc.g:3886:61: (iv_ruleParameterTranslation= ruleParameterTranslation EOF )
            // InternalMnc.g:3887:2: iv_ruleParameterTranslation= ruleParameterTranslation EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getParameterTranslationRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleParameterTranslation=ruleParameterTranslation();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleParameterTranslation; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleParameterTranslation"


    // $ANTLR start "ruleParameterTranslation"
    // InternalMnc.g:3893:1: ruleParameterTranslation returns [EObject current=null] : ( () otherlv_1= 'inputParameters' otherlv_2= '(' ( ( ruleQualifiedName ) ) (otherlv_4= ',' ( ( ruleQualifiedName ) ) )* otherlv_6= ')' otherlv_7= '=>' otherlv_8= 'translatedParameters' otherlv_9= '(' ( ( ruleQualifiedName ) ) otherlv_11= ')' ) ;
    public final EObject ruleParameterTranslation() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_11=null;


        	enterRule();

        try {
            // InternalMnc.g:3899:2: ( ( () otherlv_1= 'inputParameters' otherlv_2= '(' ( ( ruleQualifiedName ) ) (otherlv_4= ',' ( ( ruleQualifiedName ) ) )* otherlv_6= ')' otherlv_7= '=>' otherlv_8= 'translatedParameters' otherlv_9= '(' ( ( ruleQualifiedName ) ) otherlv_11= ')' ) )
            // InternalMnc.g:3900:2: ( () otherlv_1= 'inputParameters' otherlv_2= '(' ( ( ruleQualifiedName ) ) (otherlv_4= ',' ( ( ruleQualifiedName ) ) )* otherlv_6= ')' otherlv_7= '=>' otherlv_8= 'translatedParameters' otherlv_9= '(' ( ( ruleQualifiedName ) ) otherlv_11= ')' )
            {
            // InternalMnc.g:3900:2: ( () otherlv_1= 'inputParameters' otherlv_2= '(' ( ( ruleQualifiedName ) ) (otherlv_4= ',' ( ( ruleQualifiedName ) ) )* otherlv_6= ')' otherlv_7= '=>' otherlv_8= 'translatedParameters' otherlv_9= '(' ( ( ruleQualifiedName ) ) otherlv_11= ')' )
            // InternalMnc.g:3901:3: () otherlv_1= 'inputParameters' otherlv_2= '(' ( ( ruleQualifiedName ) ) (otherlv_4= ',' ( ( ruleQualifiedName ) ) )* otherlv_6= ')' otherlv_7= '=>' otherlv_8= 'translatedParameters' otherlv_9= '(' ( ( ruleQualifiedName ) ) otherlv_11= ')'
            {
            // InternalMnc.g:3901:3: ()
            // InternalMnc.g:3902:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getParameterTranslationAccess().getParameterTranslationAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,87,FOLLOW_46); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getParameterTranslationAccess().getInputParametersKeyword_1());
              		
            }
            otherlv_2=(Token)match(input,44,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getParameterTranslationAccess().getLeftParenthesisKeyword_2());
              		
            }
            // InternalMnc.g:3916:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:3917:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:3917:4: ( ruleQualifiedName )
            // InternalMnc.g:3918:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getParameterTranslationRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getParameterTranslationAccess().getInputParametersParameterCrossReference_3_0());
              				
            }
            pushFollow(FOLLOW_64);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:3932:3: (otherlv_4= ',' ( ( ruleQualifiedName ) ) )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( (LA95_0==17) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // InternalMnc.g:3933:4: otherlv_4= ',' ( ( ruleQualifiedName ) )
            	    {
            	    otherlv_4=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(otherlv_4, grammarAccess.getParameterTranslationAccess().getCommaKeyword_4_0());
            	      			
            	    }
            	    // InternalMnc.g:3937:4: ( ( ruleQualifiedName ) )
            	    // InternalMnc.g:3938:5: ( ruleQualifiedName )
            	    {
            	    // InternalMnc.g:3938:5: ( ruleQualifiedName )
            	    // InternalMnc.g:3939:6: ruleQualifiedName
            	    {
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElement(grammarAccess.getParameterTranslationRule());
            	      						}
            	      					
            	    }
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getParameterTranslationAccess().getInputParametersParameterCrossReference_4_1_0());
            	      					
            	    }
            	    pushFollow(FOLLOW_64);
            	    ruleQualifiedName();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop95;
                }
            } while (true);

            otherlv_6=(Token)match(input,45,FOLLOW_94); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_6, grammarAccess.getParameterTranslationAccess().getRightParenthesisKeyword_5());
              		
            }
            otherlv_7=(Token)match(input,84,FOLLOW_98); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_7, grammarAccess.getParameterTranslationAccess().getEqualsSignGreaterThanSignKeyword_6());
              		
            }
            otherlv_8=(Token)match(input,88,FOLLOW_46); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_8, grammarAccess.getParameterTranslationAccess().getTranslatedParametersKeyword_7());
              		
            }
            otherlv_9=(Token)match(input,44,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_9, grammarAccess.getParameterTranslationAccess().getLeftParenthesisKeyword_8());
              		
            }
            // InternalMnc.g:3970:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:3971:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:3971:4: ( ruleQualifiedName )
            // InternalMnc.g:3972:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getParameterTranslationRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getParameterTranslationAccess().getTranslatedParametersParameterCrossReference_9_0());
              				
            }
            pushFollow(FOLLOW_48);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_11=(Token)match(input,45,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_11, grammarAccess.getParameterTranslationAccess().getRightParenthesisKeyword_10());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleParameterTranslation"


    // $ANTLR start "entryRuleEventBlock"
    // InternalMnc.g:3994:1: entryRuleEventBlock returns [EObject current=null] : iv_ruleEventBlock= ruleEventBlock EOF ;
    public final EObject entryRuleEventBlock() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEventBlock = null;


        try {
            // InternalMnc.g:3994:51: (iv_ruleEventBlock= ruleEventBlock EOF )
            // InternalMnc.g:3995:2: iv_ruleEventBlock= ruleEventBlock EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEventBlockRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleEventBlock=ruleEventBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEventBlock; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleEventBlock"


    // $ANTLR start "ruleEventBlock"
    // InternalMnc.g:4001:1: ruleEventBlock returns [EObject current=null] : ( () otherlv_1= 'Event' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}' ) ;
    public final EObject ruleEventBlock() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_6=null;
        EObject lv_action_4_0 = null;

        EObject lv_validationRules_5_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:4007:2: ( ( () otherlv_1= 'Event' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}' ) )
            // InternalMnc.g:4008:2: ( () otherlv_1= 'Event' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}' )
            {
            // InternalMnc.g:4008:2: ( () otherlv_1= 'Event' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}' )
            // InternalMnc.g:4009:3: () otherlv_1= 'Event' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}'
            {
            // InternalMnc.g:4009:3: ()
            // InternalMnc.g:4010:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getEventBlockAccess().getEventBlockAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,89,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getEventBlockAccess().getEventKeyword_1());
              		
            }
            // InternalMnc.g:4020:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:4021:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:4021:4: ( ruleQualifiedName )
            // InternalMnc.g:4022:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getEventBlockRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getEventBlockAccess().getEventEventCrossReference_2_0());
              				
            }
            pushFollow(FOLLOW_26);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_3=(Token)match(input,26,FOLLOW_99); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getEventBlockAccess().getLeftCurlyBracketKeyword_3());
              		
            }
            // InternalMnc.g:4040:3: ( (lv_action_4_0= ruleAction ) )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==31) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // InternalMnc.g:4041:4: (lv_action_4_0= ruleAction )
                    {
                    // InternalMnc.g:4041:4: (lv_action_4_0= ruleAction )
                    // InternalMnc.g:4042:5: lv_action_4_0= ruleAction
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getEventBlockAccess().getActionActionParserRuleCall_4_0());
                      				
                    }
                    pushFollow(FOLLOW_100);
                    lv_action_4_0=ruleAction();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getEventBlockRule());
                      					}
                      					set(
                      						current,
                      						"action",
                      						lv_action_4_0,
                      						"com.mncml.dsl.Mnc.Action");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:4059:3: ( (lv_validationRules_5_0= ruleValidation ) )*
            loop97:
            do {
                int alt97=2;
                int LA97_0 = input.LA(1);

                if ( (LA97_0==78) ) {
                    alt97=1;
                }


                switch (alt97) {
            	case 1 :
            	    // InternalMnc.g:4060:4: (lv_validationRules_5_0= ruleValidation )
            	    {
            	    // InternalMnc.g:4060:4: (lv_validationRules_5_0= ruleValidation )
            	    // InternalMnc.g:4061:5: lv_validationRules_5_0= ruleValidation
            	    {
            	    if ( state.backtracking==0 ) {

            	      					newCompositeNode(grammarAccess.getEventBlockAccess().getValidationRulesValidationParserRuleCall_5_0());
            	      				
            	    }
            	    pushFollow(FOLLOW_100);
            	    lv_validationRules_5_0=ruleValidation();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					if (current==null) {
            	      						current = createModelElementForParent(grammarAccess.getEventBlockRule());
            	      					}
            	      					add(
            	      						current,
            	      						"validationRules",
            	      						lv_validationRules_5_0,
            	      						"com.mncml.dsl.Mnc.Validation");
            	      					afterParserOrEnumRuleCall();
            	      				
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);

            otherlv_6=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_6, grammarAccess.getEventBlockAccess().getRightCurlyBracketKeyword_6());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleEventBlock"


    // $ANTLR start "entryRuleAlarmBlock"
    // InternalMnc.g:4086:1: entryRuleAlarmBlock returns [EObject current=null] : iv_ruleAlarmBlock= ruleAlarmBlock EOF ;
    public final EObject entryRuleAlarmBlock() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAlarmBlock = null;


        try {
            // InternalMnc.g:4086:51: (iv_ruleAlarmBlock= ruleAlarmBlock EOF )
            // InternalMnc.g:4087:2: iv_ruleAlarmBlock= ruleAlarmBlock EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAlarmBlockRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleAlarmBlock=ruleAlarmBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAlarmBlock; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleAlarmBlock"


    // $ANTLR start "ruleAlarmBlock"
    // InternalMnc.g:4093:1: ruleAlarmBlock returns [EObject current=null] : ( () otherlv_1= 'Alarm' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}' ) ;
    public final EObject ruleAlarmBlock() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_6=null;
        EObject lv_action_4_0 = null;

        EObject lv_validationRules_5_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:4099:2: ( ( () otherlv_1= 'Alarm' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}' ) )
            // InternalMnc.g:4100:2: ( () otherlv_1= 'Alarm' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}' )
            {
            // InternalMnc.g:4100:2: ( () otherlv_1= 'Alarm' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}' )
            // InternalMnc.g:4101:3: () otherlv_1= 'Alarm' ( ( ruleQualifiedName ) ) otherlv_3= '{' ( (lv_action_4_0= ruleAction ) )? ( (lv_validationRules_5_0= ruleValidation ) )* otherlv_6= '}'
            {
            // InternalMnc.g:4101:3: ()
            // InternalMnc.g:4102:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getAlarmBlockAccess().getAlarmBlockAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,90,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getAlarmBlockAccess().getAlarmKeyword_1());
              		
            }
            // InternalMnc.g:4112:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:4113:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:4113:4: ( ruleQualifiedName )
            // InternalMnc.g:4114:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getAlarmBlockRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAlarmBlockAccess().getAlarmAlarmCrossReference_2_0());
              				
            }
            pushFollow(FOLLOW_26);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            otherlv_3=(Token)match(input,26,FOLLOW_99); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getAlarmBlockAccess().getLeftCurlyBracketKeyword_3());
              		
            }
            // InternalMnc.g:4132:3: ( (lv_action_4_0= ruleAction ) )?
            int alt98=2;
            int LA98_0 = input.LA(1);

            if ( (LA98_0==31) ) {
                alt98=1;
            }
            switch (alt98) {
                case 1 :
                    // InternalMnc.g:4133:4: (lv_action_4_0= ruleAction )
                    {
                    // InternalMnc.g:4133:4: (lv_action_4_0= ruleAction )
                    // InternalMnc.g:4134:5: lv_action_4_0= ruleAction
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getAlarmBlockAccess().getActionActionParserRuleCall_4_0());
                      				
                    }
                    pushFollow(FOLLOW_100);
                    lv_action_4_0=ruleAction();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getAlarmBlockRule());
                      					}
                      					set(
                      						current,
                      						"action",
                      						lv_action_4_0,
                      						"com.mncml.dsl.Mnc.Action");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:4151:3: ( (lv_validationRules_5_0= ruleValidation ) )*
            loop99:
            do {
                int alt99=2;
                int LA99_0 = input.LA(1);

                if ( (LA99_0==78) ) {
                    alt99=1;
                }


                switch (alt99) {
            	case 1 :
            	    // InternalMnc.g:4152:4: (lv_validationRules_5_0= ruleValidation )
            	    {
            	    // InternalMnc.g:4152:4: (lv_validationRules_5_0= ruleValidation )
            	    // InternalMnc.g:4153:5: lv_validationRules_5_0= ruleValidation
            	    {
            	    if ( state.backtracking==0 ) {

            	      					newCompositeNode(grammarAccess.getAlarmBlockAccess().getValidationRulesValidationParserRuleCall_5_0());
            	      				
            	    }
            	    pushFollow(FOLLOW_100);
            	    lv_validationRules_5_0=ruleValidation();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					if (current==null) {
            	      						current = createModelElementForParent(grammarAccess.getAlarmBlockRule());
            	      					}
            	      					add(
            	      						current,
            	      						"validationRules",
            	      						lv_validationRules_5_0,
            	      						"com.mncml.dsl.Mnc.Validation");
            	      					afterParserOrEnumRuleCall();
            	      				
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);

            otherlv_6=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_6, grammarAccess.getAlarmBlockAccess().getRightCurlyBracketKeyword_6());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleAlarmBlock"


    // $ANTLR start "entryRuleDataPointBlock"
    // InternalMnc.g:4178:1: entryRuleDataPointBlock returns [EObject current=null] : iv_ruleDataPointBlock= ruleDataPointBlock EOF ;
    public final EObject entryRuleDataPointBlock() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataPointBlock = null;


        try {
            // InternalMnc.g:4178:55: (iv_ruleDataPointBlock= ruleDataPointBlock EOF )
            // InternalMnc.g:4179:2: iv_ruleDataPointBlock= ruleDataPointBlock EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDataPointBlockRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleDataPointBlock=ruleDataPointBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDataPointBlock; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDataPointBlock"


    // $ANTLR start "ruleDataPointBlock"
    // InternalMnc.g:4185:1: ruleDataPointBlock returns [EObject current=null] : ( () otherlv_1= 'DataPoint' ( ( ruleQualifiedName ) ) (otherlv_3= ',' ( ( ruleQualifiedName ) ) )* otherlv_5= '{' ( (lv_action_6_0= ruleAction ) )? ( (lv_validationRules_7_0= ruleValidation ) )* otherlv_8= '}' ) ;
    public final EObject ruleDataPointBlock() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_8=null;
        EObject lv_action_6_0 = null;

        EObject lv_validationRules_7_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:4191:2: ( ( () otherlv_1= 'DataPoint' ( ( ruleQualifiedName ) ) (otherlv_3= ',' ( ( ruleQualifiedName ) ) )* otherlv_5= '{' ( (lv_action_6_0= ruleAction ) )? ( (lv_validationRules_7_0= ruleValidation ) )* otherlv_8= '}' ) )
            // InternalMnc.g:4192:2: ( () otherlv_1= 'DataPoint' ( ( ruleQualifiedName ) ) (otherlv_3= ',' ( ( ruleQualifiedName ) ) )* otherlv_5= '{' ( (lv_action_6_0= ruleAction ) )? ( (lv_validationRules_7_0= ruleValidation ) )* otherlv_8= '}' )
            {
            // InternalMnc.g:4192:2: ( () otherlv_1= 'DataPoint' ( ( ruleQualifiedName ) ) (otherlv_3= ',' ( ( ruleQualifiedName ) ) )* otherlv_5= '{' ( (lv_action_6_0= ruleAction ) )? ( (lv_validationRules_7_0= ruleValidation ) )* otherlv_8= '}' )
            // InternalMnc.g:4193:3: () otherlv_1= 'DataPoint' ( ( ruleQualifiedName ) ) (otherlv_3= ',' ( ( ruleQualifiedName ) ) )* otherlv_5= '{' ( (lv_action_6_0= ruleAction ) )? ( (lv_validationRules_7_0= ruleValidation ) )* otherlv_8= '}'
            {
            // InternalMnc.g:4193:3: ()
            // InternalMnc.g:4194:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getDataPointBlockAccess().getDataPointBlockAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,91,FOLLOW_7); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getDataPointBlockAccess().getDataPointKeyword_1());
              		
            }
            // InternalMnc.g:4204:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:4205:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:4205:4: ( ruleQualifiedName )
            // InternalMnc.g:4206:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getDataPointBlockRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDataPointBlockAccess().getDataPointDataPointCrossReference_2_0());
              				
            }
            pushFollow(FOLLOW_54);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:4220:3: (otherlv_3= ',' ( ( ruleQualifiedName ) ) )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( (LA100_0==17) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // InternalMnc.g:4221:4: otherlv_3= ',' ( ( ruleQualifiedName ) )
            	    {
            	    otherlv_3=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(otherlv_3, grammarAccess.getDataPointBlockAccess().getCommaKeyword_3_0());
            	      			
            	    }
            	    // InternalMnc.g:4225:4: ( ( ruleQualifiedName ) )
            	    // InternalMnc.g:4226:5: ( ruleQualifiedName )
            	    {
            	    // InternalMnc.g:4226:5: ( ruleQualifiedName )
            	    // InternalMnc.g:4227:6: ruleQualifiedName
            	    {
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElement(grammarAccess.getDataPointBlockRule());
            	      						}
            	      					
            	    }
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getDataPointBlockAccess().getDataPointDataPointCrossReference_3_1_0());
            	      					
            	    }
            	    pushFollow(FOLLOW_54);
            	    ruleQualifiedName();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop100;
                }
            } while (true);

            otherlv_5=(Token)match(input,26,FOLLOW_99); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_5, grammarAccess.getDataPointBlockAccess().getLeftCurlyBracketKeyword_4());
              		
            }
            // InternalMnc.g:4246:3: ( (lv_action_6_0= ruleAction ) )?
            int alt101=2;
            int LA101_0 = input.LA(1);

            if ( (LA101_0==31) ) {
                alt101=1;
            }
            switch (alt101) {
                case 1 :
                    // InternalMnc.g:4247:4: (lv_action_6_0= ruleAction )
                    {
                    // InternalMnc.g:4247:4: (lv_action_6_0= ruleAction )
                    // InternalMnc.g:4248:5: lv_action_6_0= ruleAction
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getDataPointBlockAccess().getActionActionParserRuleCall_5_0());
                      				
                    }
                    pushFollow(FOLLOW_100);
                    lv_action_6_0=ruleAction();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getDataPointBlockRule());
                      					}
                      					set(
                      						current,
                      						"action",
                      						lv_action_6_0,
                      						"com.mncml.dsl.Mnc.Action");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalMnc.g:4265:3: ( (lv_validationRules_7_0= ruleValidation ) )*
            loop102:
            do {
                int alt102=2;
                int LA102_0 = input.LA(1);

                if ( (LA102_0==78) ) {
                    alt102=1;
                }


                switch (alt102) {
            	case 1 :
            	    // InternalMnc.g:4266:4: (lv_validationRules_7_0= ruleValidation )
            	    {
            	    // InternalMnc.g:4266:4: (lv_validationRules_7_0= ruleValidation )
            	    // InternalMnc.g:4267:5: lv_validationRules_7_0= ruleValidation
            	    {
            	    if ( state.backtracking==0 ) {

            	      					newCompositeNode(grammarAccess.getDataPointBlockAccess().getValidationRulesValidationParserRuleCall_6_0());
            	      				
            	    }
            	    pushFollow(FOLLOW_100);
            	    lv_validationRules_7_0=ruleValidation();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					if (current==null) {
            	      						current = createModelElementForParent(grammarAccess.getDataPointBlockRule());
            	      					}
            	      					add(
            	      						current,
            	      						"validationRules",
            	      						lv_validationRules_7_0,
            	      						"com.mncml.dsl.Mnc.Validation");
            	      					afterParserOrEnumRuleCall();
            	      				
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop102;
                }
            } while (true);

            otherlv_8=(Token)match(input,30,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_8, grammarAccess.getDataPointBlockAccess().getRightCurlyBracketKeyword_7());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleDataPointBlock"


    // $ANTLR start "entryRuleAddress"
    // InternalMnc.g:4292:1: entryRuleAddress returns [EObject current=null] : iv_ruleAddress= ruleAddress EOF ;
    public final EObject entryRuleAddress() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddress = null;


        try {
            // InternalMnc.g:4292:48: (iv_ruleAddress= ruleAddress EOF )
            // InternalMnc.g:4293:2: iv_ruleAddress= ruleAddress EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAddressRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleAddress=ruleAddress();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAddress; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleAddress"


    // $ANTLR start "ruleAddress"
    // InternalMnc.g:4299:1: ruleAddress returns [EObject current=null] : ( () otherlv_1= 'IPaddress' otherlv_2= ':' ( (lv_ipaddress_3_0= RULE_ADDRESSFORMAT ) ) ) ;
    public final EObject ruleAddress() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_ipaddress_3_0=null;


        	enterRule();

        try {
            // InternalMnc.g:4305:2: ( ( () otherlv_1= 'IPaddress' otherlv_2= ':' ( (lv_ipaddress_3_0= RULE_ADDRESSFORMAT ) ) ) )
            // InternalMnc.g:4306:2: ( () otherlv_1= 'IPaddress' otherlv_2= ':' ( (lv_ipaddress_3_0= RULE_ADDRESSFORMAT ) ) )
            {
            // InternalMnc.g:4306:2: ( () otherlv_1= 'IPaddress' otherlv_2= ':' ( (lv_ipaddress_3_0= RULE_ADDRESSFORMAT ) ) )
            // InternalMnc.g:4307:3: () otherlv_1= 'IPaddress' otherlv_2= ':' ( (lv_ipaddress_3_0= RULE_ADDRESSFORMAT ) )
            {
            // InternalMnc.g:4307:3: ()
            // InternalMnc.g:4308:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getAddressAccess().getAddressAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,92,FOLLOW_22); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getAddressAccess().getIPaddressKeyword_1());
              		
            }
            otherlv_2=(Token)match(input,23,FOLLOW_101); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getAddressAccess().getColonKeyword_2());
              		
            }
            // InternalMnc.g:4322:3: ( (lv_ipaddress_3_0= RULE_ADDRESSFORMAT ) )
            // InternalMnc.g:4323:4: (lv_ipaddress_3_0= RULE_ADDRESSFORMAT )
            {
            // InternalMnc.g:4323:4: (lv_ipaddress_3_0= RULE_ADDRESSFORMAT )
            // InternalMnc.g:4324:5: lv_ipaddress_3_0= RULE_ADDRESSFORMAT
            {
            lv_ipaddress_3_0=(Token)match(input,RULE_ADDRESSFORMAT,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					newLeafNode(lv_ipaddress_3_0, grammarAccess.getAddressAccess().getIpaddressADDRESSFORMATTerminalRuleCall_3_0());
              				
            }
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getAddressRule());
              					}
              					setWithLastConsumed(
              						current,
              						"ipaddress",
              						lv_ipaddress_3_0,
              						"com.mncml.dsl.Mnc.ADDRESSFORMAT");
              				
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleAddress"


    // $ANTLR start "entryRulePort"
    // InternalMnc.g:4344:1: entryRulePort returns [EObject current=null] : iv_rulePort= rulePort EOF ;
    public final EObject entryRulePort() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePort = null;


        try {
            // InternalMnc.g:4344:45: (iv_rulePort= rulePort EOF )
            // InternalMnc.g:4345:2: iv_rulePort= rulePort EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPortRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_rulePort=rulePort();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePort; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRulePort"


    // $ANTLR start "rulePort"
    // InternalMnc.g:4351:1: rulePort returns [EObject current=null] : ( () otherlv_1= 'port' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleEInt ) ) )? ) ;
    public final EObject rulePort() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        AntlrDatatypeRuleToken lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:4357:2: ( ( () otherlv_1= 'port' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleEInt ) ) )? ) )
            // InternalMnc.g:4358:2: ( () otherlv_1= 'port' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleEInt ) ) )? )
            {
            // InternalMnc.g:4358:2: ( () otherlv_1= 'port' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleEInt ) ) )? )
            // InternalMnc.g:4359:3: () otherlv_1= 'port' ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleEInt ) ) )?
            {
            // InternalMnc.g:4359:3: ()
            // InternalMnc.g:4360:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getPortAccess().getPortAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,93,FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getPortAccess().getPortKeyword_1());
              		
            }
            // InternalMnc.g:4370:3: ( (lv_name_2_0= ruleEString ) )
            // InternalMnc.g:4371:4: (lv_name_2_0= ruleEString )
            {
            // InternalMnc.g:4371:4: (lv_name_2_0= ruleEString )
            // InternalMnc.g:4372:5: lv_name_2_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getPortAccess().getNameEStringParserRuleCall_2_0());
              				
            }
            pushFollow(FOLLOW_102);
            lv_name_2_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getPortRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_2_0,
              						"com.dml.dsl.Dml.EString");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:4389:3: (otherlv_3= '=' ( (lv_value_4_0= ruleEInt ) ) )?
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==21) ) {
                alt103=1;
            }
            switch (alt103) {
                case 1 :
                    // InternalMnc.g:4390:4: otherlv_3= '=' ( (lv_value_4_0= ruleEInt ) )
                    {
                    otherlv_3=(Token)match(input,21,FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_3, grammarAccess.getPortAccess().getEqualsSignKeyword_3_0());
                      			
                    }
                    // InternalMnc.g:4394:4: ( (lv_value_4_0= ruleEInt ) )
                    // InternalMnc.g:4395:5: (lv_value_4_0= ruleEInt )
                    {
                    // InternalMnc.g:4395:5: (lv_value_4_0= ruleEInt )
                    // InternalMnc.g:4396:6: lv_value_4_0= ruleEInt
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getPortAccess().getValueEIntParserRuleCall_3_1_0());
                      					
                    }
                    pushFollow(FOLLOW_2);
                    lv_value_4_0=ruleEInt();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getPortRule());
                      						}
                      						set(
                      							current,
                      							"value",
                      							lv_value_4_0,
                      							"com.dml.dsl.Dml.EInt");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "rulePort"


    // $ANTLR start "entryRuleDataModel"
    // InternalMnc.g:4418:1: entryRuleDataModel returns [EObject current=null] : iv_ruleDataModel= ruleDataModel EOF ;
    public final EObject entryRuleDataModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataModel = null;


        try {
            // InternalMnc.g:4418:50: (iv_ruleDataModel= ruleDataModel EOF )
            // InternalMnc.g:4419:2: iv_ruleDataModel= ruleDataModel EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDataModelRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleDataModel=ruleDataModel();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDataModel; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:4425:1: ruleDataModel returns [EObject current=null] : ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) ) ;
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
            // InternalMnc.g:4431:2: ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) ) )
            // InternalMnc.g:4432:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) )
            {
            // InternalMnc.g:4432:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) ) )
            // InternalMnc.g:4433:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) )
            {
            // InternalMnc.g:4433:3: ( ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?) )
            // InternalMnc.g:4434:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?)
            {
            getUnorderedGroupHelper().enter(grammarAccess.getDataModelAccess().getUnorderedGroup());
            // InternalMnc.g:4437:4: ( ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?)
            // InternalMnc.g:4438:5: ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+ {...}?
            {
            // InternalMnc.g:4438:5: ( ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) ) )+
            int cnt108=0;
            loop108:
            do {
                int alt108=3;
                int LA108_0 = input.LA(1);

                if ( LA108_0 == 94 && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
                    alt108=1;
                }
                else if ( ( LA108_0 == 30 || LA108_0 == 96 ) && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
                    alt108=2;
                }


                switch (alt108) {
            	case 1 :
            	    // InternalMnc.g:4439:3: ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) )
            	    {
            	    // InternalMnc.g:4439:3: ({...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) ) )
            	    // InternalMnc.g:4440:4: {...}? => ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0)");
            	    }
            	    // InternalMnc.g:4440:103: ( ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) ) )
            	    // InternalMnc.g:4441:5: ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0);
            	    // InternalMnc.g:4444:8: ({...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? ) )
            	    // InternalMnc.g:4444:9: {...}? => (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleDataModel", "true");
            	    }
            	    // InternalMnc.g:4444:18: (otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )? )
            	    // InternalMnc.g:4444:19: otherlv_1= 'DataModel' ( (lv_name_2_0= ruleEString ) ) otherlv_3= '{' (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )?
            	    {
            	    otherlv_1=(Token)match(input,94,FOLLOW_4); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      								newLeafNode(otherlv_1, grammarAccess.getDataModelAccess().getDataModelKeyword_0_0());
            	      							
            	    }
            	    // InternalMnc.g:4448:8: ( (lv_name_2_0= ruleEString ) )
            	    // InternalMnc.g:4449:9: (lv_name_2_0= ruleEString )
            	    {
            	    // InternalMnc.g:4449:9: (lv_name_2_0= ruleEString )
            	    // InternalMnc.g:4450:10: lv_name_2_0= ruleEString
            	    {
            	    if ( state.backtracking==0 ) {

            	      										newCompositeNode(grammarAccess.getDataModelAccess().getNameEStringParserRuleCall_0_1_0());
            	      									
            	    }
            	    pushFollow(FOLLOW_26);
            	    lv_name_2_0=ruleEString();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

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


            	    }

            	    otherlv_3=(Token)match(input,26,FOLLOW_103); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      								newLeafNode(otherlv_3, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_2());
            	      							
            	    }
            	    // InternalMnc.g:4471:8: (otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}' )?
            	    int alt105=2;
            	    int LA105_0 = input.LA(1);

            	    if ( (LA105_0==95) ) {
            	        alt105=1;
            	    }
            	    switch (alt105) {
            	        case 1 :
            	            // InternalMnc.g:4472:9: otherlv_4= 'primitives' otherlv_5= '{' ( (lv_primitives_6_0= ruleParameter ) ) (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )* otherlv_9= '}'
            	            {
            	            otherlv_4=(Token)match(input,95,FOLLOW_26); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              									newLeafNode(otherlv_4, grammarAccess.getDataModelAccess().getPrimitivesKeyword_0_3_0());
            	              								
            	            }
            	            otherlv_5=(Token)match(input,26,FOLLOW_12); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              									newLeafNode(otherlv_5, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_3_1());
            	              								
            	            }
            	            // InternalMnc.g:4480:9: ( (lv_primitives_6_0= ruleParameter ) )
            	            // InternalMnc.g:4481:10: (lv_primitives_6_0= ruleParameter )
            	            {
            	            // InternalMnc.g:4481:10: (lv_primitives_6_0= ruleParameter )
            	            // InternalMnc.g:4482:11: lv_primitives_6_0= ruleParameter
            	            {
            	            if ( state.backtracking==0 ) {

            	              											newCompositeNode(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_2_0());
            	              										
            	            }
            	            pushFollow(FOLLOW_30);
            	            lv_primitives_6_0=ruleParameter();

            	            state._fsp--;
            	            if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

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


            	            }

            	            // InternalMnc.g:4499:9: (otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) ) )*
            	            loop104:
            	            do {
            	                int alt104=2;
            	                int LA104_0 = input.LA(1);

            	                if ( (LA104_0==17) ) {
            	                    alt104=1;
            	                }


            	                switch (alt104) {
            	            	case 1 :
            	            	    // InternalMnc.g:4500:10: otherlv_7= ',' ( (lv_primitives_8_0= ruleParameter ) )
            	            	    {
            	            	    otherlv_7=(Token)match(input,17,FOLLOW_12); if (state.failed) return current;
            	            	    if ( state.backtracking==0 ) {

            	            	      										newLeafNode(otherlv_7, grammarAccess.getDataModelAccess().getCommaKeyword_0_3_3_0());
            	            	      									
            	            	    }
            	            	    // InternalMnc.g:4504:10: ( (lv_primitives_8_0= ruleParameter ) )
            	            	    // InternalMnc.g:4505:11: (lv_primitives_8_0= ruleParameter )
            	            	    {
            	            	    // InternalMnc.g:4505:11: (lv_primitives_8_0= ruleParameter )
            	            	    // InternalMnc.g:4506:12: lv_primitives_8_0= ruleParameter
            	            	    {
            	            	    if ( state.backtracking==0 ) {

            	            	      												newCompositeNode(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_3_1_0());
            	            	      											
            	            	    }
            	            	    pushFollow(FOLLOW_30);
            	            	    lv_primitives_8_0=ruleParameter();

            	            	    state._fsp--;
            	            	    if (state.failed) return current;
            	            	    if ( state.backtracking==0 ) {

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


            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop104;
            	                }
            	            } while (true);

            	            otherlv_9=(Token)match(input,30,FOLLOW_104); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              									newLeafNode(otherlv_9, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_0_3_4());
            	              								
            	            }

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
            	    // InternalMnc.g:4535:3: ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) )
            	    {
            	    // InternalMnc.g:4535:3: ({...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) ) )
            	    // InternalMnc.g:4536:4: {...}? => ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1)");
            	    }
            	    // InternalMnc.g:4536:103: ( ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) ) )
            	    // InternalMnc.g:4537:5: ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) )
            	    {
            	    getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1);
            	    // InternalMnc.g:4540:8: ({...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' ) )
            	    // InternalMnc.g:4540:9: {...}? => ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' )
            	    {
            	    if ( !((true)) ) {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        throw new FailedPredicateException(input, "ruleDataModel", "true");
            	    }
            	    // InternalMnc.g:4540:18: ( (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}' )
            	    // InternalMnc.g:4540:19: (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )? otherlv_16= '}'
            	    {
            	    // InternalMnc.g:4540:19: (otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}' )?
            	    int alt107=2;
            	    int LA107_0 = input.LA(1);

            	    if ( (LA107_0==96) ) {
            	        alt107=1;
            	    }
            	    switch (alt107) {
            	        case 1 :
            	            // InternalMnc.g:4541:9: otherlv_10= 'composites' otherlv_11= '{' ( (otherlv_12= RULE_ID ) ) (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )* otherlv_15= '}'
            	            {
            	            otherlv_10=(Token)match(input,96,FOLLOW_26); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              									newLeafNode(otherlv_10, grammarAccess.getDataModelAccess().getCompositesKeyword_1_0_0());
            	              								
            	            }
            	            otherlv_11=(Token)match(input,26,FOLLOW_7); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              									newLeafNode(otherlv_11, grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_1_0_1());
            	              								
            	            }
            	            // InternalMnc.g:4549:9: ( (otherlv_12= RULE_ID ) )
            	            // InternalMnc.g:4550:10: (otherlv_12= RULE_ID )
            	            {
            	            // InternalMnc.g:4550:10: (otherlv_12= RULE_ID )
            	            // InternalMnc.g:4551:11: otherlv_12= RULE_ID
            	            {
            	            if ( state.backtracking==0 ) {

            	              											if (current==null) {
            	              												current = createModelElement(grammarAccess.getDataModelRule());
            	              											}
            	              										
            	            }
            	            otherlv_12=(Token)match(input,RULE_ID,FOLLOW_30); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              											newLeafNode(otherlv_12, grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_2_0());
            	              										
            	            }

            	            }


            	            }

            	            // InternalMnc.g:4562:9: (otherlv_13= ',' ( (otherlv_14= RULE_ID ) ) )*
            	            loop106:
            	            do {
            	                int alt106=2;
            	                int LA106_0 = input.LA(1);

            	                if ( (LA106_0==17) ) {
            	                    alt106=1;
            	                }


            	                switch (alt106) {
            	            	case 1 :
            	            	    // InternalMnc.g:4563:10: otherlv_13= ',' ( (otherlv_14= RULE_ID ) )
            	            	    {
            	            	    otherlv_13=(Token)match(input,17,FOLLOW_7); if (state.failed) return current;
            	            	    if ( state.backtracking==0 ) {

            	            	      										newLeafNode(otherlv_13, grammarAccess.getDataModelAccess().getCommaKeyword_1_0_3_0());
            	            	      									
            	            	    }
            	            	    // InternalMnc.g:4567:10: ( (otherlv_14= RULE_ID ) )
            	            	    // InternalMnc.g:4568:11: (otherlv_14= RULE_ID )
            	            	    {
            	            	    // InternalMnc.g:4568:11: (otherlv_14= RULE_ID )
            	            	    // InternalMnc.g:4569:12: otherlv_14= RULE_ID
            	            	    {
            	            	    if ( state.backtracking==0 ) {

            	            	      												if (current==null) {
            	            	      													current = createModelElement(grammarAccess.getDataModelRule());
            	            	      												}
            	            	      											
            	            	    }
            	            	    otherlv_14=(Token)match(input,RULE_ID,FOLLOW_30); if (state.failed) return current;
            	            	    if ( state.backtracking==0 ) {

            	            	      												newLeafNode(otherlv_14, grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_3_1_0());
            	            	      											
            	            	    }

            	            	    }


            	            	    }


            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop106;
            	                }
            	            } while (true);

            	            otherlv_15=(Token)match(input,30,FOLLOW_45); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              									newLeafNode(otherlv_15, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_0_4());
            	              								
            	            }

            	            }
            	            break;

            	    }

            	    otherlv_16=(Token)match(input,30,FOLLOW_104); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      								newLeafNode(otherlv_16, grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_1());
            	      							
            	    }

            	    }


            	    }

            	    getUnorderedGroupHelper().returnFromSelection(grammarAccess.getDataModelAccess().getUnorderedGroup());

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt108 >= 1 ) break loop108;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(108, input);
                        throw eee;
                }
                cnt108++;
            } while (true);

            if ( ! getUnorderedGroupHelper().canLeave(grammarAccess.getDataModelAccess().getUnorderedGroup()) ) {
                if (state.backtracking>0) {state.failed=true; return current;}
                throw new FailedPredicateException(input, "ruleDataModel", "getUnorderedGroupHelper().canLeave(grammarAccess.getDataModelAccess().getUnorderedGroup())");
            }

            }


            }

            getUnorderedGroupHelper().leave(grammarAccess.getDataModelAccess().getUnorderedGroup());

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleDataModel"


    // $ANTLR start "entryRuleParameter"
    // InternalMnc.g:4607:1: entryRuleParameter returns [EObject current=null] : iv_ruleParameter= ruleParameter EOF ;
    public final EObject entryRuleParameter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParameter = null;


        try {
            // InternalMnc.g:4607:50: (iv_ruleParameter= ruleParameter EOF )
            // InternalMnc.g:4608:2: iv_ruleParameter= ruleParameter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getParameterRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleParameter=ruleParameter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleParameter; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:4614:1: ruleParameter returns [EObject current=null] : (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType ) ;
    public final EObject ruleParameter() throws RecognitionException {
        EObject current = null;

        EObject this_SimpleType_0 = null;

        EObject this_AbstractType_1 = null;

        EObject this_ArrayType_2 = null;



        	enterRule();

        try {
            // InternalMnc.g:4620:2: ( (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType ) )
            // InternalMnc.g:4621:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )
            {
            // InternalMnc.g:4621:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )
            int alt109=3;
            alt109 = dfa109.predict(input);
            switch (alt109) {
                case 1 :
                    // InternalMnc.g:4622:3: this_SimpleType_0= ruleSimpleType
                    {
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getParameterAccess().getSimpleTypeParserRuleCall_0());
                      		
                    }
                    pushFollow(FOLLOW_2);
                    this_SimpleType_0=ruleSimpleType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_SimpleType_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalMnc.g:4631:3: this_AbstractType_1= ruleAbstractType
                    {
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getParameterAccess().getAbstractTypeParserRuleCall_1());
                      		
                    }
                    pushFollow(FOLLOW_2);
                    this_AbstractType_1=ruleAbstractType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AbstractType_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalMnc.g:4640:3: this_ArrayType_2= ruleArrayType
                    {
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getParameterAccess().getArrayTypeParserRuleCall_2());
                      		
                    }
                    pushFollow(FOLLOW_2);
                    this_ArrayType_2=ruleArrayType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ArrayType_2;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleParameter"


    // $ANTLR start "entryRuleQualifiedName"
    // InternalMnc.g:4652:1: entryRuleQualifiedName returns [String current=null] : iv_ruleQualifiedName= ruleQualifiedName EOF ;
    public final String entryRuleQualifiedName() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQualifiedName = null;


        try {
            // InternalMnc.g:4652:53: (iv_ruleQualifiedName= ruleQualifiedName EOF )
            // InternalMnc.g:4653:2: iv_ruleQualifiedName= ruleQualifiedName EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getQualifiedNameRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleQualifiedName=ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleQualifiedName.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:4659:1: ruleQualifiedName returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQualifiedName() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;


        	enterRule();

        try {
            // InternalMnc.g:4665:2: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // InternalMnc.g:4666:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // InternalMnc.g:4666:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // InternalMnc.g:4667:3: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_105); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_ID_0);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ID_0, grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_0());
              		
            }
            // InternalMnc.g:4674:3: (kw= '.' this_ID_2= RULE_ID )*
            loop110:
            do {
                int alt110=2;
                int LA110_0 = input.LA(1);

                if ( (LA110_0==97) ) {
                    alt110=1;
                }


                switch (alt110) {
            	case 1 :
            	    // InternalMnc.g:4675:4: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,97,FOLLOW_7); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(kw);
            	      				newLeafNode(kw, grammarAccess.getQualifiedNameAccess().getFullStopKeyword_1_0());
            	      			
            	    }
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_105); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ID_2);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ID_2, grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_1_1());
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    break loop110;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleQualifiedName"


    // $ANTLR start "entryRuleSimpleType"
    // InternalMnc.g:4692:1: entryRuleSimpleType returns [EObject current=null] : iv_ruleSimpleType= ruleSimpleType EOF ;
    public final EObject entryRuleSimpleType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleType = null;


        try {
            // InternalMnc.g:4692:51: (iv_ruleSimpleType= ruleSimpleType EOF )
            // InternalMnc.g:4693:2: iv_ruleSimpleType= ruleSimpleType EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSimpleTypeRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleSimpleType=ruleSimpleType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSimpleType; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:4699:1: ruleSimpleType returns [EObject current=null] : ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? ) ;
    public final EObject ruleSimpleType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        Enumerator lv_type_1_0 = null;

        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:4705:2: ( ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? ) )
            // InternalMnc.g:4706:2: ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? )
            {
            // InternalMnc.g:4706:2: ( () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )? )
            // InternalMnc.g:4707:3: () ( (lv_type_1_0= rulePrimitiveValueType ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )?
            {
            // InternalMnc.g:4707:3: ()
            // InternalMnc.g:4708:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getSimpleTypeAccess().getSimpleTypeAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:4714:3: ( (lv_type_1_0= rulePrimitiveValueType ) )
            // InternalMnc.g:4715:4: (lv_type_1_0= rulePrimitiveValueType )
            {
            // InternalMnc.g:4715:4: (lv_type_1_0= rulePrimitiveValueType )
            // InternalMnc.g:4716:5: lv_type_1_0= rulePrimitiveValueType
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSimpleTypeAccess().getTypePrimitiveValueTypeEnumRuleCall_1_0());
              				
            }
            pushFollow(FOLLOW_4);
            lv_type_1_0=rulePrimitiveValueType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

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


            }

            // InternalMnc.g:4733:3: ( (lv_name_2_0= ruleEString ) )
            // InternalMnc.g:4734:4: (lv_name_2_0= ruleEString )
            {
            // InternalMnc.g:4734:4: (lv_name_2_0= ruleEString )
            // InternalMnc.g:4735:5: lv_name_2_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSimpleTypeAccess().getNameEStringParserRuleCall_2_0());
              				
            }
            pushFollow(FOLLOW_102);
            lv_name_2_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

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


            }

            // InternalMnc.g:4752:3: (otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) ) )?
            int alt111=2;
            int LA111_0 = input.LA(1);

            if ( (LA111_0==21) ) {
                alt111=1;
            }
            switch (alt111) {
                case 1 :
                    // InternalMnc.g:4753:4: otherlv_3= '=' ( (lv_value_4_0= rulePrimitiveValue ) )
                    {
                    otherlv_3=(Token)match(input,21,FOLLOW_20); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_3, grammarAccess.getSimpleTypeAccess().getEqualsSignKeyword_3_0());
                      			
                    }
                    // InternalMnc.g:4757:4: ( (lv_value_4_0= rulePrimitiveValue ) )
                    // InternalMnc.g:4758:5: (lv_value_4_0= rulePrimitiveValue )
                    {
                    // InternalMnc.g:4758:5: (lv_value_4_0= rulePrimitiveValue )
                    // InternalMnc.g:4759:6: lv_value_4_0= rulePrimitiveValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSimpleTypeAccess().getValuePrimitiveValueParserRuleCall_3_1_0());
                      					
                    }
                    pushFollow(FOLLOW_2);
                    lv_value_4_0=rulePrimitiveValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

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


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleSimpleType"


    // $ANTLR start "entryRuleAbstractType"
    // InternalMnc.g:4781:1: entryRuleAbstractType returns [EObject current=null] : iv_ruleAbstractType= ruleAbstractType EOF ;
    public final EObject entryRuleAbstractType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractType = null;


        try {
            // InternalMnc.g:4781:53: (iv_ruleAbstractType= ruleAbstractType EOF )
            // InternalMnc.g:4782:2: iv_ruleAbstractType= ruleAbstractType EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAbstractTypeRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleAbstractType=ruleAbstractType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAbstractType; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:4788:1: ruleAbstractType returns [EObject current=null] : ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? ) ;
    public final EObject ruleAbstractType() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_value_4_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:4794:2: ( ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? ) )
            // InternalMnc.g:4795:2: ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? )
            {
            // InternalMnc.g:4795:2: ( () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )? )
            // InternalMnc.g:4796:3: () ( ( ruleQualifiedName ) ) ( (lv_name_2_0= ruleEString ) ) (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )?
            {
            // InternalMnc.g:4796:3: ()
            // InternalMnc.g:4797:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getAbstractTypeAccess().getAbstractTypeAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:4803:3: ( ( ruleQualifiedName ) )
            // InternalMnc.g:4804:4: ( ruleQualifiedName )
            {
            // InternalMnc.g:4804:4: ( ruleQualifiedName )
            // InternalMnc.g:4805:5: ruleQualifiedName
            {
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getAbstractTypeRule());
              					}
              				
            }
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAbstractTypeAccess().getTypeDataModelCrossReference_1_0());
              				
            }
            pushFollow(FOLLOW_4);
            ruleQualifiedName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalMnc.g:4819:3: ( (lv_name_2_0= ruleEString ) )
            // InternalMnc.g:4820:4: (lv_name_2_0= ruleEString )
            {
            // InternalMnc.g:4820:4: (lv_name_2_0= ruleEString )
            // InternalMnc.g:4821:5: lv_name_2_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAbstractTypeAccess().getNameEStringParserRuleCall_2_0());
              				
            }
            pushFollow(FOLLOW_102);
            lv_name_2_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

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


            }

            // InternalMnc.g:4838:3: (otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) ) )?
            int alt112=2;
            int LA112_0 = input.LA(1);

            if ( (LA112_0==21) ) {
                alt112=1;
            }
            switch (alt112) {
                case 1 :
                    // InternalMnc.g:4839:4: otherlv_3= '=' ( (lv_value_4_0= ruleAbstractObjectValue ) )
                    {
                    otherlv_3=(Token)match(input,21,FOLLOW_20); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_3, grammarAccess.getAbstractTypeAccess().getEqualsSignKeyword_3_0());
                      			
                    }
                    // InternalMnc.g:4843:4: ( (lv_value_4_0= ruleAbstractObjectValue ) )
                    // InternalMnc.g:4844:5: (lv_value_4_0= ruleAbstractObjectValue )
                    {
                    // InternalMnc.g:4844:5: (lv_value_4_0= ruleAbstractObjectValue )
                    // InternalMnc.g:4845:6: lv_value_4_0= ruleAbstractObjectValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getAbstractTypeAccess().getValueAbstractObjectValueParserRuleCall_3_1_0());
                      					
                    }
                    pushFollow(FOLLOW_2);
                    lv_value_4_0=ruleAbstractObjectValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

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


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleAbstractType"


    // $ANTLR start "entryRulePrimitiveValue"
    // InternalMnc.g:4867:1: entryRulePrimitiveValue returns [EObject current=null] : iv_rulePrimitiveValue= rulePrimitiveValue EOF ;
    public final EObject entryRulePrimitiveValue() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimitiveValue = null;


        try {
            // InternalMnc.g:4867:55: (iv_rulePrimitiveValue= rulePrimitiveValue EOF )
            // InternalMnc.g:4868:2: iv_rulePrimitiveValue= rulePrimitiveValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPrimitiveValueRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_rulePrimitiveValue=rulePrimitiveValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePrimitiveValue; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:4874:1: rulePrimitiveValue returns [EObject current=null] : ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue ) ;
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
            // InternalMnc.g:4880:2: ( ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue ) )
            // InternalMnc.g:4881:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )
            {
            // InternalMnc.g:4881:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )
            int alt113=7;
            alt113 = dfa113.predict(input);
            switch (alt113) {
                case 1 :
                    // InternalMnc.g:4882:3: ( () ( (lv_intValue_1_0= ruleEInt ) ) )
                    {
                    // InternalMnc.g:4882:3: ( () ( (lv_intValue_1_0= ruleEInt ) ) )
                    // InternalMnc.g:4883:4: () ( (lv_intValue_1_0= ruleEInt ) )
                    {
                    // InternalMnc.g:4883:4: ()
                    // InternalMnc.g:4884:5: 
                    {
                    if ( state.backtracking==0 ) {

                      					current = forceCreateModelElement(
                      						grammarAccess.getPrimitiveValueAccess().getIntValueAction_0_0(),
                      						current);
                      				
                    }

                    }

                    // InternalMnc.g:4890:4: ( (lv_intValue_1_0= ruleEInt ) )
                    // InternalMnc.g:4891:5: (lv_intValue_1_0= ruleEInt )
                    {
                    // InternalMnc.g:4891:5: (lv_intValue_1_0= ruleEInt )
                    // InternalMnc.g:4892:6: lv_intValue_1_0= ruleEInt
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getPrimitiveValueAccess().getIntValueEIntParserRuleCall_0_1_0());
                      					
                    }
                    pushFollow(FOLLOW_2);
                    lv_intValue_1_0=ruleEInt();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

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


                    }
                    break;
                case 2 :
                    // InternalMnc.g:4911:3: ( () ( (lv_floatValue_3_0= ruleEFloat ) ) )
                    {
                    // InternalMnc.g:4911:3: ( () ( (lv_floatValue_3_0= ruleEFloat ) ) )
                    // InternalMnc.g:4912:4: () ( (lv_floatValue_3_0= ruleEFloat ) )
                    {
                    // InternalMnc.g:4912:4: ()
                    // InternalMnc.g:4913:5: 
                    {
                    if ( state.backtracking==0 ) {

                      					current = forceCreateModelElement(
                      						grammarAccess.getPrimitiveValueAccess().getFloatValueAction_1_0(),
                      						current);
                      				
                    }

                    }

                    // InternalMnc.g:4919:4: ( (lv_floatValue_3_0= ruleEFloat ) )
                    // InternalMnc.g:4920:5: (lv_floatValue_3_0= ruleEFloat )
                    {
                    // InternalMnc.g:4920:5: (lv_floatValue_3_0= ruleEFloat )
                    // InternalMnc.g:4921:6: lv_floatValue_3_0= ruleEFloat
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getPrimitiveValueAccess().getFloatValueEFloatParserRuleCall_1_1_0());
                      					
                    }
                    pushFollow(FOLLOW_2);
                    lv_floatValue_3_0=ruleEFloat();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

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


                    }
                    break;
                case 3 :
                    // InternalMnc.g:4940:3: ( () ( (lv_stringValue_5_0= RULE_STRING ) ) )
                    {
                    // InternalMnc.g:4940:3: ( () ( (lv_stringValue_5_0= RULE_STRING ) ) )
                    // InternalMnc.g:4941:4: () ( (lv_stringValue_5_0= RULE_STRING ) )
                    {
                    // InternalMnc.g:4941:4: ()
                    // InternalMnc.g:4942:5: 
                    {
                    if ( state.backtracking==0 ) {

                      					current = forceCreateModelElement(
                      						grammarAccess.getPrimitiveValueAccess().getStringValueAction_2_0(),
                      						current);
                      				
                    }

                    }

                    // InternalMnc.g:4948:4: ( (lv_stringValue_5_0= RULE_STRING ) )
                    // InternalMnc.g:4949:5: (lv_stringValue_5_0= RULE_STRING )
                    {
                    // InternalMnc.g:4949:5: (lv_stringValue_5_0= RULE_STRING )
                    // InternalMnc.g:4950:6: lv_stringValue_5_0= RULE_STRING
                    {
                    lv_stringValue_5_0=(Token)match(input,RULE_STRING,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						newLeafNode(lv_stringValue_5_0, grammarAccess.getPrimitiveValueAccess().getStringValueSTRINGTerminalRuleCall_2_1_0());
                      					
                    }
                    if ( state.backtracking==0 ) {

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


                    }
                    break;
                case 4 :
                    // InternalMnc.g:4968:3: ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) )
                    {
                    // InternalMnc.g:4968:3: ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) )
                    // InternalMnc.g:4969:4: () ( (lv_boolValue_7_0= ruleEBoolean ) )
                    {
                    // InternalMnc.g:4969:4: ()
                    // InternalMnc.g:4970:5: 
                    {
                    if ( state.backtracking==0 ) {

                      					current = forceCreateModelElement(
                      						grammarAccess.getPrimitiveValueAccess().getBoolValueAction_3_0(),
                      						current);
                      				
                    }

                    }

                    // InternalMnc.g:4976:4: ( (lv_boolValue_7_0= ruleEBoolean ) )
                    // InternalMnc.g:4977:5: (lv_boolValue_7_0= ruleEBoolean )
                    {
                    // InternalMnc.g:4977:5: (lv_boolValue_7_0= ruleEBoolean )
                    // InternalMnc.g:4978:6: lv_boolValue_7_0= ruleEBoolean
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getPrimitiveValueAccess().getBoolValueEBooleanParserRuleCall_3_1_0());
                      					
                    }
                    pushFollow(FOLLOW_2);
                    lv_boolValue_7_0=ruleEBoolean();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

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


                    }
                    break;
                case 5 :
                    // InternalMnc.g:4997:3: ( () ( (lv_dateValue_9_0= ruleEDate ) ) )
                    {
                    // InternalMnc.g:4997:3: ( () ( (lv_dateValue_9_0= ruleEDate ) ) )
                    // InternalMnc.g:4998:4: () ( (lv_dateValue_9_0= ruleEDate ) )
                    {
                    // InternalMnc.g:4998:4: ()
                    // InternalMnc.g:4999:5: 
                    {
                    if ( state.backtracking==0 ) {

                      					current = forceCreateModelElement(
                      						grammarAccess.getPrimitiveValueAccess().getDateValueAction_4_0(),
                      						current);
                      				
                    }

                    }

                    // InternalMnc.g:5005:4: ( (lv_dateValue_9_0= ruleEDate ) )
                    // InternalMnc.g:5006:5: (lv_dateValue_9_0= ruleEDate )
                    {
                    // InternalMnc.g:5006:5: (lv_dateValue_9_0= ruleEDate )
                    // InternalMnc.g:5007:6: lv_dateValue_9_0= ruleEDate
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getPrimitiveValueAccess().getDateValueEDateParserRuleCall_4_1_0());
                      					
                    }
                    pushFollow(FOLLOW_2);
                    lv_dateValue_9_0=ruleEDate();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

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


                    }
                    break;
                case 6 :
                    // InternalMnc.g:5026:3: this_ArrayValues_10= ruleArrayValues
                    {
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getPrimitiveValueAccess().getArrayValuesParserRuleCall_5());
                      		
                    }
                    pushFollow(FOLLOW_2);
                    this_ArrayValues_10=ruleArrayValues();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ArrayValues_10;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 7 :
                    // InternalMnc.g:5035:3: this_AbstractObjectValue_11= ruleAbstractObjectValue
                    {
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getPrimitiveValueAccess().getAbstractObjectValueParserRuleCall_6());
                      		
                    }
                    pushFollow(FOLLOW_2);
                    this_AbstractObjectValue_11=ruleAbstractObjectValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AbstractObjectValue_11;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "rulePrimitiveValue"


    // $ANTLR start "entryRuleAbstractObjectValue"
    // InternalMnc.g:5047:1: entryRuleAbstractObjectValue returns [EObject current=null] : iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF ;
    public final EObject entryRuleAbstractObjectValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractObjectValue = null;


        try {
            // InternalMnc.g:5047:60: (iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF )
            // InternalMnc.g:5048:2: iv_ruleAbstractObjectValue= ruleAbstractObjectValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAbstractObjectValueRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleAbstractObjectValue=ruleAbstractObjectValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAbstractObjectValue; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5054:1: ruleAbstractObjectValue returns [EObject current=null] : ( () ( (lv_abstractValue_1_0= RULE_ID ) ) ) ;
    public final EObject ruleAbstractObjectValue() throws RecognitionException {
        EObject current = null;

        Token lv_abstractValue_1_0=null;


        	enterRule();

        try {
            // InternalMnc.g:5060:2: ( ( () ( (lv_abstractValue_1_0= RULE_ID ) ) ) )
            // InternalMnc.g:5061:2: ( () ( (lv_abstractValue_1_0= RULE_ID ) ) )
            {
            // InternalMnc.g:5061:2: ( () ( (lv_abstractValue_1_0= RULE_ID ) ) )
            // InternalMnc.g:5062:3: () ( (lv_abstractValue_1_0= RULE_ID ) )
            {
            // InternalMnc.g:5062:3: ()
            // InternalMnc.g:5063:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getAbstractObjectValueAccess().getAbstractObjectValueAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:5069:3: ( (lv_abstractValue_1_0= RULE_ID ) )
            // InternalMnc.g:5070:4: (lv_abstractValue_1_0= RULE_ID )
            {
            // InternalMnc.g:5070:4: (lv_abstractValue_1_0= RULE_ID )
            // InternalMnc.g:5071:5: lv_abstractValue_1_0= RULE_ID
            {
            lv_abstractValue_1_0=(Token)match(input,RULE_ID,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					newLeafNode(lv_abstractValue_1_0, grammarAccess.getAbstractObjectValueAccess().getAbstractValueIDTerminalRuleCall_1_0());
              				
            }
            if ( state.backtracking==0 ) {

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


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleAbstractObjectValue"


    // $ANTLR start "entryRuleArrayValues"
    // InternalMnc.g:5091:1: entryRuleArrayValues returns [EObject current=null] : iv_ruleArrayValues= ruleArrayValues EOF ;
    public final EObject entryRuleArrayValues() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayValues = null;


        try {
            // InternalMnc.g:5091:52: (iv_ruleArrayValues= ruleArrayValues EOF )
            // InternalMnc.g:5092:2: iv_ruleArrayValues= ruleArrayValues EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getArrayValuesRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleArrayValues=ruleArrayValues();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleArrayValues; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5098:1: ruleArrayValues returns [EObject current=null] : ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' ) ;
    public final EObject ruleArrayValues() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_values_2_0 = null;

        EObject lv_values_4_0 = null;



        	enterRule();

        try {
            // InternalMnc.g:5104:2: ( ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' ) )
            // InternalMnc.g:5105:2: ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' )
            {
            // InternalMnc.g:5105:2: ( () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']' )
            // InternalMnc.g:5106:3: () otherlv_1= '[' ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )? otherlv_5= ']'
            {
            // InternalMnc.g:5106:3: ()
            // InternalMnc.g:5107:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getArrayValuesAccess().getArrayValuesAction_0(),
              					current);
              			
            }

            }

            otherlv_1=(Token)match(input,16,FOLLOW_106); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_1, grammarAccess.getArrayValuesAccess().getLeftSquareBracketKeyword_1());
              		
            }
            // InternalMnc.g:5117:3: ( ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )* )?
            int alt115=2;
            int LA115_0 = input.LA(1);

            if ( (LA115_0==RULE_ID||(LA115_0>=RULE_STRING && LA115_0<=RULE_INT)||LA115_0==16||(LA115_0>=97 && LA115_0<=100)) ) {
                alt115=1;
            }
            switch (alt115) {
                case 1 :
                    // InternalMnc.g:5118:4: ( (lv_values_2_0= rulePrimitiveValue ) ) (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )*
                    {
                    // InternalMnc.g:5118:4: ( (lv_values_2_0= rulePrimitiveValue ) )
                    // InternalMnc.g:5119:5: (lv_values_2_0= rulePrimitiveValue )
                    {
                    // InternalMnc.g:5119:5: (lv_values_2_0= rulePrimitiveValue )
                    // InternalMnc.g:5120:6: lv_values_2_0= rulePrimitiveValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_0_0());
                      					
                    }
                    pushFollow(FOLLOW_11);
                    lv_values_2_0=rulePrimitiveValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

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


                    }

                    // InternalMnc.g:5137:4: (otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) ) )*
                    loop114:
                    do {
                        int alt114=2;
                        int LA114_0 = input.LA(1);

                        if ( (LA114_0==17) ) {
                            alt114=1;
                        }


                        switch (alt114) {
                    	case 1 :
                    	    // InternalMnc.g:5138:5: otherlv_3= ',' ( (lv_values_4_0= rulePrimitiveValue ) )
                    	    {
                    	    otherlv_3=(Token)match(input,17,FOLLOW_20); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(otherlv_3, grammarAccess.getArrayValuesAccess().getCommaKeyword_2_1_0());
                    	      				
                    	    }
                    	    // InternalMnc.g:5142:5: ( (lv_values_4_0= rulePrimitiveValue ) )
                    	    // InternalMnc.g:5143:6: (lv_values_4_0= rulePrimitiveValue )
                    	    {
                    	    // InternalMnc.g:5143:6: (lv_values_4_0= rulePrimitiveValue )
                    	    // InternalMnc.g:5144:7: lv_values_4_0= rulePrimitiveValue
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      							newCompositeNode(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_1_1_0());
                    	      						
                    	    }
                    	    pushFollow(FOLLOW_11);
                    	    lv_values_4_0=rulePrimitiveValue();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

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


                    	    }
                    	    break;

                    	default :
                    	    break loop114;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_5, grammarAccess.getArrayValuesAccess().getRightSquareBracketKeyword_3());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleArrayValues"


    // $ANTLR start "entryRuleArrayType"
    // InternalMnc.g:5171:1: entryRuleArrayType returns [EObject current=null] : iv_ruleArrayType= ruleArrayType EOF ;
    public final EObject entryRuleArrayType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayType = null;


        try {
            // InternalMnc.g:5171:50: (iv_ruleArrayType= ruleArrayType EOF )
            // InternalMnc.g:5172:2: iv_ruleArrayType= ruleArrayType EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getArrayTypeRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleArrayType=ruleArrayType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleArrayType; 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5178:1: ruleArrayType returns [EObject current=null] : ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? ) ;
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
            // InternalMnc.g:5184:2: ( ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? ) )
            // InternalMnc.g:5185:2: ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? )
            {
            // InternalMnc.g:5185:2: ( () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )? )
            // InternalMnc.g:5186:3: () ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) ) otherlv_3= '[' otherlv_4= ']' ( (lv_name_5_0= ruleEString ) ) (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )?
            {
            // InternalMnc.g:5186:3: ()
            // InternalMnc.g:5187:4: 
            {
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getArrayTypeAccess().getArrayTypeAction_0(),
              					current);
              			
            }

            }

            // InternalMnc.g:5193:3: ( ( (lv_primitiveType_1_0= rulePrimitiveValueType ) ) | ( ( ruleQualifiedName ) ) )
            int alt116=2;
            int LA116_0 = input.LA(1);

            if ( ((LA116_0>=103 && LA116_0<=108)) ) {
                alt116=1;
            }
            else if ( (LA116_0==RULE_ID) ) {
                alt116=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 116, 0, input);

                throw nvae;
            }
            switch (alt116) {
                case 1 :
                    // InternalMnc.g:5194:4: ( (lv_primitiveType_1_0= rulePrimitiveValueType ) )
                    {
                    // InternalMnc.g:5194:4: ( (lv_primitiveType_1_0= rulePrimitiveValueType ) )
                    // InternalMnc.g:5195:5: (lv_primitiveType_1_0= rulePrimitiveValueType )
                    {
                    // InternalMnc.g:5195:5: (lv_primitiveType_1_0= rulePrimitiveValueType )
                    // InternalMnc.g:5196:6: lv_primitiveType_1_0= rulePrimitiveValueType
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getArrayTypeAccess().getPrimitiveTypePrimitiveValueTypeEnumRuleCall_1_0_0());
                      					
                    }
                    pushFollow(FOLLOW_9);
                    lv_primitiveType_1_0=rulePrimitiveValueType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

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


                    }
                    break;
                case 2 :
                    // InternalMnc.g:5214:4: ( ( ruleQualifiedName ) )
                    {
                    // InternalMnc.g:5214:4: ( ( ruleQualifiedName ) )
                    // InternalMnc.g:5215:5: ( ruleQualifiedName )
                    {
                    // InternalMnc.g:5215:5: ( ruleQualifiedName )
                    // InternalMnc.g:5216:6: ruleQualifiedName
                    {
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElement(grammarAccess.getArrayTypeRule());
                      						}
                      					
                    }
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getArrayTypeAccess().getDataModelTypeDataModelCrossReference_1_1_0());
                      					
                    }
                    pushFollow(FOLLOW_9);
                    ruleQualifiedName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }


                    }
                    break;

            }

            otherlv_3=(Token)match(input,16,FOLLOW_17); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_3, grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_2());
              		
            }
            otherlv_4=(Token)match(input,18,FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_4, grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_3());
              		
            }
            // InternalMnc.g:5239:3: ( (lv_name_5_0= ruleEString ) )
            // InternalMnc.g:5240:4: (lv_name_5_0= ruleEString )
            {
            // InternalMnc.g:5240:4: (lv_name_5_0= ruleEString )
            // InternalMnc.g:5241:5: lv_name_5_0= ruleEString
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getArrayTypeAccess().getNameEStringParserRuleCall_4_0());
              				
            }
            pushFollow(FOLLOW_102);
            lv_name_5_0=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

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


            }

            // InternalMnc.g:5258:3: (otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']' )?
            int alt119=2;
            int LA119_0 = input.LA(1);

            if ( (LA119_0==21) ) {
                alt119=1;
            }
            switch (alt119) {
                case 1 :
                    // InternalMnc.g:5259:4: otherlv_6= '=' otherlv_7= '[' ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )? otherlv_11= ']'
                    {
                    otherlv_6=(Token)match(input,21,FOLLOW_9); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_6, grammarAccess.getArrayTypeAccess().getEqualsSignKeyword_5_0());
                      			
                    }
                    otherlv_7=(Token)match(input,16,FOLLOW_106); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_7, grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_5_1());
                      			
                    }
                    // InternalMnc.g:5267:4: ( ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )* )?
                    int alt118=2;
                    int LA118_0 = input.LA(1);

                    if ( (LA118_0==RULE_ID||(LA118_0>=RULE_STRING && LA118_0<=RULE_INT)||LA118_0==16||(LA118_0>=97 && LA118_0<=100)) ) {
                        alt118=1;
                    }
                    switch (alt118) {
                        case 1 :
                            // InternalMnc.g:5268:5: ( (lv_values_8_0= rulePrimitiveValue ) ) (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )*
                            {
                            // InternalMnc.g:5268:5: ( (lv_values_8_0= rulePrimitiveValue ) )
                            // InternalMnc.g:5269:6: (lv_values_8_0= rulePrimitiveValue )
                            {
                            // InternalMnc.g:5269:6: (lv_values_8_0= rulePrimitiveValue )
                            // InternalMnc.g:5270:7: lv_values_8_0= rulePrimitiveValue
                            {
                            if ( state.backtracking==0 ) {

                              							newCompositeNode(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_0_0());
                              						
                            }
                            pushFollow(FOLLOW_11);
                            lv_values_8_0=rulePrimitiveValue();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

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


                            }

                            // InternalMnc.g:5287:5: (otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) ) )*
                            loop117:
                            do {
                                int alt117=2;
                                int LA117_0 = input.LA(1);

                                if ( (LA117_0==17) ) {
                                    alt117=1;
                                }


                                switch (alt117) {
                            	case 1 :
                            	    // InternalMnc.g:5288:6: otherlv_9= ',' ( (lv_values_10_0= rulePrimitiveValue ) )
                            	    {
                            	    otherlv_9=(Token)match(input,17,FOLLOW_20); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      						newLeafNode(otherlv_9, grammarAccess.getArrayTypeAccess().getCommaKeyword_5_2_1_0());
                            	      					
                            	    }
                            	    // InternalMnc.g:5292:6: ( (lv_values_10_0= rulePrimitiveValue ) )
                            	    // InternalMnc.g:5293:7: (lv_values_10_0= rulePrimitiveValue )
                            	    {
                            	    // InternalMnc.g:5293:7: (lv_values_10_0= rulePrimitiveValue )
                            	    // InternalMnc.g:5294:8: lv_values_10_0= rulePrimitiveValue
                            	    {
                            	    if ( state.backtracking==0 ) {

                            	      								newCompositeNode(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_1_1_0());
                            	      							
                            	    }
                            	    pushFollow(FOLLOW_11);
                            	    lv_values_10_0=rulePrimitiveValue();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

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


                            	    }
                            	    break;

                            	default :
                            	    break loop117;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_11=(Token)match(input,18,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_11, grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_5_3());
                      			
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleArrayType"


    // $ANTLR start "entryRuleEString"
    // InternalMnc.g:5322:1: entryRuleEString returns [String current=null] : iv_ruleEString= ruleEString EOF ;
    public final String entryRuleEString() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEString = null;


        try {
            // InternalMnc.g:5322:47: (iv_ruleEString= ruleEString EOF )
            // InternalMnc.g:5323:2: iv_ruleEString= ruleEString EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEStringRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleEString=ruleEString();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEString.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5329:1: ruleEString returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleEString() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;
        Token this_ID_1=null;


        	enterRule();

        try {
            // InternalMnc.g:5335:2: ( (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID ) )
            // InternalMnc.g:5336:2: (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID )
            {
            // InternalMnc.g:5336:2: (this_STRING_0= RULE_STRING | this_ID_1= RULE_ID )
            int alt120=2;
            int LA120_0 = input.LA(1);

            if ( (LA120_0==RULE_STRING) ) {
                alt120=1;
            }
            else if ( (LA120_0==RULE_ID) ) {
                alt120=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 120, 0, input);

                throw nvae;
            }
            switch (alt120) {
                case 1 :
                    // InternalMnc.g:5337:3: this_STRING_0= RULE_STRING
                    {
                    this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_STRING_0);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_STRING_0, grammarAccess.getEStringAccess().getSTRINGTerminalRuleCall_0());
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalMnc.g:5345:3: this_ID_1= RULE_ID
                    {
                    this_ID_1=(Token)match(input,RULE_ID,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_ID_1);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_ID_1, grammarAccess.getEStringAccess().getIDTerminalRuleCall_1());
                      		
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleEString"


    // $ANTLR start "entryRuleEInt"
    // InternalMnc.g:5356:1: entryRuleEInt returns [String current=null] : iv_ruleEInt= ruleEInt EOF ;
    public final String entryRuleEInt() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEInt = null;


        try {
            // InternalMnc.g:5356:44: (iv_ruleEInt= ruleEInt EOF )
            // InternalMnc.g:5357:2: iv_ruleEInt= ruleEInt EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEIntRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleEInt=ruleEInt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEInt.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5363:1: ruleEInt returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? this_INT_1= RULE_INT ) ;
    public final AntlrDatatypeRuleToken ruleEInt() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;


        	enterRule();

        try {
            // InternalMnc.g:5369:2: ( ( (kw= '-' )? this_INT_1= RULE_INT ) )
            // InternalMnc.g:5370:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            {
            // InternalMnc.g:5370:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            // InternalMnc.g:5371:3: (kw= '-' )? this_INT_1= RULE_INT
            {
            // InternalMnc.g:5371:3: (kw= '-' )?
            int alt121=2;
            int LA121_0 = input.LA(1);

            if ( (LA121_0==98) ) {
                alt121=1;
            }
            switch (alt121) {
                case 1 :
                    // InternalMnc.g:5372:4: kw= '-'
                    {
                    kw=(Token)match(input,98,FOLLOW_107); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(kw);
                      				newLeafNode(kw, grammarAccess.getEIntAccess().getHyphenMinusKeyword_0());
                      			
                    }

                    }
                    break;

            }

            this_INT_1=(Token)match(input,RULE_INT,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_INT_1);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_INT_1, grammarAccess.getEIntAccess().getINTTerminalRuleCall_1());
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleEInt"


    // $ANTLR start "entryRuleEBoolean"
    // InternalMnc.g:5389:1: entryRuleEBoolean returns [String current=null] : iv_ruleEBoolean= ruleEBoolean EOF ;
    public final String entryRuleEBoolean() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEBoolean = null;


        try {
            // InternalMnc.g:5389:48: (iv_ruleEBoolean= ruleEBoolean EOF )
            // InternalMnc.g:5390:2: iv_ruleEBoolean= ruleEBoolean EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEBooleanRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleEBoolean=ruleEBoolean();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEBoolean.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5396:1: ruleEBoolean returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'false' | kw= 'true' ) ;
    public final AntlrDatatypeRuleToken ruleEBoolean() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalMnc.g:5402:2: ( (kw= 'false' | kw= 'true' ) )
            // InternalMnc.g:5403:2: (kw= 'false' | kw= 'true' )
            {
            // InternalMnc.g:5403:2: (kw= 'false' | kw= 'true' )
            int alt122=2;
            int LA122_0 = input.LA(1);

            if ( (LA122_0==99) ) {
                alt122=1;
            }
            else if ( (LA122_0==100) ) {
                alt122=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 122, 0, input);

                throw nvae;
            }
            switch (alt122) {
                case 1 :
                    // InternalMnc.g:5404:3: kw= 'false'
                    {
                    kw=(Token)match(input,99,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(kw);
                      			newLeafNode(kw, grammarAccess.getEBooleanAccess().getFalseKeyword_0());
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalMnc.g:5410:3: kw= 'true'
                    {
                    kw=(Token)match(input,100,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(kw);
                      			newLeafNode(kw, grammarAccess.getEBooleanAccess().getTrueKeyword_1());
                      		
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleEBoolean"


    // $ANTLR start "entryRuleEFloat"
    // InternalMnc.g:5419:1: entryRuleEFloat returns [String current=null] : iv_ruleEFloat= ruleEFloat EOF ;
    public final String entryRuleEFloat() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEFloat = null;


        try {
            // InternalMnc.g:5419:46: (iv_ruleEFloat= ruleEFloat EOF )
            // InternalMnc.g:5420:2: iv_ruleEFloat= ruleEFloat EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEFloatRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleEFloat=ruleEFloat();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEFloat.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5426:1: ruleEFloat returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? ) ;
    public final AntlrDatatypeRuleToken ruleEFloat() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;
        Token this_INT_3=null;
        Token this_INT_7=null;


        	enterRule();

        try {
            // InternalMnc.g:5432:2: ( ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? ) )
            // InternalMnc.g:5433:2: ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? )
            {
            // InternalMnc.g:5433:2: ( (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )? )
            // InternalMnc.g:5434:3: (kw= '-' )? (this_INT_1= RULE_INT )? kw= '.' this_INT_3= RULE_INT ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )?
            {
            // InternalMnc.g:5434:3: (kw= '-' )?
            int alt123=2;
            int LA123_0 = input.LA(1);

            if ( (LA123_0==98) ) {
                alt123=1;
            }
            switch (alt123) {
                case 1 :
                    // InternalMnc.g:5435:4: kw= '-'
                    {
                    kw=(Token)match(input,98,FOLLOW_108); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(kw);
                      				newLeafNode(kw, grammarAccess.getEFloatAccess().getHyphenMinusKeyword_0());
                      			
                    }

                    }
                    break;

            }

            // InternalMnc.g:5441:3: (this_INT_1= RULE_INT )?
            int alt124=2;
            int LA124_0 = input.LA(1);

            if ( (LA124_0==RULE_INT) ) {
                alt124=1;
            }
            switch (alt124) {
                case 1 :
                    // InternalMnc.g:5442:4: this_INT_1= RULE_INT
                    {
                    this_INT_1=(Token)match(input,RULE_INT,FOLLOW_109); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_INT_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_INT_1, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_1());
                      			
                    }

                    }
                    break;

            }

            kw=(Token)match(input,97,FOLLOW_107); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(kw);
              			newLeafNode(kw, grammarAccess.getEFloatAccess().getFullStopKeyword_2());
              		
            }
            this_INT_3=(Token)match(input,RULE_INT,FOLLOW_110); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_INT_3);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_INT_3, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_3());
              		
            }
            // InternalMnc.g:5462:3: ( (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT )?
            int alt127=2;
            int LA127_0 = input.LA(1);

            if ( ((LA127_0>=101 && LA127_0<=102)) ) {
                alt127=1;
            }
            switch (alt127) {
                case 1 :
                    // InternalMnc.g:5463:4: (kw= 'E' | kw= 'e' ) (kw= '-' )? this_INT_7= RULE_INT
                    {
                    // InternalMnc.g:5463:4: (kw= 'E' | kw= 'e' )
                    int alt125=2;
                    int LA125_0 = input.LA(1);

                    if ( (LA125_0==101) ) {
                        alt125=1;
                    }
                    else if ( (LA125_0==102) ) {
                        alt125=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 125, 0, input);

                        throw nvae;
                    }
                    switch (alt125) {
                        case 1 :
                            // InternalMnc.g:5464:5: kw= 'E'
                            {
                            kw=(Token)match(input,101,FOLLOW_16); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              					current.merge(kw);
                              					newLeafNode(kw, grammarAccess.getEFloatAccess().getEKeyword_4_0_0());
                              				
                            }

                            }
                            break;
                        case 2 :
                            // InternalMnc.g:5470:5: kw= 'e'
                            {
                            kw=(Token)match(input,102,FOLLOW_16); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              					current.merge(kw);
                              					newLeafNode(kw, grammarAccess.getEFloatAccess().getEKeyword_4_0_1());
                              				
                            }

                            }
                            break;

                    }

                    // InternalMnc.g:5476:4: (kw= '-' )?
                    int alt126=2;
                    int LA126_0 = input.LA(1);

                    if ( (LA126_0==98) ) {
                        alt126=1;
                    }
                    switch (alt126) {
                        case 1 :
                            // InternalMnc.g:5477:5: kw= '-'
                            {
                            kw=(Token)match(input,98,FOLLOW_107); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              					current.merge(kw);
                              					newLeafNode(kw, grammarAccess.getEFloatAccess().getHyphenMinusKeyword_4_1());
                              				
                            }

                            }
                            break;

                    }

                    this_INT_7=(Token)match(input,RULE_INT,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_INT_7);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_INT_7, grammarAccess.getEFloatAccess().getINTTerminalRuleCall_4_2());
                      			
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleEFloat"


    // $ANTLR start "entryRuleEDate"
    // InternalMnc.g:5495:1: entryRuleEDate returns [String current=null] : iv_ruleEDate= ruleEDate EOF ;
    public final String entryRuleEDate() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEDate = null;


        try {
            // InternalMnc.g:5495:45: (iv_ruleEDate= ruleEDate EOF )
            // InternalMnc.g:5496:2: iv_ruleEDate= ruleEDate EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEDateRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleEDate=ruleEDate();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEDate.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5502:1: ruleEDate returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear ) ;
    public final AntlrDatatypeRuleToken ruleEDate() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_Day_0 = null;

        AntlrDatatypeRuleToken this_Month_2 = null;

        AntlrDatatypeRuleToken this_Year_4 = null;



        	enterRule();

        try {
            // InternalMnc.g:5508:2: ( (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear ) )
            // InternalMnc.g:5509:2: (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear )
            {
            // InternalMnc.g:5509:2: (this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear )
            // InternalMnc.g:5510:3: this_Day_0= ruleDay kw= '-' this_Month_2= ruleMonth kw= '-' this_Year_4= ruleYear
            {
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getEDateAccess().getDayParserRuleCall_0());
              		
            }
            pushFollow(FOLLOW_111);
            this_Day_0=ruleDay();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_Day_0);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
            }
            kw=(Token)match(input,98,FOLLOW_107); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(kw);
              			newLeafNode(kw, grammarAccess.getEDateAccess().getHyphenMinusKeyword_1());
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getEDateAccess().getMonthParserRuleCall_2());
              		
            }
            pushFollow(FOLLOW_111);
            this_Month_2=ruleMonth();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_Month_2);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
            }
            kw=(Token)match(input,98,FOLLOW_107); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(kw);
              			newLeafNode(kw, grammarAccess.getEDateAccess().getHyphenMinusKeyword_3());
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getEDateAccess().getYearParserRuleCall_4());
              		
            }
            pushFollow(FOLLOW_2);
            this_Year_4=ruleYear();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_Year_4);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
            }

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleEDate"


    // $ANTLR start "entryRuleDay"
    // InternalMnc.g:5554:1: entryRuleDay returns [String current=null] : iv_ruleDay= ruleDay EOF ;
    public final String entryRuleDay() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDay = null;


        try {
            // InternalMnc.g:5554:43: (iv_ruleDay= ruleDay EOF )
            // InternalMnc.g:5555:2: iv_ruleDay= ruleDay EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDayRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleDay=ruleDay();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDay.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5561:1: ruleDay returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleDay() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalMnc.g:5567:2: (this_INT_0= RULE_INT )
            // InternalMnc.g:5568:2: this_INT_0= RULE_INT
            {
            this_INT_0=(Token)match(input,RULE_INT,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_INT_0);
              	
            }
            if ( state.backtracking==0 ) {

              		newLeafNode(this_INT_0, grammarAccess.getDayAccess().getINTTerminalRuleCall());
              	
            }

            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleDay"


    // $ANTLR start "entryRuleMonth"
    // InternalMnc.g:5578:1: entryRuleMonth returns [String current=null] : iv_ruleMonth= ruleMonth EOF ;
    public final String entryRuleMonth() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleMonth = null;


        try {
            // InternalMnc.g:5578:45: (iv_ruleMonth= ruleMonth EOF )
            // InternalMnc.g:5579:2: iv_ruleMonth= ruleMonth EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMonthRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleMonth=ruleMonth();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMonth.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5585:1: ruleMonth returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleMonth() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalMnc.g:5591:2: (this_INT_0= RULE_INT )
            // InternalMnc.g:5592:2: this_INT_0= RULE_INT
            {
            this_INT_0=(Token)match(input,RULE_INT,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_INT_0);
              	
            }
            if ( state.backtracking==0 ) {

              		newLeafNode(this_INT_0, grammarAccess.getMonthAccess().getINTTerminalRuleCall());
              	
            }

            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleMonth"


    // $ANTLR start "entryRuleYear"
    // InternalMnc.g:5602:1: entryRuleYear returns [String current=null] : iv_ruleYear= ruleYear EOF ;
    public final String entryRuleYear() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleYear = null;


        try {
            // InternalMnc.g:5602:44: (iv_ruleYear= ruleYear EOF )
            // InternalMnc.g:5603:2: iv_ruleYear= ruleYear EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getYearRule()); 
            }
            pushFollow(FOLLOW_1);
            iv_ruleYear=ruleYear();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleYear.getText(); 
            }
            match(input,EOF,FOLLOW_2); if (state.failed) return current;

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
    // InternalMnc.g:5609:1: ruleYear returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_0= RULE_INT ;
    public final AntlrDatatypeRuleToken ruleYear() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_0=null;


        	enterRule();

        try {
            // InternalMnc.g:5615:2: (this_INT_0= RULE_INT )
            // InternalMnc.g:5616:2: this_INT_0= RULE_INT
            {
            this_INT_0=(Token)match(input,RULE_INT,FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_INT_0);
              	
            }
            if ( state.backtracking==0 ) {

              		newLeafNode(this_INT_0, grammarAccess.getYearAccess().getINTTerminalRuleCall());
              	
            }

            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "ruleYear"


    // $ANTLR start "rulePrimitiveValueType"
    // InternalMnc.g:5626:1: rulePrimitiveValueType returns [Enumerator current=null] : ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) ) ;
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
            // InternalMnc.g:5632:2: ( ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) ) )
            // InternalMnc.g:5633:2: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) )
            {
            // InternalMnc.g:5633:2: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'boolean' ) | (enumLiteral_2= 'float' ) | (enumLiteral_3= 'string' ) | (enumLiteral_4= 'object' ) | (enumLiteral_5= 'date' ) )
            int alt128=6;
            switch ( input.LA(1) ) {
            case 103:
                {
                alt128=1;
                }
                break;
            case 104:
                {
                alt128=2;
                }
                break;
            case 105:
                {
                alt128=3;
                }
                break;
            case 106:
                {
                alt128=4;
                }
                break;
            case 107:
                {
                alt128=5;
                }
                break;
            case 108:
                {
                alt128=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 128, 0, input);

                throw nvae;
            }

            switch (alt128) {
                case 1 :
                    // InternalMnc.g:5634:3: (enumLiteral_0= 'int' )
                    {
                    // InternalMnc.g:5634:3: (enumLiteral_0= 'int' )
                    // InternalMnc.g:5635:4: enumLiteral_0= 'int'
                    {
                    enumLiteral_0=(Token)match(input,103,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_0, grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0());
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // InternalMnc.g:5642:3: (enumLiteral_1= 'boolean' )
                    {
                    // InternalMnc.g:5642:3: (enumLiteral_1= 'boolean' )
                    // InternalMnc.g:5643:4: enumLiteral_1= 'boolean'
                    {
                    enumLiteral_1=(Token)match(input,104,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_1, grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1());
                      			
                    }

                    }


                    }
                    break;
                case 3 :
                    // InternalMnc.g:5650:3: (enumLiteral_2= 'float' )
                    {
                    // InternalMnc.g:5650:3: (enumLiteral_2= 'float' )
                    // InternalMnc.g:5651:4: enumLiteral_2= 'float'
                    {
                    enumLiteral_2=(Token)match(input,105,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_2, grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2());
                      			
                    }

                    }


                    }
                    break;
                case 4 :
                    // InternalMnc.g:5658:3: (enumLiteral_3= 'string' )
                    {
                    // InternalMnc.g:5658:3: (enumLiteral_3= 'string' )
                    // InternalMnc.g:5659:4: enumLiteral_3= 'string'
                    {
                    enumLiteral_3=(Token)match(input,106,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_3, grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3());
                      			
                    }

                    }


                    }
                    break;
                case 5 :
                    // InternalMnc.g:5666:3: (enumLiteral_4= 'object' )
                    {
                    // InternalMnc.g:5666:3: (enumLiteral_4= 'object' )
                    // InternalMnc.g:5667:4: enumLiteral_4= 'object'
                    {
                    enumLiteral_4=(Token)match(input,107,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_4, grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4());
                      			
                    }

                    }


                    }
                    break;
                case 6 :
                    // InternalMnc.g:5674:3: (enumLiteral_5= 'date' )
                    {
                    // InternalMnc.g:5674:3: (enumLiteral_5= 'date' )
                    // InternalMnc.g:5675:4: enumLiteral_5= 'date'
                    {
                    enumLiteral_5=(Token)match(input,108,FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getPrimitiveValueTypeAccess().getDateEnumLiteralDeclaration_5().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_5, grammarAccess.getPrimitiveValueTypeAccess().getDateEnumLiteralDeclaration_5());
                      			
                    }

                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {

              	leaveRule();

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
    // $ANTLR end "rulePrimitiveValueType"

    // Delegated rules


    protected DFA63 dfa63 = new DFA63(this);
    protected DFA109 dfa109 = new DFA109(this);
    protected DFA113 dfa113 = new DFA113(this);
    static final String dfa_1s = "\12\uffff";
    static final String dfa_2s = "\1\31\11\uffff";
    static final String dfa_3s = "\1\135\11\uffff";
    static final String dfa_4s = "\1\uffff\1\11\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String dfa_5s = "\1\0\11\uffff}>";
    static final String[] dfa_6s = {
            "\1\10\4\uffff\1\1\2\uffff\1\3\1\uffff\1\4\1\uffff\1\5\14\uffff\1\2\1\6\1\7\47\uffff\1\11\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "()* loopback of 2105:6: ( ({...}? => ( ({...}? => ( ( ( ( ({...}? => ( ({...}? => ( (lv_port_10_0= rulePort ) ) ) ) ) | ({...}? => ( ({...}? => ( (otherlv_11= 'dataPoints' otherlv_12= '{' ( (lv_dataPoints_13_0= ruleDataPoint ) )* ) otherlv_14= '}' ) ) ) ) )+ {...}?) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_15= 'alarms' otherlv_16= '{' ( (lv_alarms_17_0= ruleAlarm ) )* otherlv_18= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'commands' otherlv_20= '{' ( (lv_commands_21_0= ruleCommand ) )* otherlv_22= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_23= 'events' otherlv_24= '{' ( (lv_events_25_0= ruleEvent ) )* otherlv_26= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_27= 'responses' otherlv_28= '{' ( (lv_responses_29_0= ruleResponse ) )* otherlv_30= '}' ) ) ) ) | ({...}? => ( ({...}? => (otherlv_31= 'operatingStates' otherlv_32= '{' ( (lv_operatingStatesUtility_33_0= ruleOperatingStateUtility ) ) otherlv_34= '}' ) ) ) ) | ({...}? => ( ({...}? => ( (lv_subscribedItems_35_0= ruleSubscribableItemList ) ) ) ) ) | ({...}? => ( ({...}? => ( (lv_ipaddress_36_0= ruleAddress ) ) ) ) ) )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_0 = input.LA(1);

                         
                        int index63_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA63_0==30) ) {s = 1;}

                        else if ( ( LA63_0 == 50 || LA63_0 == 93 ) && getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 0) ) {s = 2;}

                        else if ( LA63_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 1) ) {s = 3;}

                        else if ( LA63_0 == 35 && getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 2) ) {s = 4;}

                        else if ( LA63_0 == 37 && getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 3) ) {s = 5;}

                        else if ( LA63_0 == 51 && getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 4) ) {s = 6;}

                        else if ( LA63_0 == 52 && getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 5) ) {s = 7;}

                        else if ( LA63_0 == 25 && getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 6) ) {s = 8;}

                        else if ( LA63_0 == 92 && getUnorderedGroupHelper().canSelect(grammarAccess.getInterfaceDescriptionAccess().getUnorderedGroup_5(), 7) ) {s = 9;}

                         
                        input.seek(index63_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 63, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_7s = "\15\uffff";
    static final String dfa_8s = "\10\4\2\uffff\1\4\1\uffff\1\4";
    static final String dfa_9s = "\1\154\6\20\1\141\2\uffff\1\4\1\uffff\1\141";
    static final String dfa_10s = "\10\uffff\1\3\1\1\1\uffff\1\2\1\uffff";
    static final String dfa_11s = "\15\uffff}>";
    static final String[] dfa_12s = {
            "\1\7\142\uffff\1\1\1\2\1\3\1\4\1\5\1\6",
            "\1\11\1\uffff\1\11\11\uffff\1\10",
            "\1\11\1\uffff\1\11\11\uffff\1\10",
            "\1\11\1\uffff\1\11\11\uffff\1\10",
            "\1\11\1\uffff\1\11\11\uffff\1\10",
            "\1\11\1\uffff\1\11\11\uffff\1\10",
            "\1\11\1\uffff\1\11\11\uffff\1\10",
            "\1\13\1\uffff\1\13\11\uffff\1\10\120\uffff\1\12",
            "",
            "",
            "\1\14",
            "",
            "\1\13\1\uffff\1\13\11\uffff\1\10\120\uffff\1\12"
    };

    static final short[] dfa_7 = DFA.unpackEncodedString(dfa_7s);
    static final char[] dfa_8 = DFA.unpackEncodedStringToUnsignedChars(dfa_8s);
    static final char[] dfa_9 = DFA.unpackEncodedStringToUnsignedChars(dfa_9s);
    static final short[] dfa_10 = DFA.unpackEncodedString(dfa_10s);
    static final short[] dfa_11 = DFA.unpackEncodedString(dfa_11s);
    static final short[][] dfa_12 = unpackEncodedStringArray(dfa_12s);

    class DFA109 extends DFA {

        public DFA109(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 109;
            this.eot = dfa_7;
            this.eof = dfa_7;
            this.min = dfa_8;
            this.max = dfa_9;
            this.accept = dfa_10;
            this.special = dfa_11;
            this.transition = dfa_12;
        }
        public String getDescription() {
            return "4621:2: (this_SimpleType_0= ruleSimpleType | this_AbstractType_1= ruleAbstractType | this_ArrayType_2= ruleArrayType )";
        }
    }
    static final String dfa_13s = "\13\uffff";
    static final String dfa_14s = "\2\uffff\1\12\5\uffff\1\12\2\uffff";
    static final String dfa_15s = "\1\4\1\7\1\4\5\uffff\1\4\2\uffff";
    static final String dfa_16s = "\1\144\1\141\1\154\5\uffff\1\154\2\uffff";
    static final String dfa_17s = "\3\uffff\1\2\1\3\1\4\1\6\1\7\1\uffff\1\5\1\1";
    static final String dfa_18s = "\13\uffff}>";
    static final String[] dfa_19s = {
            "\1\7\1\uffff\1\4\1\2\10\uffff\1\6\120\uffff\1\3\1\1\2\5",
            "\1\10\131\uffff\1\3",
            "\1\12\13\uffff\3\12\1\uffff\1\12\11\uffff\1\12\16\uffff\1\12\33\uffff\1\12\1\uffff\2\12\24\uffff\1\3\1\11\4\uffff\6\12",
            "",
            "",
            "",
            "",
            "",
            "\1\12\13\uffff\3\12\1\uffff\1\12\11\uffff\1\12\16\uffff\1\12\33\uffff\1\12\1\uffff\2\12\24\uffff\1\3\5\uffff\6\12",
            "",
            ""
    };

    static final short[] dfa_13 = DFA.unpackEncodedString(dfa_13s);
    static final short[] dfa_14 = DFA.unpackEncodedString(dfa_14s);
    static final char[] dfa_15 = DFA.unpackEncodedStringToUnsignedChars(dfa_15s);
    static final char[] dfa_16 = DFA.unpackEncodedStringToUnsignedChars(dfa_16s);
    static final short[] dfa_17 = DFA.unpackEncodedString(dfa_17s);
    static final short[] dfa_18 = DFA.unpackEncodedString(dfa_18s);
    static final short[][] dfa_19 = unpackEncodedStringArray(dfa_19s);

    class DFA113 extends DFA {

        public DFA113(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 113;
            this.eot = dfa_13;
            this.eof = dfa_14;
            this.min = dfa_15;
            this.max = dfa_16;
            this.accept = dfa_17;
            this.special = dfa_18;
            this.transition = dfa_19;
        }
        public String getDescription() {
            return "4881:2: ( ( () ( (lv_intValue_1_0= ruleEInt ) ) ) | ( () ( (lv_floatValue_3_0= ruleEFloat ) ) ) | ( () ( (lv_stringValue_5_0= RULE_STRING ) ) ) | ( () ( (lv_boolValue_7_0= ruleEBoolean ) ) ) | ( () ( (lv_dateValue_9_0= ruleEDate ) ) ) | this_ArrayValues_10= ruleArrayValues | this_AbstractObjectValue_11= ruleAbstractObjectValue )";
        }
    }
 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000040010L,0x00001F8000000000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000000010L,0x00001F8000000000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000140010L,0x00001F8000000000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000160000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000000080L,0x0000000400000000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000000050L,0x00001F8000000000L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x00000000000100D0L,0x0000001E00000000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000001400052L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000001000012L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000078000000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000000070020000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000000060020000L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000040020000L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000055540000000L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x0000055440000000L});
    public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0000055040000000L});
    public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_38 = new BitSet(new long[]{0x0000054040000000L});
    public static final BitSet FOLLOW_39 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_40 = new BitSet(new long[]{0x0000050040000000L});
    public static final BitSet FOLLOW_41 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_42 = new BitSet(new long[]{0x0000040040000000L});
    public static final BitSet FOLLOW_43 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_44 = new BitSet(new long[]{0x0000000000040000L,0x0000000000020000L});
    public static final BitSet FOLLOW_45 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_46 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_47 = new BitSet(new long[]{0x00002000000100D0L,0x0000001E00800000L});
    public static final BitSet FOLLOW_48 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_49 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_50 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_51 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_52 = new BitSet(new long[]{0x00000000000100D0L,0x0000001E00800000L});
    public static final BitSet FOLLOW_53 = new BitSet(new long[]{0x0002000004000000L});
    public static final BitSet FOLLOW_54 = new BitSet(new long[]{0x0000000004020000L});
    public static final BitSet FOLLOW_55 = new BitSet(new long[]{0x001C002A42000000L,0x0000000030000000L});
    public static final BitSet FOLLOW_56 = new BitSet(new long[]{0x0000000040080050L,0x00001F8000000000L});
    public static final BitSet FOLLOW_57 = new BitSet(new long[]{0x0000000040080050L});
    public static final BitSet FOLLOW_58 = new BitSet(new long[]{0x0000000040008050L});
    public static final BitSet FOLLOW_59 = new BitSet(new long[]{0x0000000040000050L});
    public static final BitSet FOLLOW_60 = new BitSet(new long[]{0x0000000001400050L});
    public static final BitSet FOLLOW_61 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_62 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_63 = new BitSet(new long[]{0x1F00000040000000L});
    public static final BitSet FOLLOW_64 = new BitSet(new long[]{0x0000200000020000L});
    public static final BitSet FOLLOW_65 = new BitSet(new long[]{0x2000000040000000L});
    public static final BitSet FOLLOW_66 = new BitSet(new long[]{0x0000000040000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_67 = new BitSet(new long[]{0x0000000040000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_68 = new BitSet(new long[]{0x0000000040000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_69 = new BitSet(new long[]{0x40000000C0000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_70 = new BitSet(new long[]{0x4000000040000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_71 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_72 = new BitSet(new long[]{0x0000000040000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_73 = new BitSet(new long[]{0x00000000C0000000L,0x0000000000004002L});
    public static final BitSet FOLLOW_74 = new BitSet(new long[]{0x0000000040000000L,0x0000000000004002L});
    public static final BitSet FOLLOW_75 = new BitSet(new long[]{0x0000000040000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_76 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_77 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000060L});
    public static final BitSet FOLLOW_78 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_79 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_80 = new BitSet(new long[]{0x00000000400100D0L,0x0000001E00800000L});
    public static final BitSet FOLLOW_81 = new BitSet(new long[]{0x0000000000030000L,0x0000000000000060L});
    public static final BitSet FOLLOW_82 = new BitSet(new long[]{0x0000000000040000L,0x0000000000001A00L});
    public static final BitSet FOLLOW_83 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_84 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_85 = new BitSet(new long[]{0x0000000040000000L,0x0000000000018180L});
    public static final BitSet FOLLOW_86 = new BitSet(new long[]{0x0000000040000000L,0x0000000000018060L});
    public static final BitSet FOLLOW_87 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_88 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_89 = new BitSet(new long[]{0x0000000040000000L,0x0000000000018000L});
    public static final BitSet FOLLOW_90 = new BitSet(new long[]{0x0000000000000010L,0x0000000000040000L});
    public static final BitSet FOLLOW_91 = new BitSet(new long[]{0x0000100000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_92 = new BitSet(new long[]{0x0000100000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_93 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_94 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_95 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_96 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_97 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_98 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_99 = new BitSet(new long[]{0x00000000C0000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_100 = new BitSet(new long[]{0x0000000040000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_101 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_102 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_103 = new BitSet(new long[]{0x0000000040000002L,0x00000001C0000000L});
    public static final BitSet FOLLOW_104 = new BitSet(new long[]{0x0000000040000002L,0x0000000140000000L});
    public static final BitSet FOLLOW_105 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_106 = new BitSet(new long[]{0x00000000000500D0L,0x0000001E00000000L});
    public static final BitSet FOLLOW_107 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_108 = new BitSet(new long[]{0x0000000000000080L,0x0000000200000000L});
    public static final BitSet FOLLOW_109 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_110 = new BitSet(new long[]{0x0000000000000002L,0x0000006000000000L});
    public static final BitSet FOLLOW_111 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});

}
