package com.operation.dsl.ide.contentassist.antlr.internal;

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.DFA;
import com.operation.dsl.services.OperationGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalOperationParser extends AbstractInternalContentAssistParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_INT", "RULE_STRING", "RULE_ID", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'false'", "'true'", "'E'", "'e'", "'int'", "'boolean'", "'float'", "'string'", "'object'", "'date'", "'Operation'", "'('", "')'", "'{'", "'}'", "','", "'execute'", "'return'", "'DataModel'", "'primitives'", "'composites'", "'.'", "'='", "'['", "']'", "'-'"
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
    public static final int RULE_ID=6;
    public static final int RULE_WS=9;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=4;
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

    	public void setGrammarAccess(OperationGrammarAccess grammarAccess) {
    		this.grammarAccess = grammarAccess;
    	}

    	@Override
    	protected Grammar getGrammar() {
    		return grammarAccess.getGrammar();
    	}

    	@Override
    	protected String getValueForTokenName(String tokenName) {
    		return tokenName;
    	}



    // $ANTLR start "entryRuleOperationDescriptions"
    // InternalOperation.g:53:1: entryRuleOperationDescriptions : ruleOperationDescriptions EOF ;
    public final void entryRuleOperationDescriptions() throws RecognitionException {
        try {
            // InternalOperation.g:54:1: ( ruleOperationDescriptions EOF )
            // InternalOperation.g:55:1: ruleOperationDescriptions EOF
            {
             before(grammarAccess.getOperationDescriptionsRule()); 
            pushFollow(FOLLOW_1);
            ruleOperationDescriptions();

            state._fsp--;

             after(grammarAccess.getOperationDescriptionsRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleOperationDescriptions"


    // $ANTLR start "ruleOperationDescriptions"
    // InternalOperation.g:62:1: ruleOperationDescriptions : ( ( rule__OperationDescriptions__Group__0 ) ) ;
    public final void ruleOperationDescriptions() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:66:2: ( ( ( rule__OperationDescriptions__Group__0 ) ) )
            // InternalOperation.g:67:2: ( ( rule__OperationDescriptions__Group__0 ) )
            {
            // InternalOperation.g:67:2: ( ( rule__OperationDescriptions__Group__0 ) )
            // InternalOperation.g:68:3: ( rule__OperationDescriptions__Group__0 )
            {
             before(grammarAccess.getOperationDescriptionsAccess().getGroup()); 
            // InternalOperation.g:69:3: ( rule__OperationDescriptions__Group__0 )
            // InternalOperation.g:69:4: rule__OperationDescriptions__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__OperationDescriptions__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getOperationDescriptionsAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleOperationDescriptions"


    // $ANTLR start "entryRuleOperation"
    // InternalOperation.g:78:1: entryRuleOperation : ruleOperation EOF ;
    public final void entryRuleOperation() throws RecognitionException {
        try {
            // InternalOperation.g:79:1: ( ruleOperation EOF )
            // InternalOperation.g:80:1: ruleOperation EOF
            {
             before(grammarAccess.getOperationRule()); 
            pushFollow(FOLLOW_1);
            ruleOperation();

            state._fsp--;

             after(grammarAccess.getOperationRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleOperation"


    // $ANTLR start "ruleOperation"
    // InternalOperation.g:87:1: ruleOperation : ( ( rule__Operation__Group__0 ) ) ;
    public final void ruleOperation() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:91:2: ( ( ( rule__Operation__Group__0 ) ) )
            // InternalOperation.g:92:2: ( ( rule__Operation__Group__0 ) )
            {
            // InternalOperation.g:92:2: ( ( rule__Operation__Group__0 ) )
            // InternalOperation.g:93:3: ( rule__Operation__Group__0 )
            {
             before(grammarAccess.getOperationAccess().getGroup()); 
            // InternalOperation.g:94:3: ( rule__Operation__Group__0 )
            // InternalOperation.g:94:4: rule__Operation__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Operation__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getOperationAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleOperation"


    // $ANTLR start "entryRuleDataModel"
    // InternalOperation.g:103:1: entryRuleDataModel : ruleDataModel EOF ;
    public final void entryRuleDataModel() throws RecognitionException {
        try {
            // InternalOperation.g:104:1: ( ruleDataModel EOF )
            // InternalOperation.g:105:1: ruleDataModel EOF
            {
             before(grammarAccess.getDataModelRule()); 
            pushFollow(FOLLOW_1);
            ruleDataModel();

            state._fsp--;

             after(grammarAccess.getDataModelRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleDataModel"


    // $ANTLR start "ruleDataModel"
    // InternalOperation.g:112:1: ruleDataModel : ( ( rule__DataModel__UnorderedGroup ) ) ;
    public final void ruleDataModel() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:116:2: ( ( ( rule__DataModel__UnorderedGroup ) ) )
            // InternalOperation.g:117:2: ( ( rule__DataModel__UnorderedGroup ) )
            {
            // InternalOperation.g:117:2: ( ( rule__DataModel__UnorderedGroup ) )
            // InternalOperation.g:118:3: ( rule__DataModel__UnorderedGroup )
            {
             before(grammarAccess.getDataModelAccess().getUnorderedGroup()); 
            // InternalOperation.g:119:3: ( rule__DataModel__UnorderedGroup )
            // InternalOperation.g:119:4: rule__DataModel__UnorderedGroup
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__UnorderedGroup();

            state._fsp--;


            }

             after(grammarAccess.getDataModelAccess().getUnorderedGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleDataModel"


    // $ANTLR start "entryRuleParameter"
    // InternalOperation.g:128:1: entryRuleParameter : ruleParameter EOF ;
    public final void entryRuleParameter() throws RecognitionException {
        try {
            // InternalOperation.g:129:1: ( ruleParameter EOF )
            // InternalOperation.g:130:1: ruleParameter EOF
            {
             before(grammarAccess.getParameterRule()); 
            pushFollow(FOLLOW_1);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getParameterRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleParameter"


    // $ANTLR start "ruleParameter"
    // InternalOperation.g:137:1: ruleParameter : ( ( rule__Parameter__Alternatives ) ) ;
    public final void ruleParameter() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:141:2: ( ( ( rule__Parameter__Alternatives ) ) )
            // InternalOperation.g:142:2: ( ( rule__Parameter__Alternatives ) )
            {
            // InternalOperation.g:142:2: ( ( rule__Parameter__Alternatives ) )
            // InternalOperation.g:143:3: ( rule__Parameter__Alternatives )
            {
             before(grammarAccess.getParameterAccess().getAlternatives()); 
            // InternalOperation.g:144:3: ( rule__Parameter__Alternatives )
            // InternalOperation.g:144:4: rule__Parameter__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__Parameter__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getParameterAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleParameter"


    // $ANTLR start "entryRuleQualifiedName"
    // InternalOperation.g:153:1: entryRuleQualifiedName : ruleQualifiedName EOF ;
    public final void entryRuleQualifiedName() throws RecognitionException {
        try {
            // InternalOperation.g:154:1: ( ruleQualifiedName EOF )
            // InternalOperation.g:155:1: ruleQualifiedName EOF
            {
             before(grammarAccess.getQualifiedNameRule()); 
            pushFollow(FOLLOW_1);
            ruleQualifiedName();

            state._fsp--;

             after(grammarAccess.getQualifiedNameRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleQualifiedName"


    // $ANTLR start "ruleQualifiedName"
    // InternalOperation.g:162:1: ruleQualifiedName : ( ( rule__QualifiedName__Group__0 ) ) ;
    public final void ruleQualifiedName() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:166:2: ( ( ( rule__QualifiedName__Group__0 ) ) )
            // InternalOperation.g:167:2: ( ( rule__QualifiedName__Group__0 ) )
            {
            // InternalOperation.g:167:2: ( ( rule__QualifiedName__Group__0 ) )
            // InternalOperation.g:168:3: ( rule__QualifiedName__Group__0 )
            {
             before(grammarAccess.getQualifiedNameAccess().getGroup()); 
            // InternalOperation.g:169:3: ( rule__QualifiedName__Group__0 )
            // InternalOperation.g:169:4: rule__QualifiedName__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__QualifiedName__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getQualifiedNameAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleQualifiedName"


    // $ANTLR start "entryRuleSimpleType"
    // InternalOperation.g:178:1: entryRuleSimpleType : ruleSimpleType EOF ;
    public final void entryRuleSimpleType() throws RecognitionException {
        try {
            // InternalOperation.g:179:1: ( ruleSimpleType EOF )
            // InternalOperation.g:180:1: ruleSimpleType EOF
            {
             before(grammarAccess.getSimpleTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleSimpleType();

            state._fsp--;

             after(grammarAccess.getSimpleTypeRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleSimpleType"


    // $ANTLR start "ruleSimpleType"
    // InternalOperation.g:187:1: ruleSimpleType : ( ( rule__SimpleType__Group__0 ) ) ;
    public final void ruleSimpleType() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:191:2: ( ( ( rule__SimpleType__Group__0 ) ) )
            // InternalOperation.g:192:2: ( ( rule__SimpleType__Group__0 ) )
            {
            // InternalOperation.g:192:2: ( ( rule__SimpleType__Group__0 ) )
            // InternalOperation.g:193:3: ( rule__SimpleType__Group__0 )
            {
             before(grammarAccess.getSimpleTypeAccess().getGroup()); 
            // InternalOperation.g:194:3: ( rule__SimpleType__Group__0 )
            // InternalOperation.g:194:4: rule__SimpleType__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__SimpleType__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getSimpleTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleSimpleType"


    // $ANTLR start "entryRuleAbstractType"
    // InternalOperation.g:203:1: entryRuleAbstractType : ruleAbstractType EOF ;
    public final void entryRuleAbstractType() throws RecognitionException {
        try {
            // InternalOperation.g:204:1: ( ruleAbstractType EOF )
            // InternalOperation.g:205:1: ruleAbstractType EOF
            {
             before(grammarAccess.getAbstractTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleAbstractType();

            state._fsp--;

             after(grammarAccess.getAbstractTypeRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleAbstractType"


    // $ANTLR start "ruleAbstractType"
    // InternalOperation.g:212:1: ruleAbstractType : ( ( rule__AbstractType__Group__0 ) ) ;
    public final void ruleAbstractType() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:216:2: ( ( ( rule__AbstractType__Group__0 ) ) )
            // InternalOperation.g:217:2: ( ( rule__AbstractType__Group__0 ) )
            {
            // InternalOperation.g:217:2: ( ( rule__AbstractType__Group__0 ) )
            // InternalOperation.g:218:3: ( rule__AbstractType__Group__0 )
            {
             before(grammarAccess.getAbstractTypeAccess().getGroup()); 
            // InternalOperation.g:219:3: ( rule__AbstractType__Group__0 )
            // InternalOperation.g:219:4: rule__AbstractType__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__AbstractType__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getAbstractTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAbstractType"


    // $ANTLR start "entryRulePrimitiveValue"
    // InternalOperation.g:228:1: entryRulePrimitiveValue : rulePrimitiveValue EOF ;
    public final void entryRulePrimitiveValue() throws RecognitionException {
        try {
            // InternalOperation.g:229:1: ( rulePrimitiveValue EOF )
            // InternalOperation.g:230:1: rulePrimitiveValue EOF
            {
             before(grammarAccess.getPrimitiveValueRule()); 
            pushFollow(FOLLOW_1);
            rulePrimitiveValue();

            state._fsp--;

             after(grammarAccess.getPrimitiveValueRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRulePrimitiveValue"


    // $ANTLR start "rulePrimitiveValue"
    // InternalOperation.g:237:1: rulePrimitiveValue : ( ( rule__PrimitiveValue__Alternatives ) ) ;
    public final void rulePrimitiveValue() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:241:2: ( ( ( rule__PrimitiveValue__Alternatives ) ) )
            // InternalOperation.g:242:2: ( ( rule__PrimitiveValue__Alternatives ) )
            {
            // InternalOperation.g:242:2: ( ( rule__PrimitiveValue__Alternatives ) )
            // InternalOperation.g:243:3: ( rule__PrimitiveValue__Alternatives )
            {
             before(grammarAccess.getPrimitiveValueAccess().getAlternatives()); 
            // InternalOperation.g:244:3: ( rule__PrimitiveValue__Alternatives )
            // InternalOperation.g:244:4: rule__PrimitiveValue__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getPrimitiveValueAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rulePrimitiveValue"


    // $ANTLR start "entryRuleAbstractObjectValue"
    // InternalOperation.g:253:1: entryRuleAbstractObjectValue : ruleAbstractObjectValue EOF ;
    public final void entryRuleAbstractObjectValue() throws RecognitionException {
        try {
            // InternalOperation.g:254:1: ( ruleAbstractObjectValue EOF )
            // InternalOperation.g:255:1: ruleAbstractObjectValue EOF
            {
             before(grammarAccess.getAbstractObjectValueRule()); 
            pushFollow(FOLLOW_1);
            ruleAbstractObjectValue();

            state._fsp--;

             after(grammarAccess.getAbstractObjectValueRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleAbstractObjectValue"


    // $ANTLR start "ruleAbstractObjectValue"
    // InternalOperation.g:262:1: ruleAbstractObjectValue : ( ( rule__AbstractObjectValue__Group__0 ) ) ;
    public final void ruleAbstractObjectValue() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:266:2: ( ( ( rule__AbstractObjectValue__Group__0 ) ) )
            // InternalOperation.g:267:2: ( ( rule__AbstractObjectValue__Group__0 ) )
            {
            // InternalOperation.g:267:2: ( ( rule__AbstractObjectValue__Group__0 ) )
            // InternalOperation.g:268:3: ( rule__AbstractObjectValue__Group__0 )
            {
             before(grammarAccess.getAbstractObjectValueAccess().getGroup()); 
            // InternalOperation.g:269:3: ( rule__AbstractObjectValue__Group__0 )
            // InternalOperation.g:269:4: rule__AbstractObjectValue__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__AbstractObjectValue__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getAbstractObjectValueAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAbstractObjectValue"


    // $ANTLR start "entryRuleArrayValues"
    // InternalOperation.g:278:1: entryRuleArrayValues : ruleArrayValues EOF ;
    public final void entryRuleArrayValues() throws RecognitionException {
        try {
            // InternalOperation.g:279:1: ( ruleArrayValues EOF )
            // InternalOperation.g:280:1: ruleArrayValues EOF
            {
             before(grammarAccess.getArrayValuesRule()); 
            pushFollow(FOLLOW_1);
            ruleArrayValues();

            state._fsp--;

             after(grammarAccess.getArrayValuesRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleArrayValues"


    // $ANTLR start "ruleArrayValues"
    // InternalOperation.g:287:1: ruleArrayValues : ( ( rule__ArrayValues__Group__0 ) ) ;
    public final void ruleArrayValues() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:291:2: ( ( ( rule__ArrayValues__Group__0 ) ) )
            // InternalOperation.g:292:2: ( ( rule__ArrayValues__Group__0 ) )
            {
            // InternalOperation.g:292:2: ( ( rule__ArrayValues__Group__0 ) )
            // InternalOperation.g:293:3: ( rule__ArrayValues__Group__0 )
            {
             before(grammarAccess.getArrayValuesAccess().getGroup()); 
            // InternalOperation.g:294:3: ( rule__ArrayValues__Group__0 )
            // InternalOperation.g:294:4: rule__ArrayValues__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getArrayValuesAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleArrayValues"


    // $ANTLR start "entryRuleArrayType"
    // InternalOperation.g:303:1: entryRuleArrayType : ruleArrayType EOF ;
    public final void entryRuleArrayType() throws RecognitionException {
        try {
            // InternalOperation.g:304:1: ( ruleArrayType EOF )
            // InternalOperation.g:305:1: ruleArrayType EOF
            {
             before(grammarAccess.getArrayTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleArrayType();

            state._fsp--;

             after(grammarAccess.getArrayTypeRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleArrayType"


    // $ANTLR start "ruleArrayType"
    // InternalOperation.g:312:1: ruleArrayType : ( ( rule__ArrayType__Group__0 ) ) ;
    public final void ruleArrayType() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:316:2: ( ( ( rule__ArrayType__Group__0 ) ) )
            // InternalOperation.g:317:2: ( ( rule__ArrayType__Group__0 ) )
            {
            // InternalOperation.g:317:2: ( ( rule__ArrayType__Group__0 ) )
            // InternalOperation.g:318:3: ( rule__ArrayType__Group__0 )
            {
             before(grammarAccess.getArrayTypeAccess().getGroup()); 
            // InternalOperation.g:319:3: ( rule__ArrayType__Group__0 )
            // InternalOperation.g:319:4: rule__ArrayType__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getArrayTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleArrayType"


    // $ANTLR start "entryRuleEString"
    // InternalOperation.g:328:1: entryRuleEString : ruleEString EOF ;
    public final void entryRuleEString() throws RecognitionException {
        try {
            // InternalOperation.g:329:1: ( ruleEString EOF )
            // InternalOperation.g:330:1: ruleEString EOF
            {
             before(grammarAccess.getEStringRule()); 
            pushFollow(FOLLOW_1);
            ruleEString();

            state._fsp--;

             after(grammarAccess.getEStringRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEString"


    // $ANTLR start "ruleEString"
    // InternalOperation.g:337:1: ruleEString : ( ( rule__EString__Alternatives ) ) ;
    public final void ruleEString() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:341:2: ( ( ( rule__EString__Alternatives ) ) )
            // InternalOperation.g:342:2: ( ( rule__EString__Alternatives ) )
            {
            // InternalOperation.g:342:2: ( ( rule__EString__Alternatives ) )
            // InternalOperation.g:343:3: ( rule__EString__Alternatives )
            {
             before(grammarAccess.getEStringAccess().getAlternatives()); 
            // InternalOperation.g:344:3: ( rule__EString__Alternatives )
            // InternalOperation.g:344:4: rule__EString__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__EString__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getEStringAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEString"


    // $ANTLR start "entryRuleEInt"
    // InternalOperation.g:353:1: entryRuleEInt : ruleEInt EOF ;
    public final void entryRuleEInt() throws RecognitionException {
        try {
            // InternalOperation.g:354:1: ( ruleEInt EOF )
            // InternalOperation.g:355:1: ruleEInt EOF
            {
             before(grammarAccess.getEIntRule()); 
            pushFollow(FOLLOW_1);
            ruleEInt();

            state._fsp--;

             after(grammarAccess.getEIntRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEInt"


    // $ANTLR start "ruleEInt"
    // InternalOperation.g:362:1: ruleEInt : ( ( rule__EInt__Group__0 ) ) ;
    public final void ruleEInt() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:366:2: ( ( ( rule__EInt__Group__0 ) ) )
            // InternalOperation.g:367:2: ( ( rule__EInt__Group__0 ) )
            {
            // InternalOperation.g:367:2: ( ( rule__EInt__Group__0 ) )
            // InternalOperation.g:368:3: ( rule__EInt__Group__0 )
            {
             before(grammarAccess.getEIntAccess().getGroup()); 
            // InternalOperation.g:369:3: ( rule__EInt__Group__0 )
            // InternalOperation.g:369:4: rule__EInt__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__EInt__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getEIntAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEInt"


    // $ANTLR start "entryRuleEBoolean"
    // InternalOperation.g:378:1: entryRuleEBoolean : ruleEBoolean EOF ;
    public final void entryRuleEBoolean() throws RecognitionException {
        try {
            // InternalOperation.g:379:1: ( ruleEBoolean EOF )
            // InternalOperation.g:380:1: ruleEBoolean EOF
            {
             before(grammarAccess.getEBooleanRule()); 
            pushFollow(FOLLOW_1);
            ruleEBoolean();

            state._fsp--;

             after(grammarAccess.getEBooleanRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEBoolean"


    // $ANTLR start "ruleEBoolean"
    // InternalOperation.g:387:1: ruleEBoolean : ( ( rule__EBoolean__Alternatives ) ) ;
    public final void ruleEBoolean() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:391:2: ( ( ( rule__EBoolean__Alternatives ) ) )
            // InternalOperation.g:392:2: ( ( rule__EBoolean__Alternatives ) )
            {
            // InternalOperation.g:392:2: ( ( rule__EBoolean__Alternatives ) )
            // InternalOperation.g:393:3: ( rule__EBoolean__Alternatives )
            {
             before(grammarAccess.getEBooleanAccess().getAlternatives()); 
            // InternalOperation.g:394:3: ( rule__EBoolean__Alternatives )
            // InternalOperation.g:394:4: rule__EBoolean__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__EBoolean__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getEBooleanAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEBoolean"


    // $ANTLR start "entryRuleEFloat"
    // InternalOperation.g:403:1: entryRuleEFloat : ruleEFloat EOF ;
    public final void entryRuleEFloat() throws RecognitionException {
        try {
            // InternalOperation.g:404:1: ( ruleEFloat EOF )
            // InternalOperation.g:405:1: ruleEFloat EOF
            {
             before(grammarAccess.getEFloatRule()); 
            pushFollow(FOLLOW_1);
            ruleEFloat();

            state._fsp--;

             after(grammarAccess.getEFloatRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEFloat"


    // $ANTLR start "ruleEFloat"
    // InternalOperation.g:412:1: ruleEFloat : ( ( rule__EFloat__Group__0 ) ) ;
    public final void ruleEFloat() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:416:2: ( ( ( rule__EFloat__Group__0 ) ) )
            // InternalOperation.g:417:2: ( ( rule__EFloat__Group__0 ) )
            {
            // InternalOperation.g:417:2: ( ( rule__EFloat__Group__0 ) )
            // InternalOperation.g:418:3: ( rule__EFloat__Group__0 )
            {
             before(grammarAccess.getEFloatAccess().getGroup()); 
            // InternalOperation.g:419:3: ( rule__EFloat__Group__0 )
            // InternalOperation.g:419:4: rule__EFloat__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__EFloat__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getEFloatAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEFloat"


    // $ANTLR start "entryRuleEDate"
    // InternalOperation.g:428:1: entryRuleEDate : ruleEDate EOF ;
    public final void entryRuleEDate() throws RecognitionException {
        try {
            // InternalOperation.g:429:1: ( ruleEDate EOF )
            // InternalOperation.g:430:1: ruleEDate EOF
            {
             before(grammarAccess.getEDateRule()); 
            pushFollow(FOLLOW_1);
            ruleEDate();

            state._fsp--;

             after(grammarAccess.getEDateRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEDate"


    // $ANTLR start "ruleEDate"
    // InternalOperation.g:437:1: ruleEDate : ( ( rule__EDate__Group__0 ) ) ;
    public final void ruleEDate() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:441:2: ( ( ( rule__EDate__Group__0 ) ) )
            // InternalOperation.g:442:2: ( ( rule__EDate__Group__0 ) )
            {
            // InternalOperation.g:442:2: ( ( rule__EDate__Group__0 ) )
            // InternalOperation.g:443:3: ( rule__EDate__Group__0 )
            {
             before(grammarAccess.getEDateAccess().getGroup()); 
            // InternalOperation.g:444:3: ( rule__EDate__Group__0 )
            // InternalOperation.g:444:4: rule__EDate__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__EDate__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getEDateAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEDate"


    // $ANTLR start "entryRuleDay"
    // InternalOperation.g:453:1: entryRuleDay : ruleDay EOF ;
    public final void entryRuleDay() throws RecognitionException {
        try {
            // InternalOperation.g:454:1: ( ruleDay EOF )
            // InternalOperation.g:455:1: ruleDay EOF
            {
             before(grammarAccess.getDayRule()); 
            pushFollow(FOLLOW_1);
            ruleDay();

            state._fsp--;

             after(grammarAccess.getDayRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleDay"


    // $ANTLR start "ruleDay"
    // InternalOperation.g:462:1: ruleDay : ( RULE_INT ) ;
    public final void ruleDay() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:466:2: ( ( RULE_INT ) )
            // InternalOperation.g:467:2: ( RULE_INT )
            {
            // InternalOperation.g:467:2: ( RULE_INT )
            // InternalOperation.g:468:3: RULE_INT
            {
             before(grammarAccess.getDayAccess().getINTTerminalRuleCall()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getDayAccess().getINTTerminalRuleCall()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleDay"


    // $ANTLR start "entryRuleMonth"
    // InternalOperation.g:478:1: entryRuleMonth : ruleMonth EOF ;
    public final void entryRuleMonth() throws RecognitionException {
        try {
            // InternalOperation.g:479:1: ( ruleMonth EOF )
            // InternalOperation.g:480:1: ruleMonth EOF
            {
             before(grammarAccess.getMonthRule()); 
            pushFollow(FOLLOW_1);
            ruleMonth();

            state._fsp--;

             after(grammarAccess.getMonthRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleMonth"


    // $ANTLR start "ruleMonth"
    // InternalOperation.g:487:1: ruleMonth : ( RULE_INT ) ;
    public final void ruleMonth() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:491:2: ( ( RULE_INT ) )
            // InternalOperation.g:492:2: ( RULE_INT )
            {
            // InternalOperation.g:492:2: ( RULE_INT )
            // InternalOperation.g:493:3: RULE_INT
            {
             before(grammarAccess.getMonthAccess().getINTTerminalRuleCall()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getMonthAccess().getINTTerminalRuleCall()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleMonth"


    // $ANTLR start "entryRuleYear"
    // InternalOperation.g:503:1: entryRuleYear : ruleYear EOF ;
    public final void entryRuleYear() throws RecognitionException {
        try {
            // InternalOperation.g:504:1: ( ruleYear EOF )
            // InternalOperation.g:505:1: ruleYear EOF
            {
             before(grammarAccess.getYearRule()); 
            pushFollow(FOLLOW_1);
            ruleYear();

            state._fsp--;

             after(grammarAccess.getYearRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleYear"


    // $ANTLR start "ruleYear"
    // InternalOperation.g:512:1: ruleYear : ( RULE_INT ) ;
    public final void ruleYear() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:516:2: ( ( RULE_INT ) )
            // InternalOperation.g:517:2: ( RULE_INT )
            {
            // InternalOperation.g:517:2: ( RULE_INT )
            // InternalOperation.g:518:3: RULE_INT
            {
             before(grammarAccess.getYearAccess().getINTTerminalRuleCall()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getYearAccess().getINTTerminalRuleCall()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleYear"


    // $ANTLR start "rulePrimitiveValueType"
    // InternalOperation.g:528:1: rulePrimitiveValueType : ( ( rule__PrimitiveValueType__Alternatives ) ) ;
    public final void rulePrimitiveValueType() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:532:1: ( ( ( rule__PrimitiveValueType__Alternatives ) ) )
            // InternalOperation.g:533:2: ( ( rule__PrimitiveValueType__Alternatives ) )
            {
            // InternalOperation.g:533:2: ( ( rule__PrimitiveValueType__Alternatives ) )
            // InternalOperation.g:534:3: ( rule__PrimitiveValueType__Alternatives )
            {
             before(grammarAccess.getPrimitiveValueTypeAccess().getAlternatives()); 
            // InternalOperation.g:535:3: ( rule__PrimitiveValueType__Alternatives )
            // InternalOperation.g:535:4: rule__PrimitiveValueType__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValueType__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getPrimitiveValueTypeAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rulePrimitiveValueType"


    // $ANTLR start "rule__Parameter__Alternatives"
    // InternalOperation.g:543:1: rule__Parameter__Alternatives : ( ( ruleSimpleType ) | ( ruleAbstractType ) | ( ruleArrayType ) );
    public final void rule__Parameter__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:547:1: ( ( ruleSimpleType ) | ( ruleAbstractType ) | ( ruleArrayType ) )
            int alt1=3;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // InternalOperation.g:548:2: ( ruleSimpleType )
                    {
                    // InternalOperation.g:548:2: ( ruleSimpleType )
                    // InternalOperation.g:549:3: ruleSimpleType
                    {
                     before(grammarAccess.getParameterAccess().getSimpleTypeParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleSimpleType();

                    state._fsp--;

                     after(grammarAccess.getParameterAccess().getSimpleTypeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:554:2: ( ruleAbstractType )
                    {
                    // InternalOperation.g:554:2: ( ruleAbstractType )
                    // InternalOperation.g:555:3: ruleAbstractType
                    {
                     before(grammarAccess.getParameterAccess().getAbstractTypeParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleAbstractType();

                    state._fsp--;

                     after(grammarAccess.getParameterAccess().getAbstractTypeParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOperation.g:560:2: ( ruleArrayType )
                    {
                    // InternalOperation.g:560:2: ( ruleArrayType )
                    // InternalOperation.g:561:3: ruleArrayType
                    {
                     before(grammarAccess.getParameterAccess().getArrayTypeParserRuleCall_2()); 
                    pushFollow(FOLLOW_2);
                    ruleArrayType();

                    state._fsp--;

                     after(grammarAccess.getParameterAccess().getArrayTypeParserRuleCall_2()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Parameter__Alternatives"


    // $ANTLR start "rule__PrimitiveValue__Alternatives"
    // InternalOperation.g:570:1: rule__PrimitiveValue__Alternatives : ( ( ( rule__PrimitiveValue__Group_0__0 ) ) | ( ( rule__PrimitiveValue__Group_1__0 ) ) | ( ( rule__PrimitiveValue__Group_2__0 ) ) | ( ( rule__PrimitiveValue__Group_3__0 ) ) | ( ( rule__PrimitiveValue__Group_4__0 ) ) | ( ruleArrayValues ) | ( ruleAbstractObjectValue ) );
    public final void rule__PrimitiveValue__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:574:1: ( ( ( rule__PrimitiveValue__Group_0__0 ) ) | ( ( rule__PrimitiveValue__Group_1__0 ) ) | ( ( rule__PrimitiveValue__Group_2__0 ) ) | ( ( rule__PrimitiveValue__Group_3__0 ) ) | ( ( rule__PrimitiveValue__Group_4__0 ) ) | ( ruleArrayValues ) | ( ruleAbstractObjectValue ) )
            int alt2=7;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // InternalOperation.g:575:2: ( ( rule__PrimitiveValue__Group_0__0 ) )
                    {
                    // InternalOperation.g:575:2: ( ( rule__PrimitiveValue__Group_0__0 ) )
                    // InternalOperation.g:576:3: ( rule__PrimitiveValue__Group_0__0 )
                    {
                     before(grammarAccess.getPrimitiveValueAccess().getGroup_0()); 
                    // InternalOperation.g:577:3: ( rule__PrimitiveValue__Group_0__0 )
                    // InternalOperation.g:577:4: rule__PrimitiveValue__Group_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__PrimitiveValue__Group_0__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getPrimitiveValueAccess().getGroup_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:581:2: ( ( rule__PrimitiveValue__Group_1__0 ) )
                    {
                    // InternalOperation.g:581:2: ( ( rule__PrimitiveValue__Group_1__0 ) )
                    // InternalOperation.g:582:3: ( rule__PrimitiveValue__Group_1__0 )
                    {
                     before(grammarAccess.getPrimitiveValueAccess().getGroup_1()); 
                    // InternalOperation.g:583:3: ( rule__PrimitiveValue__Group_1__0 )
                    // InternalOperation.g:583:4: rule__PrimitiveValue__Group_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__PrimitiveValue__Group_1__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getPrimitiveValueAccess().getGroup_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOperation.g:587:2: ( ( rule__PrimitiveValue__Group_2__0 ) )
                    {
                    // InternalOperation.g:587:2: ( ( rule__PrimitiveValue__Group_2__0 ) )
                    // InternalOperation.g:588:3: ( rule__PrimitiveValue__Group_2__0 )
                    {
                     before(grammarAccess.getPrimitiveValueAccess().getGroup_2()); 
                    // InternalOperation.g:589:3: ( rule__PrimitiveValue__Group_2__0 )
                    // InternalOperation.g:589:4: rule__PrimitiveValue__Group_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__PrimitiveValue__Group_2__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getPrimitiveValueAccess().getGroup_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOperation.g:593:2: ( ( rule__PrimitiveValue__Group_3__0 ) )
                    {
                    // InternalOperation.g:593:2: ( ( rule__PrimitiveValue__Group_3__0 ) )
                    // InternalOperation.g:594:3: ( rule__PrimitiveValue__Group_3__0 )
                    {
                     before(grammarAccess.getPrimitiveValueAccess().getGroup_3()); 
                    // InternalOperation.g:595:3: ( rule__PrimitiveValue__Group_3__0 )
                    // InternalOperation.g:595:4: rule__PrimitiveValue__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__PrimitiveValue__Group_3__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getPrimitiveValueAccess().getGroup_3()); 

                    }


                    }
                    break;
                case 5 :
                    // InternalOperation.g:599:2: ( ( rule__PrimitiveValue__Group_4__0 ) )
                    {
                    // InternalOperation.g:599:2: ( ( rule__PrimitiveValue__Group_4__0 ) )
                    // InternalOperation.g:600:3: ( rule__PrimitiveValue__Group_4__0 )
                    {
                     before(grammarAccess.getPrimitiveValueAccess().getGroup_4()); 
                    // InternalOperation.g:601:3: ( rule__PrimitiveValue__Group_4__0 )
                    // InternalOperation.g:601:4: rule__PrimitiveValue__Group_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__PrimitiveValue__Group_4__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getPrimitiveValueAccess().getGroup_4()); 

                    }


                    }
                    break;
                case 6 :
                    // InternalOperation.g:605:2: ( ruleArrayValues )
                    {
                    // InternalOperation.g:605:2: ( ruleArrayValues )
                    // InternalOperation.g:606:3: ruleArrayValues
                    {
                     before(grammarAccess.getPrimitiveValueAccess().getArrayValuesParserRuleCall_5()); 
                    pushFollow(FOLLOW_2);
                    ruleArrayValues();

                    state._fsp--;

                     after(grammarAccess.getPrimitiveValueAccess().getArrayValuesParserRuleCall_5()); 

                    }


                    }
                    break;
                case 7 :
                    // InternalOperation.g:611:2: ( ruleAbstractObjectValue )
                    {
                    // InternalOperation.g:611:2: ( ruleAbstractObjectValue )
                    // InternalOperation.g:612:3: ruleAbstractObjectValue
                    {
                     before(grammarAccess.getPrimitiveValueAccess().getAbstractObjectValueParserRuleCall_6()); 
                    pushFollow(FOLLOW_2);
                    ruleAbstractObjectValue();

                    state._fsp--;

                     after(grammarAccess.getPrimitiveValueAccess().getAbstractObjectValueParserRuleCall_6()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Alternatives"


    // $ANTLR start "rule__ArrayType__Alternatives_1"
    // InternalOperation.g:621:1: rule__ArrayType__Alternatives_1 : ( ( ( rule__ArrayType__PrimitiveTypeAssignment_1_0 ) ) | ( ( rule__ArrayType__DataModelTypeAssignment_1_1 ) ) );
    public final void rule__ArrayType__Alternatives_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:625:1: ( ( ( rule__ArrayType__PrimitiveTypeAssignment_1_0 ) ) | ( ( rule__ArrayType__DataModelTypeAssignment_1_1 ) ) )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=15 && LA3_0<=20)) ) {
                alt3=1;
            }
            else if ( (LA3_0==RULE_ID) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // InternalOperation.g:626:2: ( ( rule__ArrayType__PrimitiveTypeAssignment_1_0 ) )
                    {
                    // InternalOperation.g:626:2: ( ( rule__ArrayType__PrimitiveTypeAssignment_1_0 ) )
                    // InternalOperation.g:627:3: ( rule__ArrayType__PrimitiveTypeAssignment_1_0 )
                    {
                     before(grammarAccess.getArrayTypeAccess().getPrimitiveTypeAssignment_1_0()); 
                    // InternalOperation.g:628:3: ( rule__ArrayType__PrimitiveTypeAssignment_1_0 )
                    // InternalOperation.g:628:4: rule__ArrayType__PrimitiveTypeAssignment_1_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ArrayType__PrimitiveTypeAssignment_1_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getArrayTypeAccess().getPrimitiveTypeAssignment_1_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:632:2: ( ( rule__ArrayType__DataModelTypeAssignment_1_1 ) )
                    {
                    // InternalOperation.g:632:2: ( ( rule__ArrayType__DataModelTypeAssignment_1_1 ) )
                    // InternalOperation.g:633:3: ( rule__ArrayType__DataModelTypeAssignment_1_1 )
                    {
                     before(grammarAccess.getArrayTypeAccess().getDataModelTypeAssignment_1_1()); 
                    // InternalOperation.g:634:3: ( rule__ArrayType__DataModelTypeAssignment_1_1 )
                    // InternalOperation.g:634:4: rule__ArrayType__DataModelTypeAssignment_1_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__ArrayType__DataModelTypeAssignment_1_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getArrayTypeAccess().getDataModelTypeAssignment_1_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Alternatives_1"


    // $ANTLR start "rule__EString__Alternatives"
    // InternalOperation.g:642:1: rule__EString__Alternatives : ( ( RULE_STRING ) | ( RULE_ID ) );
    public final void rule__EString__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:646:1: ( ( RULE_STRING ) | ( RULE_ID ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_STRING) ) {
                alt4=1;
            }
            else if ( (LA4_0==RULE_ID) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // InternalOperation.g:647:2: ( RULE_STRING )
                    {
                    // InternalOperation.g:647:2: ( RULE_STRING )
                    // InternalOperation.g:648:3: RULE_STRING
                    {
                     before(grammarAccess.getEStringAccess().getSTRINGTerminalRuleCall_0()); 
                    match(input,RULE_STRING,FOLLOW_2); 
                     after(grammarAccess.getEStringAccess().getSTRINGTerminalRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:653:2: ( RULE_ID )
                    {
                    // InternalOperation.g:653:2: ( RULE_ID )
                    // InternalOperation.g:654:3: RULE_ID
                    {
                     before(grammarAccess.getEStringAccess().getIDTerminalRuleCall_1()); 
                    match(input,RULE_ID,FOLLOW_2); 
                     after(grammarAccess.getEStringAccess().getIDTerminalRuleCall_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EString__Alternatives"


    // $ANTLR start "rule__EBoolean__Alternatives"
    // InternalOperation.g:663:1: rule__EBoolean__Alternatives : ( ( 'false' ) | ( 'true' ) );
    public final void rule__EBoolean__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:667:1: ( ( 'false' ) | ( 'true' ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==11) ) {
                alt5=1;
            }
            else if ( (LA5_0==12) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // InternalOperation.g:668:2: ( 'false' )
                    {
                    // InternalOperation.g:668:2: ( 'false' )
                    // InternalOperation.g:669:3: 'false'
                    {
                     before(grammarAccess.getEBooleanAccess().getFalseKeyword_0()); 
                    match(input,11,FOLLOW_2); 
                     after(grammarAccess.getEBooleanAccess().getFalseKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:674:2: ( 'true' )
                    {
                    // InternalOperation.g:674:2: ( 'true' )
                    // InternalOperation.g:675:3: 'true'
                    {
                     before(grammarAccess.getEBooleanAccess().getTrueKeyword_1()); 
                    match(input,12,FOLLOW_2); 
                     after(grammarAccess.getEBooleanAccess().getTrueKeyword_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EBoolean__Alternatives"


    // $ANTLR start "rule__EFloat__Alternatives_4_0"
    // InternalOperation.g:684:1: rule__EFloat__Alternatives_4_0 : ( ( 'E' ) | ( 'e' ) );
    public final void rule__EFloat__Alternatives_4_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:688:1: ( ( 'E' ) | ( 'e' ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==13) ) {
                alt6=1;
            }
            else if ( (LA6_0==14) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // InternalOperation.g:689:2: ( 'E' )
                    {
                    // InternalOperation.g:689:2: ( 'E' )
                    // InternalOperation.g:690:3: 'E'
                    {
                     before(grammarAccess.getEFloatAccess().getEKeyword_4_0_0()); 
                    match(input,13,FOLLOW_2); 
                     after(grammarAccess.getEFloatAccess().getEKeyword_4_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:695:2: ( 'e' )
                    {
                    // InternalOperation.g:695:2: ( 'e' )
                    // InternalOperation.g:696:3: 'e'
                    {
                     before(grammarAccess.getEFloatAccess().getEKeyword_4_0_1()); 
                    match(input,14,FOLLOW_2); 
                     after(grammarAccess.getEFloatAccess().getEKeyword_4_0_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Alternatives_4_0"


    // $ANTLR start "rule__PrimitiveValueType__Alternatives"
    // InternalOperation.g:705:1: rule__PrimitiveValueType__Alternatives : ( ( ( 'int' ) ) | ( ( 'boolean' ) ) | ( ( 'float' ) ) | ( ( 'string' ) ) | ( ( 'object' ) ) | ( ( 'date' ) ) );
    public final void rule__PrimitiveValueType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:709:1: ( ( ( 'int' ) ) | ( ( 'boolean' ) ) | ( ( 'float' ) ) | ( ( 'string' ) ) | ( ( 'object' ) ) | ( ( 'date' ) ) )
            int alt7=6;
            switch ( input.LA(1) ) {
            case 15:
                {
                alt7=1;
                }
                break;
            case 16:
                {
                alt7=2;
                }
                break;
            case 17:
                {
                alt7=3;
                }
                break;
            case 18:
                {
                alt7=4;
                }
                break;
            case 19:
                {
                alt7=5;
                }
                break;
            case 20:
                {
                alt7=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // InternalOperation.g:710:2: ( ( 'int' ) )
                    {
                    // InternalOperation.g:710:2: ( ( 'int' ) )
                    // InternalOperation.g:711:3: ( 'int' )
                    {
                     before(grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0()); 
                    // InternalOperation.g:712:3: ( 'int' )
                    // InternalOperation.g:712:4: 'int'
                    {
                    match(input,15,FOLLOW_2); 

                    }

                     after(grammarAccess.getPrimitiveValueTypeAccess().getIntEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:716:2: ( ( 'boolean' ) )
                    {
                    // InternalOperation.g:716:2: ( ( 'boolean' ) )
                    // InternalOperation.g:717:3: ( 'boolean' )
                    {
                     before(grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1()); 
                    // InternalOperation.g:718:3: ( 'boolean' )
                    // InternalOperation.g:718:4: 'boolean'
                    {
                    match(input,16,FOLLOW_2); 

                    }

                     after(grammarAccess.getPrimitiveValueTypeAccess().getBooleanEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOperation.g:722:2: ( ( 'float' ) )
                    {
                    // InternalOperation.g:722:2: ( ( 'float' ) )
                    // InternalOperation.g:723:3: ( 'float' )
                    {
                     before(grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2()); 
                    // InternalOperation.g:724:3: ( 'float' )
                    // InternalOperation.g:724:4: 'float'
                    {
                    match(input,17,FOLLOW_2); 

                    }

                     after(grammarAccess.getPrimitiveValueTypeAccess().getFloatEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOperation.g:728:2: ( ( 'string' ) )
                    {
                    // InternalOperation.g:728:2: ( ( 'string' ) )
                    // InternalOperation.g:729:3: ( 'string' )
                    {
                     before(grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3()); 
                    // InternalOperation.g:730:3: ( 'string' )
                    // InternalOperation.g:730:4: 'string'
                    {
                    match(input,18,FOLLOW_2); 

                    }

                     after(grammarAccess.getPrimitiveValueTypeAccess().getStringEnumLiteralDeclaration_3()); 

                    }


                    }
                    break;
                case 5 :
                    // InternalOperation.g:734:2: ( ( 'object' ) )
                    {
                    // InternalOperation.g:734:2: ( ( 'object' ) )
                    // InternalOperation.g:735:3: ( 'object' )
                    {
                     before(grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4()); 
                    // InternalOperation.g:736:3: ( 'object' )
                    // InternalOperation.g:736:4: 'object'
                    {
                    match(input,19,FOLLOW_2); 

                    }

                     after(grammarAccess.getPrimitiveValueTypeAccess().getObjectEnumLiteralDeclaration_4()); 

                    }


                    }
                    break;
                case 6 :
                    // InternalOperation.g:740:2: ( ( 'date' ) )
                    {
                    // InternalOperation.g:740:2: ( ( 'date' ) )
                    // InternalOperation.g:741:3: ( 'date' )
                    {
                     before(grammarAccess.getPrimitiveValueTypeAccess().getDateEnumLiteralDeclaration_5()); 
                    // InternalOperation.g:742:3: ( 'date' )
                    // InternalOperation.g:742:4: 'date'
                    {
                    match(input,20,FOLLOW_2); 

                    }

                     after(grammarAccess.getPrimitiveValueTypeAccess().getDateEnumLiteralDeclaration_5()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValueType__Alternatives"


    // $ANTLR start "rule__OperationDescriptions__Group__0"
    // InternalOperation.g:750:1: rule__OperationDescriptions__Group__0 : rule__OperationDescriptions__Group__0__Impl rule__OperationDescriptions__Group__1 ;
    public final void rule__OperationDescriptions__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:754:1: ( rule__OperationDescriptions__Group__0__Impl rule__OperationDescriptions__Group__1 )
            // InternalOperation.g:755:2: rule__OperationDescriptions__Group__0__Impl rule__OperationDescriptions__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__OperationDescriptions__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__OperationDescriptions__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__Group__0"


    // $ANTLR start "rule__OperationDescriptions__Group__0__Impl"
    // InternalOperation.g:762:1: rule__OperationDescriptions__Group__0__Impl : ( () ) ;
    public final void rule__OperationDescriptions__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:766:1: ( ( () ) )
            // InternalOperation.g:767:1: ( () )
            {
            // InternalOperation.g:767:1: ( () )
            // InternalOperation.g:768:2: ()
            {
             before(grammarAccess.getOperationDescriptionsAccess().getOperationDescriptionsAction_0()); 
            // InternalOperation.g:769:2: ()
            // InternalOperation.g:769:3: 
            {
            }

             after(grammarAccess.getOperationDescriptionsAccess().getOperationDescriptionsAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__Group__0__Impl"


    // $ANTLR start "rule__OperationDescriptions__Group__1"
    // InternalOperation.g:777:1: rule__OperationDescriptions__Group__1 : rule__OperationDescriptions__Group__1__Impl ;
    public final void rule__OperationDescriptions__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:781:1: ( rule__OperationDescriptions__Group__1__Impl )
            // InternalOperation.g:782:2: rule__OperationDescriptions__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__OperationDescriptions__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__Group__1"


    // $ANTLR start "rule__OperationDescriptions__Group__1__Impl"
    // InternalOperation.g:788:1: rule__OperationDescriptions__Group__1__Impl : ( ( rule__OperationDescriptions__Group_1__0 )? ) ;
    public final void rule__OperationDescriptions__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:792:1: ( ( ( rule__OperationDescriptions__Group_1__0 )? ) )
            // InternalOperation.g:793:1: ( ( rule__OperationDescriptions__Group_1__0 )? )
            {
            // InternalOperation.g:793:1: ( ( rule__OperationDescriptions__Group_1__0 )? )
            // InternalOperation.g:794:2: ( rule__OperationDescriptions__Group_1__0 )?
            {
             before(grammarAccess.getOperationDescriptionsAccess().getGroup_1()); 
            // InternalOperation.g:795:2: ( rule__OperationDescriptions__Group_1__0 )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==21) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // InternalOperation.g:795:3: rule__OperationDescriptions__Group_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__OperationDescriptions__Group_1__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOperationDescriptionsAccess().getGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__Group__1__Impl"


    // $ANTLR start "rule__OperationDescriptions__Group_1__0"
    // InternalOperation.g:804:1: rule__OperationDescriptions__Group_1__0 : rule__OperationDescriptions__Group_1__0__Impl rule__OperationDescriptions__Group_1__1 ;
    public final void rule__OperationDescriptions__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:808:1: ( rule__OperationDescriptions__Group_1__0__Impl rule__OperationDescriptions__Group_1__1 )
            // InternalOperation.g:809:2: rule__OperationDescriptions__Group_1__0__Impl rule__OperationDescriptions__Group_1__1
            {
            pushFollow(FOLLOW_3);
            rule__OperationDescriptions__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__OperationDescriptions__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__Group_1__0"


    // $ANTLR start "rule__OperationDescriptions__Group_1__0__Impl"
    // InternalOperation.g:816:1: rule__OperationDescriptions__Group_1__0__Impl : ( ( rule__OperationDescriptions__OperationsAssignment_1_0 ) ) ;
    public final void rule__OperationDescriptions__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:820:1: ( ( ( rule__OperationDescriptions__OperationsAssignment_1_0 ) ) )
            // InternalOperation.g:821:1: ( ( rule__OperationDescriptions__OperationsAssignment_1_0 ) )
            {
            // InternalOperation.g:821:1: ( ( rule__OperationDescriptions__OperationsAssignment_1_0 ) )
            // InternalOperation.g:822:2: ( rule__OperationDescriptions__OperationsAssignment_1_0 )
            {
             before(grammarAccess.getOperationDescriptionsAccess().getOperationsAssignment_1_0()); 
            // InternalOperation.g:823:2: ( rule__OperationDescriptions__OperationsAssignment_1_0 )
            // InternalOperation.g:823:3: rule__OperationDescriptions__OperationsAssignment_1_0
            {
            pushFollow(FOLLOW_2);
            rule__OperationDescriptions__OperationsAssignment_1_0();

            state._fsp--;


            }

             after(grammarAccess.getOperationDescriptionsAccess().getOperationsAssignment_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__Group_1__0__Impl"


    // $ANTLR start "rule__OperationDescriptions__Group_1__1"
    // InternalOperation.g:831:1: rule__OperationDescriptions__Group_1__1 : rule__OperationDescriptions__Group_1__1__Impl ;
    public final void rule__OperationDescriptions__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:835:1: ( rule__OperationDescriptions__Group_1__1__Impl )
            // InternalOperation.g:836:2: rule__OperationDescriptions__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__OperationDescriptions__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__Group_1__1"


    // $ANTLR start "rule__OperationDescriptions__Group_1__1__Impl"
    // InternalOperation.g:842:1: rule__OperationDescriptions__Group_1__1__Impl : ( ( rule__OperationDescriptions__OperationsAssignment_1_1 )* ) ;
    public final void rule__OperationDescriptions__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:846:1: ( ( ( rule__OperationDescriptions__OperationsAssignment_1_1 )* ) )
            // InternalOperation.g:847:1: ( ( rule__OperationDescriptions__OperationsAssignment_1_1 )* )
            {
            // InternalOperation.g:847:1: ( ( rule__OperationDescriptions__OperationsAssignment_1_1 )* )
            // InternalOperation.g:848:2: ( rule__OperationDescriptions__OperationsAssignment_1_1 )*
            {
             before(grammarAccess.getOperationDescriptionsAccess().getOperationsAssignment_1_1()); 
            // InternalOperation.g:849:2: ( rule__OperationDescriptions__OperationsAssignment_1_1 )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==21) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // InternalOperation.g:849:3: rule__OperationDescriptions__OperationsAssignment_1_1
            	    {
            	    pushFollow(FOLLOW_4);
            	    rule__OperationDescriptions__OperationsAssignment_1_1();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

             after(grammarAccess.getOperationDescriptionsAccess().getOperationsAssignment_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__Group_1__1__Impl"


    // $ANTLR start "rule__Operation__Group__0"
    // InternalOperation.g:858:1: rule__Operation__Group__0 : rule__Operation__Group__0__Impl rule__Operation__Group__1 ;
    public final void rule__Operation__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:862:1: ( rule__Operation__Group__0__Impl rule__Operation__Group__1 )
            // InternalOperation.g:863:2: rule__Operation__Group__0__Impl rule__Operation__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__Operation__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__0"


    // $ANTLR start "rule__Operation__Group__0__Impl"
    // InternalOperation.g:870:1: rule__Operation__Group__0__Impl : ( () ) ;
    public final void rule__Operation__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:874:1: ( ( () ) )
            // InternalOperation.g:875:1: ( () )
            {
            // InternalOperation.g:875:1: ( () )
            // InternalOperation.g:876:2: ()
            {
             before(grammarAccess.getOperationAccess().getOperationAction_0()); 
            // InternalOperation.g:877:2: ()
            // InternalOperation.g:877:3: 
            {
            }

             after(grammarAccess.getOperationAccess().getOperationAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__0__Impl"


    // $ANTLR start "rule__Operation__Group__1"
    // InternalOperation.g:885:1: rule__Operation__Group__1 : rule__Operation__Group__1__Impl rule__Operation__Group__2 ;
    public final void rule__Operation__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:889:1: ( rule__Operation__Group__1__Impl rule__Operation__Group__2 )
            // InternalOperation.g:890:2: rule__Operation__Group__1__Impl rule__Operation__Group__2
            {
            pushFollow(FOLLOW_5);
            rule__Operation__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__1"


    // $ANTLR start "rule__Operation__Group__1__Impl"
    // InternalOperation.g:897:1: rule__Operation__Group__1__Impl : ( 'Operation' ) ;
    public final void rule__Operation__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:901:1: ( ( 'Operation' ) )
            // InternalOperation.g:902:1: ( 'Operation' )
            {
            // InternalOperation.g:902:1: ( 'Operation' )
            // InternalOperation.g:903:2: 'Operation'
            {
             before(grammarAccess.getOperationAccess().getOperationKeyword_1()); 
            match(input,21,FOLLOW_2); 
             after(grammarAccess.getOperationAccess().getOperationKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__1__Impl"


    // $ANTLR start "rule__Operation__Group__2"
    // InternalOperation.g:912:1: rule__Operation__Group__2 : rule__Operation__Group__2__Impl rule__Operation__Group__3 ;
    public final void rule__Operation__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:916:1: ( rule__Operation__Group__2__Impl rule__Operation__Group__3 )
            // InternalOperation.g:917:2: rule__Operation__Group__2__Impl rule__Operation__Group__3
            {
            pushFollow(FOLLOW_6);
            rule__Operation__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__2"


    // $ANTLR start "rule__Operation__Group__2__Impl"
    // InternalOperation.g:924:1: rule__Operation__Group__2__Impl : ( ( rule__Operation__NameAssignment_2 ) ) ;
    public final void rule__Operation__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:928:1: ( ( ( rule__Operation__NameAssignment_2 ) ) )
            // InternalOperation.g:929:1: ( ( rule__Operation__NameAssignment_2 ) )
            {
            // InternalOperation.g:929:1: ( ( rule__Operation__NameAssignment_2 ) )
            // InternalOperation.g:930:2: ( rule__Operation__NameAssignment_2 )
            {
             before(grammarAccess.getOperationAccess().getNameAssignment_2()); 
            // InternalOperation.g:931:2: ( rule__Operation__NameAssignment_2 )
            // InternalOperation.g:931:3: rule__Operation__NameAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__Operation__NameAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getOperationAccess().getNameAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__2__Impl"


    // $ANTLR start "rule__Operation__Group__3"
    // InternalOperation.g:939:1: rule__Operation__Group__3 : rule__Operation__Group__3__Impl rule__Operation__Group__4 ;
    public final void rule__Operation__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:943:1: ( rule__Operation__Group__3__Impl rule__Operation__Group__4 )
            // InternalOperation.g:944:2: rule__Operation__Group__3__Impl rule__Operation__Group__4
            {
            pushFollow(FOLLOW_7);
            rule__Operation__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__3"


    // $ANTLR start "rule__Operation__Group__3__Impl"
    // InternalOperation.g:951:1: rule__Operation__Group__3__Impl : ( '(' ) ;
    public final void rule__Operation__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:955:1: ( ( '(' ) )
            // InternalOperation.g:956:1: ( '(' )
            {
            // InternalOperation.g:956:1: ( '(' )
            // InternalOperation.g:957:2: '('
            {
             before(grammarAccess.getOperationAccess().getLeftParenthesisKeyword_3()); 
            match(input,22,FOLLOW_2); 
             after(grammarAccess.getOperationAccess().getLeftParenthesisKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__3__Impl"


    // $ANTLR start "rule__Operation__Group__4"
    // InternalOperation.g:966:1: rule__Operation__Group__4 : rule__Operation__Group__4__Impl rule__Operation__Group__5 ;
    public final void rule__Operation__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:970:1: ( rule__Operation__Group__4__Impl rule__Operation__Group__5 )
            // InternalOperation.g:971:2: rule__Operation__Group__4__Impl rule__Operation__Group__5
            {
            pushFollow(FOLLOW_7);
            rule__Operation__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__4"


    // $ANTLR start "rule__Operation__Group__4__Impl"
    // InternalOperation.g:978:1: rule__Operation__Group__4__Impl : ( ( rule__Operation__Group_4__0 )? ) ;
    public final void rule__Operation__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:982:1: ( ( ( rule__Operation__Group_4__0 )? ) )
            // InternalOperation.g:983:1: ( ( rule__Operation__Group_4__0 )? )
            {
            // InternalOperation.g:983:1: ( ( rule__Operation__Group_4__0 )? )
            // InternalOperation.g:984:2: ( rule__Operation__Group_4__0 )?
            {
             before(grammarAccess.getOperationAccess().getGroup_4()); 
            // InternalOperation.g:985:2: ( rule__Operation__Group_4__0 )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==RULE_ID||(LA10_0>=15 && LA10_0<=20)) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // InternalOperation.g:985:3: rule__Operation__Group_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__Operation__Group_4__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOperationAccess().getGroup_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__4__Impl"


    // $ANTLR start "rule__Operation__Group__5"
    // InternalOperation.g:993:1: rule__Operation__Group__5 : rule__Operation__Group__5__Impl rule__Operation__Group__6 ;
    public final void rule__Operation__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:997:1: ( rule__Operation__Group__5__Impl rule__Operation__Group__6 )
            // InternalOperation.g:998:2: rule__Operation__Group__5__Impl rule__Operation__Group__6
            {
            pushFollow(FOLLOW_8);
            rule__Operation__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__5"


    // $ANTLR start "rule__Operation__Group__5__Impl"
    // InternalOperation.g:1005:1: rule__Operation__Group__5__Impl : ( ')' ) ;
    public final void rule__Operation__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1009:1: ( ( ')' ) )
            // InternalOperation.g:1010:1: ( ')' )
            {
            // InternalOperation.g:1010:1: ( ')' )
            // InternalOperation.g:1011:2: ')'
            {
             before(grammarAccess.getOperationAccess().getRightParenthesisKeyword_5()); 
            match(input,23,FOLLOW_2); 
             after(grammarAccess.getOperationAccess().getRightParenthesisKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__5__Impl"


    // $ANTLR start "rule__Operation__Group__6"
    // InternalOperation.g:1020:1: rule__Operation__Group__6 : rule__Operation__Group__6__Impl rule__Operation__Group__7 ;
    public final void rule__Operation__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1024:1: ( rule__Operation__Group__6__Impl rule__Operation__Group__7 )
            // InternalOperation.g:1025:2: rule__Operation__Group__6__Impl rule__Operation__Group__7
            {
            pushFollow(FOLLOW_9);
            rule__Operation__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__6"


    // $ANTLR start "rule__Operation__Group__6__Impl"
    // InternalOperation.g:1032:1: rule__Operation__Group__6__Impl : ( '{' ) ;
    public final void rule__Operation__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1036:1: ( ( '{' ) )
            // InternalOperation.g:1037:1: ( '{' )
            {
            // InternalOperation.g:1037:1: ( '{' )
            // InternalOperation.g:1038:2: '{'
            {
             before(grammarAccess.getOperationAccess().getLeftCurlyBracketKeyword_6()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getOperationAccess().getLeftCurlyBracketKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__6__Impl"


    // $ANTLR start "rule__Operation__Group__7"
    // InternalOperation.g:1047:1: rule__Operation__Group__7 : rule__Operation__Group__7__Impl rule__Operation__Group__8 ;
    public final void rule__Operation__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1051:1: ( rule__Operation__Group__7__Impl rule__Operation__Group__8 )
            // InternalOperation.g:1052:2: rule__Operation__Group__7__Impl rule__Operation__Group__8
            {
            pushFollow(FOLLOW_9);
            rule__Operation__Group__7__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__8();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__7"


    // $ANTLR start "rule__Operation__Group__7__Impl"
    // InternalOperation.g:1059:1: rule__Operation__Group__7__Impl : ( ( rule__Operation__Group_7__0 )? ) ;
    public final void rule__Operation__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1063:1: ( ( ( rule__Operation__Group_7__0 )? ) )
            // InternalOperation.g:1064:1: ( ( rule__Operation__Group_7__0 )? )
            {
            // InternalOperation.g:1064:1: ( ( rule__Operation__Group_7__0 )? )
            // InternalOperation.g:1065:2: ( rule__Operation__Group_7__0 )?
            {
             before(grammarAccess.getOperationAccess().getGroup_7()); 
            // InternalOperation.g:1066:2: ( rule__Operation__Group_7__0 )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==27) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalOperation.g:1066:3: rule__Operation__Group_7__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__Operation__Group_7__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOperationAccess().getGroup_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__7__Impl"


    // $ANTLR start "rule__Operation__Group__8"
    // InternalOperation.g:1074:1: rule__Operation__Group__8 : rule__Operation__Group__8__Impl rule__Operation__Group__9 ;
    public final void rule__Operation__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1078:1: ( rule__Operation__Group__8__Impl rule__Operation__Group__9 )
            // InternalOperation.g:1079:2: rule__Operation__Group__8__Impl rule__Operation__Group__9
            {
            pushFollow(FOLLOW_9);
            rule__Operation__Group__8__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group__9();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__8"


    // $ANTLR start "rule__Operation__Group__8__Impl"
    // InternalOperation.g:1086:1: rule__Operation__Group__8__Impl : ( ( rule__Operation__Group_8__0 )? ) ;
    public final void rule__Operation__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1090:1: ( ( ( rule__Operation__Group_8__0 )? ) )
            // InternalOperation.g:1091:1: ( ( rule__Operation__Group_8__0 )? )
            {
            // InternalOperation.g:1091:1: ( ( rule__Operation__Group_8__0 )? )
            // InternalOperation.g:1092:2: ( rule__Operation__Group_8__0 )?
            {
             before(grammarAccess.getOperationAccess().getGroup_8()); 
            // InternalOperation.g:1093:2: ( rule__Operation__Group_8__0 )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==28) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // InternalOperation.g:1093:3: rule__Operation__Group_8__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__Operation__Group_8__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOperationAccess().getGroup_8()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__8__Impl"


    // $ANTLR start "rule__Operation__Group__9"
    // InternalOperation.g:1101:1: rule__Operation__Group__9 : rule__Operation__Group__9__Impl ;
    public final void rule__Operation__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1105:1: ( rule__Operation__Group__9__Impl )
            // InternalOperation.g:1106:2: rule__Operation__Group__9__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Operation__Group__9__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__9"


    // $ANTLR start "rule__Operation__Group__9__Impl"
    // InternalOperation.g:1112:1: rule__Operation__Group__9__Impl : ( '}' ) ;
    public final void rule__Operation__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1116:1: ( ( '}' ) )
            // InternalOperation.g:1117:1: ( '}' )
            {
            // InternalOperation.g:1117:1: ( '}' )
            // InternalOperation.g:1118:2: '}'
            {
             before(grammarAccess.getOperationAccess().getRightCurlyBracketKeyword_9()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getOperationAccess().getRightCurlyBracketKeyword_9()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group__9__Impl"


    // $ANTLR start "rule__Operation__Group_4__0"
    // InternalOperation.g:1128:1: rule__Operation__Group_4__0 : rule__Operation__Group_4__0__Impl rule__Operation__Group_4__1 ;
    public final void rule__Operation__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1132:1: ( rule__Operation__Group_4__0__Impl rule__Operation__Group_4__1 )
            // InternalOperation.g:1133:2: rule__Operation__Group_4__0__Impl rule__Operation__Group_4__1
            {
            pushFollow(FOLLOW_10);
            rule__Operation__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_4__0"


    // $ANTLR start "rule__Operation__Group_4__0__Impl"
    // InternalOperation.g:1140:1: rule__Operation__Group_4__0__Impl : ( ( rule__Operation__InputParametersAssignment_4_0 ) ) ;
    public final void rule__Operation__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1144:1: ( ( ( rule__Operation__InputParametersAssignment_4_0 ) ) )
            // InternalOperation.g:1145:1: ( ( rule__Operation__InputParametersAssignment_4_0 ) )
            {
            // InternalOperation.g:1145:1: ( ( rule__Operation__InputParametersAssignment_4_0 ) )
            // InternalOperation.g:1146:2: ( rule__Operation__InputParametersAssignment_4_0 )
            {
             before(grammarAccess.getOperationAccess().getInputParametersAssignment_4_0()); 
            // InternalOperation.g:1147:2: ( rule__Operation__InputParametersAssignment_4_0 )
            // InternalOperation.g:1147:3: rule__Operation__InputParametersAssignment_4_0
            {
            pushFollow(FOLLOW_2);
            rule__Operation__InputParametersAssignment_4_0();

            state._fsp--;


            }

             after(grammarAccess.getOperationAccess().getInputParametersAssignment_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_4__0__Impl"


    // $ANTLR start "rule__Operation__Group_4__1"
    // InternalOperation.g:1155:1: rule__Operation__Group_4__1 : rule__Operation__Group_4__1__Impl ;
    public final void rule__Operation__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1159:1: ( rule__Operation__Group_4__1__Impl )
            // InternalOperation.g:1160:2: rule__Operation__Group_4__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Operation__Group_4__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_4__1"


    // $ANTLR start "rule__Operation__Group_4__1__Impl"
    // InternalOperation.g:1166:1: rule__Operation__Group_4__1__Impl : ( ( rule__Operation__Group_4_1__0 )* ) ;
    public final void rule__Operation__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1170:1: ( ( ( rule__Operation__Group_4_1__0 )* ) )
            // InternalOperation.g:1171:1: ( ( rule__Operation__Group_4_1__0 )* )
            {
            // InternalOperation.g:1171:1: ( ( rule__Operation__Group_4_1__0 )* )
            // InternalOperation.g:1172:2: ( rule__Operation__Group_4_1__0 )*
            {
             before(grammarAccess.getOperationAccess().getGroup_4_1()); 
            // InternalOperation.g:1173:2: ( rule__Operation__Group_4_1__0 )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==26) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // InternalOperation.g:1173:3: rule__Operation__Group_4_1__0
            	    {
            	    pushFollow(FOLLOW_11);
            	    rule__Operation__Group_4_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

             after(grammarAccess.getOperationAccess().getGroup_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_4__1__Impl"


    // $ANTLR start "rule__Operation__Group_4_1__0"
    // InternalOperation.g:1182:1: rule__Operation__Group_4_1__0 : rule__Operation__Group_4_1__0__Impl rule__Operation__Group_4_1__1 ;
    public final void rule__Operation__Group_4_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1186:1: ( rule__Operation__Group_4_1__0__Impl rule__Operation__Group_4_1__1 )
            // InternalOperation.g:1187:2: rule__Operation__Group_4_1__0__Impl rule__Operation__Group_4_1__1
            {
            pushFollow(FOLLOW_12);
            rule__Operation__Group_4_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group_4_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_4_1__0"


    // $ANTLR start "rule__Operation__Group_4_1__0__Impl"
    // InternalOperation.g:1194:1: rule__Operation__Group_4_1__0__Impl : ( ',' ) ;
    public final void rule__Operation__Group_4_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1198:1: ( ( ',' ) )
            // InternalOperation.g:1199:1: ( ',' )
            {
            // InternalOperation.g:1199:1: ( ',' )
            // InternalOperation.g:1200:2: ','
            {
             before(grammarAccess.getOperationAccess().getCommaKeyword_4_1_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getOperationAccess().getCommaKeyword_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_4_1__0__Impl"


    // $ANTLR start "rule__Operation__Group_4_1__1"
    // InternalOperation.g:1209:1: rule__Operation__Group_4_1__1 : rule__Operation__Group_4_1__1__Impl ;
    public final void rule__Operation__Group_4_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1213:1: ( rule__Operation__Group_4_1__1__Impl )
            // InternalOperation.g:1214:2: rule__Operation__Group_4_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Operation__Group_4_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_4_1__1"


    // $ANTLR start "rule__Operation__Group_4_1__1__Impl"
    // InternalOperation.g:1220:1: rule__Operation__Group_4_1__1__Impl : ( ( rule__Operation__InputParametersAssignment_4_1_1 ) ) ;
    public final void rule__Operation__Group_4_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1224:1: ( ( ( rule__Operation__InputParametersAssignment_4_1_1 ) ) )
            // InternalOperation.g:1225:1: ( ( rule__Operation__InputParametersAssignment_4_1_1 ) )
            {
            // InternalOperation.g:1225:1: ( ( rule__Operation__InputParametersAssignment_4_1_1 ) )
            // InternalOperation.g:1226:2: ( rule__Operation__InputParametersAssignment_4_1_1 )
            {
             before(grammarAccess.getOperationAccess().getInputParametersAssignment_4_1_1()); 
            // InternalOperation.g:1227:2: ( rule__Operation__InputParametersAssignment_4_1_1 )
            // InternalOperation.g:1227:3: rule__Operation__InputParametersAssignment_4_1_1
            {
            pushFollow(FOLLOW_2);
            rule__Operation__InputParametersAssignment_4_1_1();

            state._fsp--;


            }

             after(grammarAccess.getOperationAccess().getInputParametersAssignment_4_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_4_1__1__Impl"


    // $ANTLR start "rule__Operation__Group_7__0"
    // InternalOperation.g:1236:1: rule__Operation__Group_7__0 : rule__Operation__Group_7__0__Impl rule__Operation__Group_7__1 ;
    public final void rule__Operation__Group_7__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1240:1: ( rule__Operation__Group_7__0__Impl rule__Operation__Group_7__1 )
            // InternalOperation.g:1241:2: rule__Operation__Group_7__0__Impl rule__Operation__Group_7__1
            {
            pushFollow(FOLLOW_5);
            rule__Operation__Group_7__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group_7__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_7__0"


    // $ANTLR start "rule__Operation__Group_7__0__Impl"
    // InternalOperation.g:1248:1: rule__Operation__Group_7__0__Impl : ( 'execute' ) ;
    public final void rule__Operation__Group_7__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1252:1: ( ( 'execute' ) )
            // InternalOperation.g:1253:1: ( 'execute' )
            {
            // InternalOperation.g:1253:1: ( 'execute' )
            // InternalOperation.g:1254:2: 'execute'
            {
             before(grammarAccess.getOperationAccess().getExecuteKeyword_7_0()); 
            match(input,27,FOLLOW_2); 
             after(grammarAccess.getOperationAccess().getExecuteKeyword_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_7__0__Impl"


    // $ANTLR start "rule__Operation__Group_7__1"
    // InternalOperation.g:1263:1: rule__Operation__Group_7__1 : rule__Operation__Group_7__1__Impl ;
    public final void rule__Operation__Group_7__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1267:1: ( rule__Operation__Group_7__1__Impl )
            // InternalOperation.g:1268:2: rule__Operation__Group_7__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Operation__Group_7__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_7__1"


    // $ANTLR start "rule__Operation__Group_7__1__Impl"
    // InternalOperation.g:1274:1: rule__Operation__Group_7__1__Impl : ( ( rule__Operation__ExecutableScriptAssignment_7_1 ) ) ;
    public final void rule__Operation__Group_7__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1278:1: ( ( ( rule__Operation__ExecutableScriptAssignment_7_1 ) ) )
            // InternalOperation.g:1279:1: ( ( rule__Operation__ExecutableScriptAssignment_7_1 ) )
            {
            // InternalOperation.g:1279:1: ( ( rule__Operation__ExecutableScriptAssignment_7_1 ) )
            // InternalOperation.g:1280:2: ( rule__Operation__ExecutableScriptAssignment_7_1 )
            {
             before(grammarAccess.getOperationAccess().getExecutableScriptAssignment_7_1()); 
            // InternalOperation.g:1281:2: ( rule__Operation__ExecutableScriptAssignment_7_1 )
            // InternalOperation.g:1281:3: rule__Operation__ExecutableScriptAssignment_7_1
            {
            pushFollow(FOLLOW_2);
            rule__Operation__ExecutableScriptAssignment_7_1();

            state._fsp--;


            }

             after(grammarAccess.getOperationAccess().getExecutableScriptAssignment_7_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_7__1__Impl"


    // $ANTLR start "rule__Operation__Group_8__0"
    // InternalOperation.g:1290:1: rule__Operation__Group_8__0 : rule__Operation__Group_8__0__Impl rule__Operation__Group_8__1 ;
    public final void rule__Operation__Group_8__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1294:1: ( rule__Operation__Group_8__0__Impl rule__Operation__Group_8__1 )
            // InternalOperation.g:1295:2: rule__Operation__Group_8__0__Impl rule__Operation__Group_8__1
            {
            pushFollow(FOLLOW_12);
            rule__Operation__Group_8__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Operation__Group_8__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_8__0"


    // $ANTLR start "rule__Operation__Group_8__0__Impl"
    // InternalOperation.g:1302:1: rule__Operation__Group_8__0__Impl : ( 'return' ) ;
    public final void rule__Operation__Group_8__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1306:1: ( ( 'return' ) )
            // InternalOperation.g:1307:1: ( 'return' )
            {
            // InternalOperation.g:1307:1: ( 'return' )
            // InternalOperation.g:1308:2: 'return'
            {
             before(grammarAccess.getOperationAccess().getReturnKeyword_8_0()); 
            match(input,28,FOLLOW_2); 
             after(grammarAccess.getOperationAccess().getReturnKeyword_8_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_8__0__Impl"


    // $ANTLR start "rule__Operation__Group_8__1"
    // InternalOperation.g:1317:1: rule__Operation__Group_8__1 : rule__Operation__Group_8__1__Impl ;
    public final void rule__Operation__Group_8__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1321:1: ( rule__Operation__Group_8__1__Impl )
            // InternalOperation.g:1322:2: rule__Operation__Group_8__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Operation__Group_8__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_8__1"


    // $ANTLR start "rule__Operation__Group_8__1__Impl"
    // InternalOperation.g:1328:1: rule__Operation__Group_8__1__Impl : ( ( rule__Operation__OutputParametersAssignment_8_1 ) ) ;
    public final void rule__Operation__Group_8__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1332:1: ( ( ( rule__Operation__OutputParametersAssignment_8_1 ) ) )
            // InternalOperation.g:1333:1: ( ( rule__Operation__OutputParametersAssignment_8_1 ) )
            {
            // InternalOperation.g:1333:1: ( ( rule__Operation__OutputParametersAssignment_8_1 ) )
            // InternalOperation.g:1334:2: ( rule__Operation__OutputParametersAssignment_8_1 )
            {
             before(grammarAccess.getOperationAccess().getOutputParametersAssignment_8_1()); 
            // InternalOperation.g:1335:2: ( rule__Operation__OutputParametersAssignment_8_1 )
            // InternalOperation.g:1335:3: rule__Operation__OutputParametersAssignment_8_1
            {
            pushFollow(FOLLOW_2);
            rule__Operation__OutputParametersAssignment_8_1();

            state._fsp--;


            }

             after(grammarAccess.getOperationAccess().getOutputParametersAssignment_8_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__Group_8__1__Impl"


    // $ANTLR start "rule__DataModel__Group_0__0"
    // InternalOperation.g:1344:1: rule__DataModel__Group_0__0 : rule__DataModel__Group_0__0__Impl rule__DataModel__Group_0__1 ;
    public final void rule__DataModel__Group_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1348:1: ( rule__DataModel__Group_0__0__Impl rule__DataModel__Group_0__1 )
            // InternalOperation.g:1349:2: rule__DataModel__Group_0__0__Impl rule__DataModel__Group_0__1
            {
            pushFollow(FOLLOW_5);
            rule__DataModel__Group_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0__0"


    // $ANTLR start "rule__DataModel__Group_0__0__Impl"
    // InternalOperation.g:1356:1: rule__DataModel__Group_0__0__Impl : ( 'DataModel' ) ;
    public final void rule__DataModel__Group_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1360:1: ( ( 'DataModel' ) )
            // InternalOperation.g:1361:1: ( 'DataModel' )
            {
            // InternalOperation.g:1361:1: ( 'DataModel' )
            // InternalOperation.g:1362:2: 'DataModel'
            {
             before(grammarAccess.getDataModelAccess().getDataModelKeyword_0_0()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getDataModelKeyword_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0__0__Impl"


    // $ANTLR start "rule__DataModel__Group_0__1"
    // InternalOperation.g:1371:1: rule__DataModel__Group_0__1 : rule__DataModel__Group_0__1__Impl rule__DataModel__Group_0__2 ;
    public final void rule__DataModel__Group_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1375:1: ( rule__DataModel__Group_0__1__Impl rule__DataModel__Group_0__2 )
            // InternalOperation.g:1376:2: rule__DataModel__Group_0__1__Impl rule__DataModel__Group_0__2
            {
            pushFollow(FOLLOW_8);
            rule__DataModel__Group_0__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0__1"


    // $ANTLR start "rule__DataModel__Group_0__1__Impl"
    // InternalOperation.g:1383:1: rule__DataModel__Group_0__1__Impl : ( ( rule__DataModel__NameAssignment_0_1 ) ) ;
    public final void rule__DataModel__Group_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1387:1: ( ( ( rule__DataModel__NameAssignment_0_1 ) ) )
            // InternalOperation.g:1388:1: ( ( rule__DataModel__NameAssignment_0_1 ) )
            {
            // InternalOperation.g:1388:1: ( ( rule__DataModel__NameAssignment_0_1 ) )
            // InternalOperation.g:1389:2: ( rule__DataModel__NameAssignment_0_1 )
            {
             before(grammarAccess.getDataModelAccess().getNameAssignment_0_1()); 
            // InternalOperation.g:1390:2: ( rule__DataModel__NameAssignment_0_1 )
            // InternalOperation.g:1390:3: rule__DataModel__NameAssignment_0_1
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__NameAssignment_0_1();

            state._fsp--;


            }

             after(grammarAccess.getDataModelAccess().getNameAssignment_0_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0__1__Impl"


    // $ANTLR start "rule__DataModel__Group_0__2"
    // InternalOperation.g:1398:1: rule__DataModel__Group_0__2 : rule__DataModel__Group_0__2__Impl rule__DataModel__Group_0__3 ;
    public final void rule__DataModel__Group_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1402:1: ( rule__DataModel__Group_0__2__Impl rule__DataModel__Group_0__3 )
            // InternalOperation.g:1403:2: rule__DataModel__Group_0__2__Impl rule__DataModel__Group_0__3
            {
            pushFollow(FOLLOW_13);
            rule__DataModel__Group_0__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0__2"


    // $ANTLR start "rule__DataModel__Group_0__2__Impl"
    // InternalOperation.g:1410:1: rule__DataModel__Group_0__2__Impl : ( '{' ) ;
    public final void rule__DataModel__Group_0__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1414:1: ( ( '{' ) )
            // InternalOperation.g:1415:1: ( '{' )
            {
            // InternalOperation.g:1415:1: ( '{' )
            // InternalOperation.g:1416:2: '{'
            {
             before(grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_2()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0__2__Impl"


    // $ANTLR start "rule__DataModel__Group_0__3"
    // InternalOperation.g:1425:1: rule__DataModel__Group_0__3 : rule__DataModel__Group_0__3__Impl ;
    public final void rule__DataModel__Group_0__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1429:1: ( rule__DataModel__Group_0__3__Impl )
            // InternalOperation.g:1430:2: rule__DataModel__Group_0__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0__3"


    // $ANTLR start "rule__DataModel__Group_0__3__Impl"
    // InternalOperation.g:1436:1: rule__DataModel__Group_0__3__Impl : ( ( rule__DataModel__Group_0_3__0 )? ) ;
    public final void rule__DataModel__Group_0__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1440:1: ( ( ( rule__DataModel__Group_0_3__0 )? ) )
            // InternalOperation.g:1441:1: ( ( rule__DataModel__Group_0_3__0 )? )
            {
            // InternalOperation.g:1441:1: ( ( rule__DataModel__Group_0_3__0 )? )
            // InternalOperation.g:1442:2: ( rule__DataModel__Group_0_3__0 )?
            {
             before(grammarAccess.getDataModelAccess().getGroup_0_3()); 
            // InternalOperation.g:1443:2: ( rule__DataModel__Group_0_3__0 )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==30) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // InternalOperation.g:1443:3: rule__DataModel__Group_0_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__DataModel__Group_0_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getDataModelAccess().getGroup_0_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0__3__Impl"


    // $ANTLR start "rule__DataModel__Group_0_3__0"
    // InternalOperation.g:1452:1: rule__DataModel__Group_0_3__0 : rule__DataModel__Group_0_3__0__Impl rule__DataModel__Group_0_3__1 ;
    public final void rule__DataModel__Group_0_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1456:1: ( rule__DataModel__Group_0_3__0__Impl rule__DataModel__Group_0_3__1 )
            // InternalOperation.g:1457:2: rule__DataModel__Group_0_3__0__Impl rule__DataModel__Group_0_3__1
            {
            pushFollow(FOLLOW_8);
            rule__DataModel__Group_0_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__0"


    // $ANTLR start "rule__DataModel__Group_0_3__0__Impl"
    // InternalOperation.g:1464:1: rule__DataModel__Group_0_3__0__Impl : ( 'primitives' ) ;
    public final void rule__DataModel__Group_0_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1468:1: ( ( 'primitives' ) )
            // InternalOperation.g:1469:1: ( 'primitives' )
            {
            // InternalOperation.g:1469:1: ( 'primitives' )
            // InternalOperation.g:1470:2: 'primitives'
            {
             before(grammarAccess.getDataModelAccess().getPrimitivesKeyword_0_3_0()); 
            match(input,30,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getPrimitivesKeyword_0_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__0__Impl"


    // $ANTLR start "rule__DataModel__Group_0_3__1"
    // InternalOperation.g:1479:1: rule__DataModel__Group_0_3__1 : rule__DataModel__Group_0_3__1__Impl rule__DataModel__Group_0_3__2 ;
    public final void rule__DataModel__Group_0_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1483:1: ( rule__DataModel__Group_0_3__1__Impl rule__DataModel__Group_0_3__2 )
            // InternalOperation.g:1484:2: rule__DataModel__Group_0_3__1__Impl rule__DataModel__Group_0_3__2
            {
            pushFollow(FOLLOW_12);
            rule__DataModel__Group_0_3__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0_3__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__1"


    // $ANTLR start "rule__DataModel__Group_0_3__1__Impl"
    // InternalOperation.g:1491:1: rule__DataModel__Group_0_3__1__Impl : ( '{' ) ;
    public final void rule__DataModel__Group_0_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1495:1: ( ( '{' ) )
            // InternalOperation.g:1496:1: ( '{' )
            {
            // InternalOperation.g:1496:1: ( '{' )
            // InternalOperation.g:1497:2: '{'
            {
             before(grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_3_1()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_0_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__1__Impl"


    // $ANTLR start "rule__DataModel__Group_0_3__2"
    // InternalOperation.g:1506:1: rule__DataModel__Group_0_3__2 : rule__DataModel__Group_0_3__2__Impl rule__DataModel__Group_0_3__3 ;
    public final void rule__DataModel__Group_0_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1510:1: ( rule__DataModel__Group_0_3__2__Impl rule__DataModel__Group_0_3__3 )
            // InternalOperation.g:1511:2: rule__DataModel__Group_0_3__2__Impl rule__DataModel__Group_0_3__3
            {
            pushFollow(FOLLOW_14);
            rule__DataModel__Group_0_3__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0_3__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__2"


    // $ANTLR start "rule__DataModel__Group_0_3__2__Impl"
    // InternalOperation.g:1518:1: rule__DataModel__Group_0_3__2__Impl : ( ( rule__DataModel__PrimitivesAssignment_0_3_2 ) ) ;
    public final void rule__DataModel__Group_0_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1522:1: ( ( ( rule__DataModel__PrimitivesAssignment_0_3_2 ) ) )
            // InternalOperation.g:1523:1: ( ( rule__DataModel__PrimitivesAssignment_0_3_2 ) )
            {
            // InternalOperation.g:1523:1: ( ( rule__DataModel__PrimitivesAssignment_0_3_2 ) )
            // InternalOperation.g:1524:2: ( rule__DataModel__PrimitivesAssignment_0_3_2 )
            {
             before(grammarAccess.getDataModelAccess().getPrimitivesAssignment_0_3_2()); 
            // InternalOperation.g:1525:2: ( rule__DataModel__PrimitivesAssignment_0_3_2 )
            // InternalOperation.g:1525:3: rule__DataModel__PrimitivesAssignment_0_3_2
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__PrimitivesAssignment_0_3_2();

            state._fsp--;


            }

             after(grammarAccess.getDataModelAccess().getPrimitivesAssignment_0_3_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__2__Impl"


    // $ANTLR start "rule__DataModel__Group_0_3__3"
    // InternalOperation.g:1533:1: rule__DataModel__Group_0_3__3 : rule__DataModel__Group_0_3__3__Impl rule__DataModel__Group_0_3__4 ;
    public final void rule__DataModel__Group_0_3__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1537:1: ( rule__DataModel__Group_0_3__3__Impl rule__DataModel__Group_0_3__4 )
            // InternalOperation.g:1538:2: rule__DataModel__Group_0_3__3__Impl rule__DataModel__Group_0_3__4
            {
            pushFollow(FOLLOW_14);
            rule__DataModel__Group_0_3__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0_3__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__3"


    // $ANTLR start "rule__DataModel__Group_0_3__3__Impl"
    // InternalOperation.g:1545:1: rule__DataModel__Group_0_3__3__Impl : ( ( rule__DataModel__Group_0_3_3__0 )* ) ;
    public final void rule__DataModel__Group_0_3__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1549:1: ( ( ( rule__DataModel__Group_0_3_3__0 )* ) )
            // InternalOperation.g:1550:1: ( ( rule__DataModel__Group_0_3_3__0 )* )
            {
            // InternalOperation.g:1550:1: ( ( rule__DataModel__Group_0_3_3__0 )* )
            // InternalOperation.g:1551:2: ( rule__DataModel__Group_0_3_3__0 )*
            {
             before(grammarAccess.getDataModelAccess().getGroup_0_3_3()); 
            // InternalOperation.g:1552:2: ( rule__DataModel__Group_0_3_3__0 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==26) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // InternalOperation.g:1552:3: rule__DataModel__Group_0_3_3__0
            	    {
            	    pushFollow(FOLLOW_11);
            	    rule__DataModel__Group_0_3_3__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

             after(grammarAccess.getDataModelAccess().getGroup_0_3_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__3__Impl"


    // $ANTLR start "rule__DataModel__Group_0_3__4"
    // InternalOperation.g:1560:1: rule__DataModel__Group_0_3__4 : rule__DataModel__Group_0_3__4__Impl ;
    public final void rule__DataModel__Group_0_3__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1564:1: ( rule__DataModel__Group_0_3__4__Impl )
            // InternalOperation.g:1565:2: rule__DataModel__Group_0_3__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0_3__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__4"


    // $ANTLR start "rule__DataModel__Group_0_3__4__Impl"
    // InternalOperation.g:1571:1: rule__DataModel__Group_0_3__4__Impl : ( '}' ) ;
    public final void rule__DataModel__Group_0_3__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1575:1: ( ( '}' ) )
            // InternalOperation.g:1576:1: ( '}' )
            {
            // InternalOperation.g:1576:1: ( '}' )
            // InternalOperation.g:1577:2: '}'
            {
             before(grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_0_3_4()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_0_3_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3__4__Impl"


    // $ANTLR start "rule__DataModel__Group_0_3_3__0"
    // InternalOperation.g:1587:1: rule__DataModel__Group_0_3_3__0 : rule__DataModel__Group_0_3_3__0__Impl rule__DataModel__Group_0_3_3__1 ;
    public final void rule__DataModel__Group_0_3_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1591:1: ( rule__DataModel__Group_0_3_3__0__Impl rule__DataModel__Group_0_3_3__1 )
            // InternalOperation.g:1592:2: rule__DataModel__Group_0_3_3__0__Impl rule__DataModel__Group_0_3_3__1
            {
            pushFollow(FOLLOW_12);
            rule__DataModel__Group_0_3_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0_3_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3_3__0"


    // $ANTLR start "rule__DataModel__Group_0_3_3__0__Impl"
    // InternalOperation.g:1599:1: rule__DataModel__Group_0_3_3__0__Impl : ( ',' ) ;
    public final void rule__DataModel__Group_0_3_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1603:1: ( ( ',' ) )
            // InternalOperation.g:1604:1: ( ',' )
            {
            // InternalOperation.g:1604:1: ( ',' )
            // InternalOperation.g:1605:2: ','
            {
             before(grammarAccess.getDataModelAccess().getCommaKeyword_0_3_3_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getCommaKeyword_0_3_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3_3__0__Impl"


    // $ANTLR start "rule__DataModel__Group_0_3_3__1"
    // InternalOperation.g:1614:1: rule__DataModel__Group_0_3_3__1 : rule__DataModel__Group_0_3_3__1__Impl ;
    public final void rule__DataModel__Group_0_3_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1618:1: ( rule__DataModel__Group_0_3_3__1__Impl )
            // InternalOperation.g:1619:2: rule__DataModel__Group_0_3_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__Group_0_3_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3_3__1"


    // $ANTLR start "rule__DataModel__Group_0_3_3__1__Impl"
    // InternalOperation.g:1625:1: rule__DataModel__Group_0_3_3__1__Impl : ( ( rule__DataModel__PrimitivesAssignment_0_3_3_1 ) ) ;
    public final void rule__DataModel__Group_0_3_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1629:1: ( ( ( rule__DataModel__PrimitivesAssignment_0_3_3_1 ) ) )
            // InternalOperation.g:1630:1: ( ( rule__DataModel__PrimitivesAssignment_0_3_3_1 ) )
            {
            // InternalOperation.g:1630:1: ( ( rule__DataModel__PrimitivesAssignment_0_3_3_1 ) )
            // InternalOperation.g:1631:2: ( rule__DataModel__PrimitivesAssignment_0_3_3_1 )
            {
             before(grammarAccess.getDataModelAccess().getPrimitivesAssignment_0_3_3_1()); 
            // InternalOperation.g:1632:2: ( rule__DataModel__PrimitivesAssignment_0_3_3_1 )
            // InternalOperation.g:1632:3: rule__DataModel__PrimitivesAssignment_0_3_3_1
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__PrimitivesAssignment_0_3_3_1();

            state._fsp--;


            }

             after(grammarAccess.getDataModelAccess().getPrimitivesAssignment_0_3_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_0_3_3__1__Impl"


    // $ANTLR start "rule__DataModel__Group_1__0"
    // InternalOperation.g:1641:1: rule__DataModel__Group_1__0 : rule__DataModel__Group_1__0__Impl rule__DataModel__Group_1__1 ;
    public final void rule__DataModel__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1645:1: ( rule__DataModel__Group_1__0__Impl rule__DataModel__Group_1__1 )
            // InternalOperation.g:1646:2: rule__DataModel__Group_1__0__Impl rule__DataModel__Group_1__1
            {
            pushFollow(FOLLOW_15);
            rule__DataModel__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1__0"


    // $ANTLR start "rule__DataModel__Group_1__0__Impl"
    // InternalOperation.g:1653:1: rule__DataModel__Group_1__0__Impl : ( ( rule__DataModel__Group_1_0__0 )? ) ;
    public final void rule__DataModel__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1657:1: ( ( ( rule__DataModel__Group_1_0__0 )? ) )
            // InternalOperation.g:1658:1: ( ( rule__DataModel__Group_1_0__0 )? )
            {
            // InternalOperation.g:1658:1: ( ( rule__DataModel__Group_1_0__0 )? )
            // InternalOperation.g:1659:2: ( rule__DataModel__Group_1_0__0 )?
            {
             before(grammarAccess.getDataModelAccess().getGroup_1_0()); 
            // InternalOperation.g:1660:2: ( rule__DataModel__Group_1_0__0 )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==31) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalOperation.g:1660:3: rule__DataModel__Group_1_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__DataModel__Group_1_0__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getDataModelAccess().getGroup_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1__0__Impl"


    // $ANTLR start "rule__DataModel__Group_1__1"
    // InternalOperation.g:1668:1: rule__DataModel__Group_1__1 : rule__DataModel__Group_1__1__Impl ;
    public final void rule__DataModel__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1672:1: ( rule__DataModel__Group_1__1__Impl )
            // InternalOperation.g:1673:2: rule__DataModel__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1__1"


    // $ANTLR start "rule__DataModel__Group_1__1__Impl"
    // InternalOperation.g:1679:1: rule__DataModel__Group_1__1__Impl : ( '}' ) ;
    public final void rule__DataModel__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1683:1: ( ( '}' ) )
            // InternalOperation.g:1684:1: ( '}' )
            {
            // InternalOperation.g:1684:1: ( '}' )
            // InternalOperation.g:1685:2: '}'
            {
             before(grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_1()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1__1__Impl"


    // $ANTLR start "rule__DataModel__Group_1_0__0"
    // InternalOperation.g:1695:1: rule__DataModel__Group_1_0__0 : rule__DataModel__Group_1_0__0__Impl rule__DataModel__Group_1_0__1 ;
    public final void rule__DataModel__Group_1_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1699:1: ( rule__DataModel__Group_1_0__0__Impl rule__DataModel__Group_1_0__1 )
            // InternalOperation.g:1700:2: rule__DataModel__Group_1_0__0__Impl rule__DataModel__Group_1_0__1
            {
            pushFollow(FOLLOW_8);
            rule__DataModel__Group_1_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1_0__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__0"


    // $ANTLR start "rule__DataModel__Group_1_0__0__Impl"
    // InternalOperation.g:1707:1: rule__DataModel__Group_1_0__0__Impl : ( 'composites' ) ;
    public final void rule__DataModel__Group_1_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1711:1: ( ( 'composites' ) )
            // InternalOperation.g:1712:1: ( 'composites' )
            {
            // InternalOperation.g:1712:1: ( 'composites' )
            // InternalOperation.g:1713:2: 'composites'
            {
             before(grammarAccess.getDataModelAccess().getCompositesKeyword_1_0_0()); 
            match(input,31,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getCompositesKeyword_1_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__0__Impl"


    // $ANTLR start "rule__DataModel__Group_1_0__1"
    // InternalOperation.g:1722:1: rule__DataModel__Group_1_0__1 : rule__DataModel__Group_1_0__1__Impl rule__DataModel__Group_1_0__2 ;
    public final void rule__DataModel__Group_1_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1726:1: ( rule__DataModel__Group_1_0__1__Impl rule__DataModel__Group_1_0__2 )
            // InternalOperation.g:1727:2: rule__DataModel__Group_1_0__1__Impl rule__DataModel__Group_1_0__2
            {
            pushFollow(FOLLOW_16);
            rule__DataModel__Group_1_0__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1_0__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__1"


    // $ANTLR start "rule__DataModel__Group_1_0__1__Impl"
    // InternalOperation.g:1734:1: rule__DataModel__Group_1_0__1__Impl : ( '{' ) ;
    public final void rule__DataModel__Group_1_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1738:1: ( ( '{' ) )
            // InternalOperation.g:1739:1: ( '{' )
            {
            // InternalOperation.g:1739:1: ( '{' )
            // InternalOperation.g:1740:2: '{'
            {
             before(grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_1_0_1()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getLeftCurlyBracketKeyword_1_0_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__1__Impl"


    // $ANTLR start "rule__DataModel__Group_1_0__2"
    // InternalOperation.g:1749:1: rule__DataModel__Group_1_0__2 : rule__DataModel__Group_1_0__2__Impl rule__DataModel__Group_1_0__3 ;
    public final void rule__DataModel__Group_1_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1753:1: ( rule__DataModel__Group_1_0__2__Impl rule__DataModel__Group_1_0__3 )
            // InternalOperation.g:1754:2: rule__DataModel__Group_1_0__2__Impl rule__DataModel__Group_1_0__3
            {
            pushFollow(FOLLOW_14);
            rule__DataModel__Group_1_0__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1_0__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__2"


    // $ANTLR start "rule__DataModel__Group_1_0__2__Impl"
    // InternalOperation.g:1761:1: rule__DataModel__Group_1_0__2__Impl : ( ( rule__DataModel__CompositesAssignment_1_0_2 ) ) ;
    public final void rule__DataModel__Group_1_0__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1765:1: ( ( ( rule__DataModel__CompositesAssignment_1_0_2 ) ) )
            // InternalOperation.g:1766:1: ( ( rule__DataModel__CompositesAssignment_1_0_2 ) )
            {
            // InternalOperation.g:1766:1: ( ( rule__DataModel__CompositesAssignment_1_0_2 ) )
            // InternalOperation.g:1767:2: ( rule__DataModel__CompositesAssignment_1_0_2 )
            {
             before(grammarAccess.getDataModelAccess().getCompositesAssignment_1_0_2()); 
            // InternalOperation.g:1768:2: ( rule__DataModel__CompositesAssignment_1_0_2 )
            // InternalOperation.g:1768:3: rule__DataModel__CompositesAssignment_1_0_2
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__CompositesAssignment_1_0_2();

            state._fsp--;


            }

             after(grammarAccess.getDataModelAccess().getCompositesAssignment_1_0_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__2__Impl"


    // $ANTLR start "rule__DataModel__Group_1_0__3"
    // InternalOperation.g:1776:1: rule__DataModel__Group_1_0__3 : rule__DataModel__Group_1_0__3__Impl rule__DataModel__Group_1_0__4 ;
    public final void rule__DataModel__Group_1_0__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1780:1: ( rule__DataModel__Group_1_0__3__Impl rule__DataModel__Group_1_0__4 )
            // InternalOperation.g:1781:2: rule__DataModel__Group_1_0__3__Impl rule__DataModel__Group_1_0__4
            {
            pushFollow(FOLLOW_14);
            rule__DataModel__Group_1_0__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1_0__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__3"


    // $ANTLR start "rule__DataModel__Group_1_0__3__Impl"
    // InternalOperation.g:1788:1: rule__DataModel__Group_1_0__3__Impl : ( ( rule__DataModel__Group_1_0_3__0 )* ) ;
    public final void rule__DataModel__Group_1_0__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1792:1: ( ( ( rule__DataModel__Group_1_0_3__0 )* ) )
            // InternalOperation.g:1793:1: ( ( rule__DataModel__Group_1_0_3__0 )* )
            {
            // InternalOperation.g:1793:1: ( ( rule__DataModel__Group_1_0_3__0 )* )
            // InternalOperation.g:1794:2: ( rule__DataModel__Group_1_0_3__0 )*
            {
             before(grammarAccess.getDataModelAccess().getGroup_1_0_3()); 
            // InternalOperation.g:1795:2: ( rule__DataModel__Group_1_0_3__0 )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==26) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // InternalOperation.g:1795:3: rule__DataModel__Group_1_0_3__0
            	    {
            	    pushFollow(FOLLOW_11);
            	    rule__DataModel__Group_1_0_3__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

             after(grammarAccess.getDataModelAccess().getGroup_1_0_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__3__Impl"


    // $ANTLR start "rule__DataModel__Group_1_0__4"
    // InternalOperation.g:1803:1: rule__DataModel__Group_1_0__4 : rule__DataModel__Group_1_0__4__Impl ;
    public final void rule__DataModel__Group_1_0__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1807:1: ( rule__DataModel__Group_1_0__4__Impl )
            // InternalOperation.g:1808:2: rule__DataModel__Group_1_0__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1_0__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__4"


    // $ANTLR start "rule__DataModel__Group_1_0__4__Impl"
    // InternalOperation.g:1814:1: rule__DataModel__Group_1_0__4__Impl : ( '}' ) ;
    public final void rule__DataModel__Group_1_0__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1818:1: ( ( '}' ) )
            // InternalOperation.g:1819:1: ( '}' )
            {
            // InternalOperation.g:1819:1: ( '}' )
            // InternalOperation.g:1820:2: '}'
            {
             before(grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_0_4()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getRightCurlyBracketKeyword_1_0_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0__4__Impl"


    // $ANTLR start "rule__DataModel__Group_1_0_3__0"
    // InternalOperation.g:1830:1: rule__DataModel__Group_1_0_3__0 : rule__DataModel__Group_1_0_3__0__Impl rule__DataModel__Group_1_0_3__1 ;
    public final void rule__DataModel__Group_1_0_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1834:1: ( rule__DataModel__Group_1_0_3__0__Impl rule__DataModel__Group_1_0_3__1 )
            // InternalOperation.g:1835:2: rule__DataModel__Group_1_0_3__0__Impl rule__DataModel__Group_1_0_3__1
            {
            pushFollow(FOLLOW_16);
            rule__DataModel__Group_1_0_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1_0_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0_3__0"


    // $ANTLR start "rule__DataModel__Group_1_0_3__0__Impl"
    // InternalOperation.g:1842:1: rule__DataModel__Group_1_0_3__0__Impl : ( ',' ) ;
    public final void rule__DataModel__Group_1_0_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1846:1: ( ( ',' ) )
            // InternalOperation.g:1847:1: ( ',' )
            {
            // InternalOperation.g:1847:1: ( ',' )
            // InternalOperation.g:1848:2: ','
            {
             before(grammarAccess.getDataModelAccess().getCommaKeyword_1_0_3_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getCommaKeyword_1_0_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0_3__0__Impl"


    // $ANTLR start "rule__DataModel__Group_1_0_3__1"
    // InternalOperation.g:1857:1: rule__DataModel__Group_1_0_3__1 : rule__DataModel__Group_1_0_3__1__Impl ;
    public final void rule__DataModel__Group_1_0_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1861:1: ( rule__DataModel__Group_1_0_3__1__Impl )
            // InternalOperation.g:1862:2: rule__DataModel__Group_1_0_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__Group_1_0_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0_3__1"


    // $ANTLR start "rule__DataModel__Group_1_0_3__1__Impl"
    // InternalOperation.g:1868:1: rule__DataModel__Group_1_0_3__1__Impl : ( ( rule__DataModel__CompositesAssignment_1_0_3_1 ) ) ;
    public final void rule__DataModel__Group_1_0_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1872:1: ( ( ( rule__DataModel__CompositesAssignment_1_0_3_1 ) ) )
            // InternalOperation.g:1873:1: ( ( rule__DataModel__CompositesAssignment_1_0_3_1 ) )
            {
            // InternalOperation.g:1873:1: ( ( rule__DataModel__CompositesAssignment_1_0_3_1 ) )
            // InternalOperation.g:1874:2: ( rule__DataModel__CompositesAssignment_1_0_3_1 )
            {
             before(grammarAccess.getDataModelAccess().getCompositesAssignment_1_0_3_1()); 
            // InternalOperation.g:1875:2: ( rule__DataModel__CompositesAssignment_1_0_3_1 )
            // InternalOperation.g:1875:3: rule__DataModel__CompositesAssignment_1_0_3_1
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__CompositesAssignment_1_0_3_1();

            state._fsp--;


            }

             after(grammarAccess.getDataModelAccess().getCompositesAssignment_1_0_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__Group_1_0_3__1__Impl"


    // $ANTLR start "rule__QualifiedName__Group__0"
    // InternalOperation.g:1884:1: rule__QualifiedName__Group__0 : rule__QualifiedName__Group__0__Impl rule__QualifiedName__Group__1 ;
    public final void rule__QualifiedName__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1888:1: ( rule__QualifiedName__Group__0__Impl rule__QualifiedName__Group__1 )
            // InternalOperation.g:1889:2: rule__QualifiedName__Group__0__Impl rule__QualifiedName__Group__1
            {
            pushFollow(FOLLOW_17);
            rule__QualifiedName__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__QualifiedName__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedName__Group__0"


    // $ANTLR start "rule__QualifiedName__Group__0__Impl"
    // InternalOperation.g:1896:1: rule__QualifiedName__Group__0__Impl : ( RULE_ID ) ;
    public final void rule__QualifiedName__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1900:1: ( ( RULE_ID ) )
            // InternalOperation.g:1901:1: ( RULE_ID )
            {
            // InternalOperation.g:1901:1: ( RULE_ID )
            // InternalOperation.g:1902:2: RULE_ID
            {
             before(grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedName__Group__0__Impl"


    // $ANTLR start "rule__QualifiedName__Group__1"
    // InternalOperation.g:1911:1: rule__QualifiedName__Group__1 : rule__QualifiedName__Group__1__Impl ;
    public final void rule__QualifiedName__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1915:1: ( rule__QualifiedName__Group__1__Impl )
            // InternalOperation.g:1916:2: rule__QualifiedName__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__QualifiedName__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedName__Group__1"


    // $ANTLR start "rule__QualifiedName__Group__1__Impl"
    // InternalOperation.g:1922:1: rule__QualifiedName__Group__1__Impl : ( ( rule__QualifiedName__Group_1__0 )* ) ;
    public final void rule__QualifiedName__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1926:1: ( ( ( rule__QualifiedName__Group_1__0 )* ) )
            // InternalOperation.g:1927:1: ( ( rule__QualifiedName__Group_1__0 )* )
            {
            // InternalOperation.g:1927:1: ( ( rule__QualifiedName__Group_1__0 )* )
            // InternalOperation.g:1928:2: ( rule__QualifiedName__Group_1__0 )*
            {
             before(grammarAccess.getQualifiedNameAccess().getGroup_1()); 
            // InternalOperation.g:1929:2: ( rule__QualifiedName__Group_1__0 )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==32) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // InternalOperation.g:1929:3: rule__QualifiedName__Group_1__0
            	    {
            	    pushFollow(FOLLOW_18);
            	    rule__QualifiedName__Group_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

             after(grammarAccess.getQualifiedNameAccess().getGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedName__Group__1__Impl"


    // $ANTLR start "rule__QualifiedName__Group_1__0"
    // InternalOperation.g:1938:1: rule__QualifiedName__Group_1__0 : rule__QualifiedName__Group_1__0__Impl rule__QualifiedName__Group_1__1 ;
    public final void rule__QualifiedName__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1942:1: ( rule__QualifiedName__Group_1__0__Impl rule__QualifiedName__Group_1__1 )
            // InternalOperation.g:1943:2: rule__QualifiedName__Group_1__0__Impl rule__QualifiedName__Group_1__1
            {
            pushFollow(FOLLOW_16);
            rule__QualifiedName__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__QualifiedName__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedName__Group_1__0"


    // $ANTLR start "rule__QualifiedName__Group_1__0__Impl"
    // InternalOperation.g:1950:1: rule__QualifiedName__Group_1__0__Impl : ( '.' ) ;
    public final void rule__QualifiedName__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1954:1: ( ( '.' ) )
            // InternalOperation.g:1955:1: ( '.' )
            {
            // InternalOperation.g:1955:1: ( '.' )
            // InternalOperation.g:1956:2: '.'
            {
             before(grammarAccess.getQualifiedNameAccess().getFullStopKeyword_1_0()); 
            match(input,32,FOLLOW_2); 
             after(grammarAccess.getQualifiedNameAccess().getFullStopKeyword_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedName__Group_1__0__Impl"


    // $ANTLR start "rule__QualifiedName__Group_1__1"
    // InternalOperation.g:1965:1: rule__QualifiedName__Group_1__1 : rule__QualifiedName__Group_1__1__Impl ;
    public final void rule__QualifiedName__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1969:1: ( rule__QualifiedName__Group_1__1__Impl )
            // InternalOperation.g:1970:2: rule__QualifiedName__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__QualifiedName__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedName__Group_1__1"


    // $ANTLR start "rule__QualifiedName__Group_1__1__Impl"
    // InternalOperation.g:1976:1: rule__QualifiedName__Group_1__1__Impl : ( RULE_ID ) ;
    public final void rule__QualifiedName__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1980:1: ( ( RULE_ID ) )
            // InternalOperation.g:1981:1: ( RULE_ID )
            {
            // InternalOperation.g:1981:1: ( RULE_ID )
            // InternalOperation.g:1982:2: RULE_ID
            {
             before(grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_1_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getQualifiedNameAccess().getIDTerminalRuleCall_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedName__Group_1__1__Impl"


    // $ANTLR start "rule__SimpleType__Group__0"
    // InternalOperation.g:1992:1: rule__SimpleType__Group__0 : rule__SimpleType__Group__0__Impl rule__SimpleType__Group__1 ;
    public final void rule__SimpleType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:1996:1: ( rule__SimpleType__Group__0__Impl rule__SimpleType__Group__1 )
            // InternalOperation.g:1997:2: rule__SimpleType__Group__0__Impl rule__SimpleType__Group__1
            {
            pushFollow(FOLLOW_19);
            rule__SimpleType__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__SimpleType__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group__0"


    // $ANTLR start "rule__SimpleType__Group__0__Impl"
    // InternalOperation.g:2004:1: rule__SimpleType__Group__0__Impl : ( () ) ;
    public final void rule__SimpleType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2008:1: ( ( () ) )
            // InternalOperation.g:2009:1: ( () )
            {
            // InternalOperation.g:2009:1: ( () )
            // InternalOperation.g:2010:2: ()
            {
             before(grammarAccess.getSimpleTypeAccess().getSimpleTypeAction_0()); 
            // InternalOperation.g:2011:2: ()
            // InternalOperation.g:2011:3: 
            {
            }

             after(grammarAccess.getSimpleTypeAccess().getSimpleTypeAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group__0__Impl"


    // $ANTLR start "rule__SimpleType__Group__1"
    // InternalOperation.g:2019:1: rule__SimpleType__Group__1 : rule__SimpleType__Group__1__Impl rule__SimpleType__Group__2 ;
    public final void rule__SimpleType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2023:1: ( rule__SimpleType__Group__1__Impl rule__SimpleType__Group__2 )
            // InternalOperation.g:2024:2: rule__SimpleType__Group__1__Impl rule__SimpleType__Group__2
            {
            pushFollow(FOLLOW_5);
            rule__SimpleType__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__SimpleType__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group__1"


    // $ANTLR start "rule__SimpleType__Group__1__Impl"
    // InternalOperation.g:2031:1: rule__SimpleType__Group__1__Impl : ( ( rule__SimpleType__TypeAssignment_1 ) ) ;
    public final void rule__SimpleType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2035:1: ( ( ( rule__SimpleType__TypeAssignment_1 ) ) )
            // InternalOperation.g:2036:1: ( ( rule__SimpleType__TypeAssignment_1 ) )
            {
            // InternalOperation.g:2036:1: ( ( rule__SimpleType__TypeAssignment_1 ) )
            // InternalOperation.g:2037:2: ( rule__SimpleType__TypeAssignment_1 )
            {
             before(grammarAccess.getSimpleTypeAccess().getTypeAssignment_1()); 
            // InternalOperation.g:2038:2: ( rule__SimpleType__TypeAssignment_1 )
            // InternalOperation.g:2038:3: rule__SimpleType__TypeAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__SimpleType__TypeAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getSimpleTypeAccess().getTypeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group__1__Impl"


    // $ANTLR start "rule__SimpleType__Group__2"
    // InternalOperation.g:2046:1: rule__SimpleType__Group__2 : rule__SimpleType__Group__2__Impl rule__SimpleType__Group__3 ;
    public final void rule__SimpleType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2050:1: ( rule__SimpleType__Group__2__Impl rule__SimpleType__Group__3 )
            // InternalOperation.g:2051:2: rule__SimpleType__Group__2__Impl rule__SimpleType__Group__3
            {
            pushFollow(FOLLOW_20);
            rule__SimpleType__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__SimpleType__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group__2"


    // $ANTLR start "rule__SimpleType__Group__2__Impl"
    // InternalOperation.g:2058:1: rule__SimpleType__Group__2__Impl : ( ( rule__SimpleType__NameAssignment_2 ) ) ;
    public final void rule__SimpleType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2062:1: ( ( ( rule__SimpleType__NameAssignment_2 ) ) )
            // InternalOperation.g:2063:1: ( ( rule__SimpleType__NameAssignment_2 ) )
            {
            // InternalOperation.g:2063:1: ( ( rule__SimpleType__NameAssignment_2 ) )
            // InternalOperation.g:2064:2: ( rule__SimpleType__NameAssignment_2 )
            {
             before(grammarAccess.getSimpleTypeAccess().getNameAssignment_2()); 
            // InternalOperation.g:2065:2: ( rule__SimpleType__NameAssignment_2 )
            // InternalOperation.g:2065:3: rule__SimpleType__NameAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__SimpleType__NameAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getSimpleTypeAccess().getNameAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group__2__Impl"


    // $ANTLR start "rule__SimpleType__Group__3"
    // InternalOperation.g:2073:1: rule__SimpleType__Group__3 : rule__SimpleType__Group__3__Impl ;
    public final void rule__SimpleType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2077:1: ( rule__SimpleType__Group__3__Impl )
            // InternalOperation.g:2078:2: rule__SimpleType__Group__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__SimpleType__Group__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group__3"


    // $ANTLR start "rule__SimpleType__Group__3__Impl"
    // InternalOperation.g:2084:1: rule__SimpleType__Group__3__Impl : ( ( rule__SimpleType__Group_3__0 )? ) ;
    public final void rule__SimpleType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2088:1: ( ( ( rule__SimpleType__Group_3__0 )? ) )
            // InternalOperation.g:2089:1: ( ( rule__SimpleType__Group_3__0 )? )
            {
            // InternalOperation.g:2089:1: ( ( rule__SimpleType__Group_3__0 )? )
            // InternalOperation.g:2090:2: ( rule__SimpleType__Group_3__0 )?
            {
             before(grammarAccess.getSimpleTypeAccess().getGroup_3()); 
            // InternalOperation.g:2091:2: ( rule__SimpleType__Group_3__0 )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==33) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // InternalOperation.g:2091:3: rule__SimpleType__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__SimpleType__Group_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getSimpleTypeAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group__3__Impl"


    // $ANTLR start "rule__SimpleType__Group_3__0"
    // InternalOperation.g:2100:1: rule__SimpleType__Group_3__0 : rule__SimpleType__Group_3__0__Impl rule__SimpleType__Group_3__1 ;
    public final void rule__SimpleType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2104:1: ( rule__SimpleType__Group_3__0__Impl rule__SimpleType__Group_3__1 )
            // InternalOperation.g:2105:2: rule__SimpleType__Group_3__0__Impl rule__SimpleType__Group_3__1
            {
            pushFollow(FOLLOW_21);
            rule__SimpleType__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__SimpleType__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group_3__0"


    // $ANTLR start "rule__SimpleType__Group_3__0__Impl"
    // InternalOperation.g:2112:1: rule__SimpleType__Group_3__0__Impl : ( '=' ) ;
    public final void rule__SimpleType__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2116:1: ( ( '=' ) )
            // InternalOperation.g:2117:1: ( '=' )
            {
            // InternalOperation.g:2117:1: ( '=' )
            // InternalOperation.g:2118:2: '='
            {
             before(grammarAccess.getSimpleTypeAccess().getEqualsSignKeyword_3_0()); 
            match(input,33,FOLLOW_2); 
             after(grammarAccess.getSimpleTypeAccess().getEqualsSignKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group_3__0__Impl"


    // $ANTLR start "rule__SimpleType__Group_3__1"
    // InternalOperation.g:2127:1: rule__SimpleType__Group_3__1 : rule__SimpleType__Group_3__1__Impl ;
    public final void rule__SimpleType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2131:1: ( rule__SimpleType__Group_3__1__Impl )
            // InternalOperation.g:2132:2: rule__SimpleType__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__SimpleType__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group_3__1"


    // $ANTLR start "rule__SimpleType__Group_3__1__Impl"
    // InternalOperation.g:2138:1: rule__SimpleType__Group_3__1__Impl : ( ( rule__SimpleType__ValueAssignment_3_1 ) ) ;
    public final void rule__SimpleType__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2142:1: ( ( ( rule__SimpleType__ValueAssignment_3_1 ) ) )
            // InternalOperation.g:2143:1: ( ( rule__SimpleType__ValueAssignment_3_1 ) )
            {
            // InternalOperation.g:2143:1: ( ( rule__SimpleType__ValueAssignment_3_1 ) )
            // InternalOperation.g:2144:2: ( rule__SimpleType__ValueAssignment_3_1 )
            {
             before(grammarAccess.getSimpleTypeAccess().getValueAssignment_3_1()); 
            // InternalOperation.g:2145:2: ( rule__SimpleType__ValueAssignment_3_1 )
            // InternalOperation.g:2145:3: rule__SimpleType__ValueAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__SimpleType__ValueAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getSimpleTypeAccess().getValueAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__Group_3__1__Impl"


    // $ANTLR start "rule__AbstractType__Group__0"
    // InternalOperation.g:2154:1: rule__AbstractType__Group__0 : rule__AbstractType__Group__0__Impl rule__AbstractType__Group__1 ;
    public final void rule__AbstractType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2158:1: ( rule__AbstractType__Group__0__Impl rule__AbstractType__Group__1 )
            // InternalOperation.g:2159:2: rule__AbstractType__Group__0__Impl rule__AbstractType__Group__1
            {
            pushFollow(FOLLOW_16);
            rule__AbstractType__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AbstractType__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group__0"


    // $ANTLR start "rule__AbstractType__Group__0__Impl"
    // InternalOperation.g:2166:1: rule__AbstractType__Group__0__Impl : ( () ) ;
    public final void rule__AbstractType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2170:1: ( ( () ) )
            // InternalOperation.g:2171:1: ( () )
            {
            // InternalOperation.g:2171:1: ( () )
            // InternalOperation.g:2172:2: ()
            {
             before(grammarAccess.getAbstractTypeAccess().getAbstractTypeAction_0()); 
            // InternalOperation.g:2173:2: ()
            // InternalOperation.g:2173:3: 
            {
            }

             after(grammarAccess.getAbstractTypeAccess().getAbstractTypeAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group__0__Impl"


    // $ANTLR start "rule__AbstractType__Group__1"
    // InternalOperation.g:2181:1: rule__AbstractType__Group__1 : rule__AbstractType__Group__1__Impl rule__AbstractType__Group__2 ;
    public final void rule__AbstractType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2185:1: ( rule__AbstractType__Group__1__Impl rule__AbstractType__Group__2 )
            // InternalOperation.g:2186:2: rule__AbstractType__Group__1__Impl rule__AbstractType__Group__2
            {
            pushFollow(FOLLOW_5);
            rule__AbstractType__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AbstractType__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group__1"


    // $ANTLR start "rule__AbstractType__Group__1__Impl"
    // InternalOperation.g:2193:1: rule__AbstractType__Group__1__Impl : ( ( rule__AbstractType__TypeAssignment_1 ) ) ;
    public final void rule__AbstractType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2197:1: ( ( ( rule__AbstractType__TypeAssignment_1 ) ) )
            // InternalOperation.g:2198:1: ( ( rule__AbstractType__TypeAssignment_1 ) )
            {
            // InternalOperation.g:2198:1: ( ( rule__AbstractType__TypeAssignment_1 ) )
            // InternalOperation.g:2199:2: ( rule__AbstractType__TypeAssignment_1 )
            {
             before(grammarAccess.getAbstractTypeAccess().getTypeAssignment_1()); 
            // InternalOperation.g:2200:2: ( rule__AbstractType__TypeAssignment_1 )
            // InternalOperation.g:2200:3: rule__AbstractType__TypeAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__AbstractType__TypeAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getAbstractTypeAccess().getTypeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group__1__Impl"


    // $ANTLR start "rule__AbstractType__Group__2"
    // InternalOperation.g:2208:1: rule__AbstractType__Group__2 : rule__AbstractType__Group__2__Impl rule__AbstractType__Group__3 ;
    public final void rule__AbstractType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2212:1: ( rule__AbstractType__Group__2__Impl rule__AbstractType__Group__3 )
            // InternalOperation.g:2213:2: rule__AbstractType__Group__2__Impl rule__AbstractType__Group__3
            {
            pushFollow(FOLLOW_20);
            rule__AbstractType__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AbstractType__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group__2"


    // $ANTLR start "rule__AbstractType__Group__2__Impl"
    // InternalOperation.g:2220:1: rule__AbstractType__Group__2__Impl : ( ( rule__AbstractType__NameAssignment_2 ) ) ;
    public final void rule__AbstractType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2224:1: ( ( ( rule__AbstractType__NameAssignment_2 ) ) )
            // InternalOperation.g:2225:1: ( ( rule__AbstractType__NameAssignment_2 ) )
            {
            // InternalOperation.g:2225:1: ( ( rule__AbstractType__NameAssignment_2 ) )
            // InternalOperation.g:2226:2: ( rule__AbstractType__NameAssignment_2 )
            {
             before(grammarAccess.getAbstractTypeAccess().getNameAssignment_2()); 
            // InternalOperation.g:2227:2: ( rule__AbstractType__NameAssignment_2 )
            // InternalOperation.g:2227:3: rule__AbstractType__NameAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__AbstractType__NameAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getAbstractTypeAccess().getNameAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group__2__Impl"


    // $ANTLR start "rule__AbstractType__Group__3"
    // InternalOperation.g:2235:1: rule__AbstractType__Group__3 : rule__AbstractType__Group__3__Impl ;
    public final void rule__AbstractType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2239:1: ( rule__AbstractType__Group__3__Impl )
            // InternalOperation.g:2240:2: rule__AbstractType__Group__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AbstractType__Group__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group__3"


    // $ANTLR start "rule__AbstractType__Group__3__Impl"
    // InternalOperation.g:2246:1: rule__AbstractType__Group__3__Impl : ( ( rule__AbstractType__Group_3__0 )? ) ;
    public final void rule__AbstractType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2250:1: ( ( ( rule__AbstractType__Group_3__0 )? ) )
            // InternalOperation.g:2251:1: ( ( rule__AbstractType__Group_3__0 )? )
            {
            // InternalOperation.g:2251:1: ( ( rule__AbstractType__Group_3__0 )? )
            // InternalOperation.g:2252:2: ( rule__AbstractType__Group_3__0 )?
            {
             before(grammarAccess.getAbstractTypeAccess().getGroup_3()); 
            // InternalOperation.g:2253:2: ( rule__AbstractType__Group_3__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==33) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // InternalOperation.g:2253:3: rule__AbstractType__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__AbstractType__Group_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAbstractTypeAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group__3__Impl"


    // $ANTLR start "rule__AbstractType__Group_3__0"
    // InternalOperation.g:2262:1: rule__AbstractType__Group_3__0 : rule__AbstractType__Group_3__0__Impl rule__AbstractType__Group_3__1 ;
    public final void rule__AbstractType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2266:1: ( rule__AbstractType__Group_3__0__Impl rule__AbstractType__Group_3__1 )
            // InternalOperation.g:2267:2: rule__AbstractType__Group_3__0__Impl rule__AbstractType__Group_3__1
            {
            pushFollow(FOLLOW_21);
            rule__AbstractType__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AbstractType__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group_3__0"


    // $ANTLR start "rule__AbstractType__Group_3__0__Impl"
    // InternalOperation.g:2274:1: rule__AbstractType__Group_3__0__Impl : ( '=' ) ;
    public final void rule__AbstractType__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2278:1: ( ( '=' ) )
            // InternalOperation.g:2279:1: ( '=' )
            {
            // InternalOperation.g:2279:1: ( '=' )
            // InternalOperation.g:2280:2: '='
            {
             before(grammarAccess.getAbstractTypeAccess().getEqualsSignKeyword_3_0()); 
            match(input,33,FOLLOW_2); 
             after(grammarAccess.getAbstractTypeAccess().getEqualsSignKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group_3__0__Impl"


    // $ANTLR start "rule__AbstractType__Group_3__1"
    // InternalOperation.g:2289:1: rule__AbstractType__Group_3__1 : rule__AbstractType__Group_3__1__Impl ;
    public final void rule__AbstractType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2293:1: ( rule__AbstractType__Group_3__1__Impl )
            // InternalOperation.g:2294:2: rule__AbstractType__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AbstractType__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group_3__1"


    // $ANTLR start "rule__AbstractType__Group_3__1__Impl"
    // InternalOperation.g:2300:1: rule__AbstractType__Group_3__1__Impl : ( ( rule__AbstractType__ValueAssignment_3_1 ) ) ;
    public final void rule__AbstractType__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2304:1: ( ( ( rule__AbstractType__ValueAssignment_3_1 ) ) )
            // InternalOperation.g:2305:1: ( ( rule__AbstractType__ValueAssignment_3_1 ) )
            {
            // InternalOperation.g:2305:1: ( ( rule__AbstractType__ValueAssignment_3_1 ) )
            // InternalOperation.g:2306:2: ( rule__AbstractType__ValueAssignment_3_1 )
            {
             before(grammarAccess.getAbstractTypeAccess().getValueAssignment_3_1()); 
            // InternalOperation.g:2307:2: ( rule__AbstractType__ValueAssignment_3_1 )
            // InternalOperation.g:2307:3: rule__AbstractType__ValueAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__AbstractType__ValueAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getAbstractTypeAccess().getValueAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__Group_3__1__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_0__0"
    // InternalOperation.g:2316:1: rule__PrimitiveValue__Group_0__0 : rule__PrimitiveValue__Group_0__0__Impl rule__PrimitiveValue__Group_0__1 ;
    public final void rule__PrimitiveValue__Group_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2320:1: ( rule__PrimitiveValue__Group_0__0__Impl rule__PrimitiveValue__Group_0__1 )
            // InternalOperation.g:2321:2: rule__PrimitiveValue__Group_0__0__Impl rule__PrimitiveValue__Group_0__1
            {
            pushFollow(FOLLOW_22);
            rule__PrimitiveValue__Group_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_0__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_0__0"


    // $ANTLR start "rule__PrimitiveValue__Group_0__0__Impl"
    // InternalOperation.g:2328:1: rule__PrimitiveValue__Group_0__0__Impl : ( () ) ;
    public final void rule__PrimitiveValue__Group_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2332:1: ( ( () ) )
            // InternalOperation.g:2333:1: ( () )
            {
            // InternalOperation.g:2333:1: ( () )
            // InternalOperation.g:2334:2: ()
            {
             before(grammarAccess.getPrimitiveValueAccess().getIntValueAction_0_0()); 
            // InternalOperation.g:2335:2: ()
            // InternalOperation.g:2335:3: 
            {
            }

             after(grammarAccess.getPrimitiveValueAccess().getIntValueAction_0_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_0__0__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_0__1"
    // InternalOperation.g:2343:1: rule__PrimitiveValue__Group_0__1 : rule__PrimitiveValue__Group_0__1__Impl ;
    public final void rule__PrimitiveValue__Group_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2347:1: ( rule__PrimitiveValue__Group_0__1__Impl )
            // InternalOperation.g:2348:2: rule__PrimitiveValue__Group_0__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_0__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_0__1"


    // $ANTLR start "rule__PrimitiveValue__Group_0__1__Impl"
    // InternalOperation.g:2354:1: rule__PrimitiveValue__Group_0__1__Impl : ( ( rule__PrimitiveValue__IntValueAssignment_0_1 ) ) ;
    public final void rule__PrimitiveValue__Group_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2358:1: ( ( ( rule__PrimitiveValue__IntValueAssignment_0_1 ) ) )
            // InternalOperation.g:2359:1: ( ( rule__PrimitiveValue__IntValueAssignment_0_1 ) )
            {
            // InternalOperation.g:2359:1: ( ( rule__PrimitiveValue__IntValueAssignment_0_1 ) )
            // InternalOperation.g:2360:2: ( rule__PrimitiveValue__IntValueAssignment_0_1 )
            {
             before(grammarAccess.getPrimitiveValueAccess().getIntValueAssignment_0_1()); 
            // InternalOperation.g:2361:2: ( rule__PrimitiveValue__IntValueAssignment_0_1 )
            // InternalOperation.g:2361:3: rule__PrimitiveValue__IntValueAssignment_0_1
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__IntValueAssignment_0_1();

            state._fsp--;


            }

             after(grammarAccess.getPrimitiveValueAccess().getIntValueAssignment_0_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_0__1__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_1__0"
    // InternalOperation.g:2370:1: rule__PrimitiveValue__Group_1__0 : rule__PrimitiveValue__Group_1__0__Impl rule__PrimitiveValue__Group_1__1 ;
    public final void rule__PrimitiveValue__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2374:1: ( rule__PrimitiveValue__Group_1__0__Impl rule__PrimitiveValue__Group_1__1 )
            // InternalOperation.g:2375:2: rule__PrimitiveValue__Group_1__0__Impl rule__PrimitiveValue__Group_1__1
            {
            pushFollow(FOLLOW_23);
            rule__PrimitiveValue__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_1__0"


    // $ANTLR start "rule__PrimitiveValue__Group_1__0__Impl"
    // InternalOperation.g:2382:1: rule__PrimitiveValue__Group_1__0__Impl : ( () ) ;
    public final void rule__PrimitiveValue__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2386:1: ( ( () ) )
            // InternalOperation.g:2387:1: ( () )
            {
            // InternalOperation.g:2387:1: ( () )
            // InternalOperation.g:2388:2: ()
            {
             before(grammarAccess.getPrimitiveValueAccess().getFloatValueAction_1_0()); 
            // InternalOperation.g:2389:2: ()
            // InternalOperation.g:2389:3: 
            {
            }

             after(grammarAccess.getPrimitiveValueAccess().getFloatValueAction_1_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_1__0__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_1__1"
    // InternalOperation.g:2397:1: rule__PrimitiveValue__Group_1__1 : rule__PrimitiveValue__Group_1__1__Impl ;
    public final void rule__PrimitiveValue__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2401:1: ( rule__PrimitiveValue__Group_1__1__Impl )
            // InternalOperation.g:2402:2: rule__PrimitiveValue__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_1__1"


    // $ANTLR start "rule__PrimitiveValue__Group_1__1__Impl"
    // InternalOperation.g:2408:1: rule__PrimitiveValue__Group_1__1__Impl : ( ( rule__PrimitiveValue__FloatValueAssignment_1_1 ) ) ;
    public final void rule__PrimitiveValue__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2412:1: ( ( ( rule__PrimitiveValue__FloatValueAssignment_1_1 ) ) )
            // InternalOperation.g:2413:1: ( ( rule__PrimitiveValue__FloatValueAssignment_1_1 ) )
            {
            // InternalOperation.g:2413:1: ( ( rule__PrimitiveValue__FloatValueAssignment_1_1 ) )
            // InternalOperation.g:2414:2: ( rule__PrimitiveValue__FloatValueAssignment_1_1 )
            {
             before(grammarAccess.getPrimitiveValueAccess().getFloatValueAssignment_1_1()); 
            // InternalOperation.g:2415:2: ( rule__PrimitiveValue__FloatValueAssignment_1_1 )
            // InternalOperation.g:2415:3: rule__PrimitiveValue__FloatValueAssignment_1_1
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__FloatValueAssignment_1_1();

            state._fsp--;


            }

             after(grammarAccess.getPrimitiveValueAccess().getFloatValueAssignment_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_1__1__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_2__0"
    // InternalOperation.g:2424:1: rule__PrimitiveValue__Group_2__0 : rule__PrimitiveValue__Group_2__0__Impl rule__PrimitiveValue__Group_2__1 ;
    public final void rule__PrimitiveValue__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2428:1: ( rule__PrimitiveValue__Group_2__0__Impl rule__PrimitiveValue__Group_2__1 )
            // InternalOperation.g:2429:2: rule__PrimitiveValue__Group_2__0__Impl rule__PrimitiveValue__Group_2__1
            {
            pushFollow(FOLLOW_24);
            rule__PrimitiveValue__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_2__0"


    // $ANTLR start "rule__PrimitiveValue__Group_2__0__Impl"
    // InternalOperation.g:2436:1: rule__PrimitiveValue__Group_2__0__Impl : ( () ) ;
    public final void rule__PrimitiveValue__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2440:1: ( ( () ) )
            // InternalOperation.g:2441:1: ( () )
            {
            // InternalOperation.g:2441:1: ( () )
            // InternalOperation.g:2442:2: ()
            {
             before(grammarAccess.getPrimitiveValueAccess().getStringValueAction_2_0()); 
            // InternalOperation.g:2443:2: ()
            // InternalOperation.g:2443:3: 
            {
            }

             after(grammarAccess.getPrimitiveValueAccess().getStringValueAction_2_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_2__0__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_2__1"
    // InternalOperation.g:2451:1: rule__PrimitiveValue__Group_2__1 : rule__PrimitiveValue__Group_2__1__Impl ;
    public final void rule__PrimitiveValue__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2455:1: ( rule__PrimitiveValue__Group_2__1__Impl )
            // InternalOperation.g:2456:2: rule__PrimitiveValue__Group_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_2__1"


    // $ANTLR start "rule__PrimitiveValue__Group_2__1__Impl"
    // InternalOperation.g:2462:1: rule__PrimitiveValue__Group_2__1__Impl : ( ( rule__PrimitiveValue__StringValueAssignment_2_1 ) ) ;
    public final void rule__PrimitiveValue__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2466:1: ( ( ( rule__PrimitiveValue__StringValueAssignment_2_1 ) ) )
            // InternalOperation.g:2467:1: ( ( rule__PrimitiveValue__StringValueAssignment_2_1 ) )
            {
            // InternalOperation.g:2467:1: ( ( rule__PrimitiveValue__StringValueAssignment_2_1 ) )
            // InternalOperation.g:2468:2: ( rule__PrimitiveValue__StringValueAssignment_2_1 )
            {
             before(grammarAccess.getPrimitiveValueAccess().getStringValueAssignment_2_1()); 
            // InternalOperation.g:2469:2: ( rule__PrimitiveValue__StringValueAssignment_2_1 )
            // InternalOperation.g:2469:3: rule__PrimitiveValue__StringValueAssignment_2_1
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__StringValueAssignment_2_1();

            state._fsp--;


            }

             after(grammarAccess.getPrimitiveValueAccess().getStringValueAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_2__1__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_3__0"
    // InternalOperation.g:2478:1: rule__PrimitiveValue__Group_3__0 : rule__PrimitiveValue__Group_3__0__Impl rule__PrimitiveValue__Group_3__1 ;
    public final void rule__PrimitiveValue__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2482:1: ( rule__PrimitiveValue__Group_3__0__Impl rule__PrimitiveValue__Group_3__1 )
            // InternalOperation.g:2483:2: rule__PrimitiveValue__Group_3__0__Impl rule__PrimitiveValue__Group_3__1
            {
            pushFollow(FOLLOW_25);
            rule__PrimitiveValue__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_3__0"


    // $ANTLR start "rule__PrimitiveValue__Group_3__0__Impl"
    // InternalOperation.g:2490:1: rule__PrimitiveValue__Group_3__0__Impl : ( () ) ;
    public final void rule__PrimitiveValue__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2494:1: ( ( () ) )
            // InternalOperation.g:2495:1: ( () )
            {
            // InternalOperation.g:2495:1: ( () )
            // InternalOperation.g:2496:2: ()
            {
             before(grammarAccess.getPrimitiveValueAccess().getBoolValueAction_3_0()); 
            // InternalOperation.g:2497:2: ()
            // InternalOperation.g:2497:3: 
            {
            }

             after(grammarAccess.getPrimitiveValueAccess().getBoolValueAction_3_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_3__0__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_3__1"
    // InternalOperation.g:2505:1: rule__PrimitiveValue__Group_3__1 : rule__PrimitiveValue__Group_3__1__Impl ;
    public final void rule__PrimitiveValue__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2509:1: ( rule__PrimitiveValue__Group_3__1__Impl )
            // InternalOperation.g:2510:2: rule__PrimitiveValue__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_3__1"


    // $ANTLR start "rule__PrimitiveValue__Group_3__1__Impl"
    // InternalOperation.g:2516:1: rule__PrimitiveValue__Group_3__1__Impl : ( ( rule__PrimitiveValue__BoolValueAssignment_3_1 ) ) ;
    public final void rule__PrimitiveValue__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2520:1: ( ( ( rule__PrimitiveValue__BoolValueAssignment_3_1 ) ) )
            // InternalOperation.g:2521:1: ( ( rule__PrimitiveValue__BoolValueAssignment_3_1 ) )
            {
            // InternalOperation.g:2521:1: ( ( rule__PrimitiveValue__BoolValueAssignment_3_1 ) )
            // InternalOperation.g:2522:2: ( rule__PrimitiveValue__BoolValueAssignment_3_1 )
            {
             before(grammarAccess.getPrimitiveValueAccess().getBoolValueAssignment_3_1()); 
            // InternalOperation.g:2523:2: ( rule__PrimitiveValue__BoolValueAssignment_3_1 )
            // InternalOperation.g:2523:3: rule__PrimitiveValue__BoolValueAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__BoolValueAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getPrimitiveValueAccess().getBoolValueAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_3__1__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_4__0"
    // InternalOperation.g:2532:1: rule__PrimitiveValue__Group_4__0 : rule__PrimitiveValue__Group_4__0__Impl rule__PrimitiveValue__Group_4__1 ;
    public final void rule__PrimitiveValue__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2536:1: ( rule__PrimitiveValue__Group_4__0__Impl rule__PrimitiveValue__Group_4__1 )
            // InternalOperation.g:2537:2: rule__PrimitiveValue__Group_4__0__Impl rule__PrimitiveValue__Group_4__1
            {
            pushFollow(FOLLOW_26);
            rule__PrimitiveValue__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_4__0"


    // $ANTLR start "rule__PrimitiveValue__Group_4__0__Impl"
    // InternalOperation.g:2544:1: rule__PrimitiveValue__Group_4__0__Impl : ( () ) ;
    public final void rule__PrimitiveValue__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2548:1: ( ( () ) )
            // InternalOperation.g:2549:1: ( () )
            {
            // InternalOperation.g:2549:1: ( () )
            // InternalOperation.g:2550:2: ()
            {
             before(grammarAccess.getPrimitiveValueAccess().getDateValueAction_4_0()); 
            // InternalOperation.g:2551:2: ()
            // InternalOperation.g:2551:3: 
            {
            }

             after(grammarAccess.getPrimitiveValueAccess().getDateValueAction_4_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_4__0__Impl"


    // $ANTLR start "rule__PrimitiveValue__Group_4__1"
    // InternalOperation.g:2559:1: rule__PrimitiveValue__Group_4__1 : rule__PrimitiveValue__Group_4__1__Impl ;
    public final void rule__PrimitiveValue__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2563:1: ( rule__PrimitiveValue__Group_4__1__Impl )
            // InternalOperation.g:2564:2: rule__PrimitiveValue__Group_4__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__Group_4__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_4__1"


    // $ANTLR start "rule__PrimitiveValue__Group_4__1__Impl"
    // InternalOperation.g:2570:1: rule__PrimitiveValue__Group_4__1__Impl : ( ( rule__PrimitiveValue__DateValueAssignment_4_1 ) ) ;
    public final void rule__PrimitiveValue__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2574:1: ( ( ( rule__PrimitiveValue__DateValueAssignment_4_1 ) ) )
            // InternalOperation.g:2575:1: ( ( rule__PrimitiveValue__DateValueAssignment_4_1 ) )
            {
            // InternalOperation.g:2575:1: ( ( rule__PrimitiveValue__DateValueAssignment_4_1 ) )
            // InternalOperation.g:2576:2: ( rule__PrimitiveValue__DateValueAssignment_4_1 )
            {
             before(grammarAccess.getPrimitiveValueAccess().getDateValueAssignment_4_1()); 
            // InternalOperation.g:2577:2: ( rule__PrimitiveValue__DateValueAssignment_4_1 )
            // InternalOperation.g:2577:3: rule__PrimitiveValue__DateValueAssignment_4_1
            {
            pushFollow(FOLLOW_2);
            rule__PrimitiveValue__DateValueAssignment_4_1();

            state._fsp--;


            }

             after(grammarAccess.getPrimitiveValueAccess().getDateValueAssignment_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__Group_4__1__Impl"


    // $ANTLR start "rule__AbstractObjectValue__Group__0"
    // InternalOperation.g:2586:1: rule__AbstractObjectValue__Group__0 : rule__AbstractObjectValue__Group__0__Impl rule__AbstractObjectValue__Group__1 ;
    public final void rule__AbstractObjectValue__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2590:1: ( rule__AbstractObjectValue__Group__0__Impl rule__AbstractObjectValue__Group__1 )
            // InternalOperation.g:2591:2: rule__AbstractObjectValue__Group__0__Impl rule__AbstractObjectValue__Group__1
            {
            pushFollow(FOLLOW_21);
            rule__AbstractObjectValue__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AbstractObjectValue__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractObjectValue__Group__0"


    // $ANTLR start "rule__AbstractObjectValue__Group__0__Impl"
    // InternalOperation.g:2598:1: rule__AbstractObjectValue__Group__0__Impl : ( () ) ;
    public final void rule__AbstractObjectValue__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2602:1: ( ( () ) )
            // InternalOperation.g:2603:1: ( () )
            {
            // InternalOperation.g:2603:1: ( () )
            // InternalOperation.g:2604:2: ()
            {
             before(grammarAccess.getAbstractObjectValueAccess().getAbstractObjectValueAction_0()); 
            // InternalOperation.g:2605:2: ()
            // InternalOperation.g:2605:3: 
            {
            }

             after(grammarAccess.getAbstractObjectValueAccess().getAbstractObjectValueAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractObjectValue__Group__0__Impl"


    // $ANTLR start "rule__AbstractObjectValue__Group__1"
    // InternalOperation.g:2613:1: rule__AbstractObjectValue__Group__1 : rule__AbstractObjectValue__Group__1__Impl ;
    public final void rule__AbstractObjectValue__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2617:1: ( rule__AbstractObjectValue__Group__1__Impl )
            // InternalOperation.g:2618:2: rule__AbstractObjectValue__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AbstractObjectValue__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractObjectValue__Group__1"


    // $ANTLR start "rule__AbstractObjectValue__Group__1__Impl"
    // InternalOperation.g:2624:1: rule__AbstractObjectValue__Group__1__Impl : ( ( rule__AbstractObjectValue__AbstractValueAssignment_1 ) ) ;
    public final void rule__AbstractObjectValue__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2628:1: ( ( ( rule__AbstractObjectValue__AbstractValueAssignment_1 ) ) )
            // InternalOperation.g:2629:1: ( ( rule__AbstractObjectValue__AbstractValueAssignment_1 ) )
            {
            // InternalOperation.g:2629:1: ( ( rule__AbstractObjectValue__AbstractValueAssignment_1 ) )
            // InternalOperation.g:2630:2: ( rule__AbstractObjectValue__AbstractValueAssignment_1 )
            {
             before(grammarAccess.getAbstractObjectValueAccess().getAbstractValueAssignment_1()); 
            // InternalOperation.g:2631:2: ( rule__AbstractObjectValue__AbstractValueAssignment_1 )
            // InternalOperation.g:2631:3: rule__AbstractObjectValue__AbstractValueAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__AbstractObjectValue__AbstractValueAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getAbstractObjectValueAccess().getAbstractValueAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractObjectValue__Group__1__Impl"


    // $ANTLR start "rule__ArrayValues__Group__0"
    // InternalOperation.g:2640:1: rule__ArrayValues__Group__0 : rule__ArrayValues__Group__0__Impl rule__ArrayValues__Group__1 ;
    public final void rule__ArrayValues__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2644:1: ( rule__ArrayValues__Group__0__Impl rule__ArrayValues__Group__1 )
            // InternalOperation.g:2645:2: rule__ArrayValues__Group__0__Impl rule__ArrayValues__Group__1
            {
            pushFollow(FOLLOW_27);
            rule__ArrayValues__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group__0"


    // $ANTLR start "rule__ArrayValues__Group__0__Impl"
    // InternalOperation.g:2652:1: rule__ArrayValues__Group__0__Impl : ( () ) ;
    public final void rule__ArrayValues__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2656:1: ( ( () ) )
            // InternalOperation.g:2657:1: ( () )
            {
            // InternalOperation.g:2657:1: ( () )
            // InternalOperation.g:2658:2: ()
            {
             before(grammarAccess.getArrayValuesAccess().getArrayValuesAction_0()); 
            // InternalOperation.g:2659:2: ()
            // InternalOperation.g:2659:3: 
            {
            }

             after(grammarAccess.getArrayValuesAccess().getArrayValuesAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group__0__Impl"


    // $ANTLR start "rule__ArrayValues__Group__1"
    // InternalOperation.g:2667:1: rule__ArrayValues__Group__1 : rule__ArrayValues__Group__1__Impl rule__ArrayValues__Group__2 ;
    public final void rule__ArrayValues__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2671:1: ( rule__ArrayValues__Group__1__Impl rule__ArrayValues__Group__2 )
            // InternalOperation.g:2672:2: rule__ArrayValues__Group__1__Impl rule__ArrayValues__Group__2
            {
            pushFollow(FOLLOW_28);
            rule__ArrayValues__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group__1"


    // $ANTLR start "rule__ArrayValues__Group__1__Impl"
    // InternalOperation.g:2679:1: rule__ArrayValues__Group__1__Impl : ( '[' ) ;
    public final void rule__ArrayValues__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2683:1: ( ( '[' ) )
            // InternalOperation.g:2684:1: ( '[' )
            {
            // InternalOperation.g:2684:1: ( '[' )
            // InternalOperation.g:2685:2: '['
            {
             before(grammarAccess.getArrayValuesAccess().getLeftSquareBracketKeyword_1()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getArrayValuesAccess().getLeftSquareBracketKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group__1__Impl"


    // $ANTLR start "rule__ArrayValues__Group__2"
    // InternalOperation.g:2694:1: rule__ArrayValues__Group__2 : rule__ArrayValues__Group__2__Impl rule__ArrayValues__Group__3 ;
    public final void rule__ArrayValues__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2698:1: ( rule__ArrayValues__Group__2__Impl rule__ArrayValues__Group__3 )
            // InternalOperation.g:2699:2: rule__ArrayValues__Group__2__Impl rule__ArrayValues__Group__3
            {
            pushFollow(FOLLOW_28);
            rule__ArrayValues__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group__2"


    // $ANTLR start "rule__ArrayValues__Group__2__Impl"
    // InternalOperation.g:2706:1: rule__ArrayValues__Group__2__Impl : ( ( rule__ArrayValues__Group_2__0 )? ) ;
    public final void rule__ArrayValues__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2710:1: ( ( ( rule__ArrayValues__Group_2__0 )? ) )
            // InternalOperation.g:2711:1: ( ( rule__ArrayValues__Group_2__0 )? )
            {
            // InternalOperation.g:2711:1: ( ( rule__ArrayValues__Group_2__0 )? )
            // InternalOperation.g:2712:2: ( rule__ArrayValues__Group_2__0 )?
            {
             before(grammarAccess.getArrayValuesAccess().getGroup_2()); 
            // InternalOperation.g:2713:2: ( rule__ArrayValues__Group_2__0 )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( ((LA21_0>=RULE_INT && LA21_0<=RULE_ID)||(LA21_0>=11 && LA21_0<=12)||LA21_0==32||LA21_0==34||LA21_0==36) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // InternalOperation.g:2713:3: rule__ArrayValues__Group_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ArrayValues__Group_2__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArrayValuesAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group__2__Impl"


    // $ANTLR start "rule__ArrayValues__Group__3"
    // InternalOperation.g:2721:1: rule__ArrayValues__Group__3 : rule__ArrayValues__Group__3__Impl ;
    public final void rule__ArrayValues__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2725:1: ( rule__ArrayValues__Group__3__Impl )
            // InternalOperation.g:2726:2: rule__ArrayValues__Group__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group__3"


    // $ANTLR start "rule__ArrayValues__Group__3__Impl"
    // InternalOperation.g:2732:1: rule__ArrayValues__Group__3__Impl : ( ']' ) ;
    public final void rule__ArrayValues__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2736:1: ( ( ']' ) )
            // InternalOperation.g:2737:1: ( ']' )
            {
            // InternalOperation.g:2737:1: ( ']' )
            // InternalOperation.g:2738:2: ']'
            {
             before(grammarAccess.getArrayValuesAccess().getRightSquareBracketKeyword_3()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getArrayValuesAccess().getRightSquareBracketKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group__3__Impl"


    // $ANTLR start "rule__ArrayValues__Group_2__0"
    // InternalOperation.g:2748:1: rule__ArrayValues__Group_2__0 : rule__ArrayValues__Group_2__0__Impl rule__ArrayValues__Group_2__1 ;
    public final void rule__ArrayValues__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2752:1: ( rule__ArrayValues__Group_2__0__Impl rule__ArrayValues__Group_2__1 )
            // InternalOperation.g:2753:2: rule__ArrayValues__Group_2__0__Impl rule__ArrayValues__Group_2__1
            {
            pushFollow(FOLLOW_10);
            rule__ArrayValues__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group_2__0"


    // $ANTLR start "rule__ArrayValues__Group_2__0__Impl"
    // InternalOperation.g:2760:1: rule__ArrayValues__Group_2__0__Impl : ( ( rule__ArrayValues__ValuesAssignment_2_0 ) ) ;
    public final void rule__ArrayValues__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2764:1: ( ( ( rule__ArrayValues__ValuesAssignment_2_0 ) ) )
            // InternalOperation.g:2765:1: ( ( rule__ArrayValues__ValuesAssignment_2_0 ) )
            {
            // InternalOperation.g:2765:1: ( ( rule__ArrayValues__ValuesAssignment_2_0 ) )
            // InternalOperation.g:2766:2: ( rule__ArrayValues__ValuesAssignment_2_0 )
            {
             before(grammarAccess.getArrayValuesAccess().getValuesAssignment_2_0()); 
            // InternalOperation.g:2767:2: ( rule__ArrayValues__ValuesAssignment_2_0 )
            // InternalOperation.g:2767:3: rule__ArrayValues__ValuesAssignment_2_0
            {
            pushFollow(FOLLOW_2);
            rule__ArrayValues__ValuesAssignment_2_0();

            state._fsp--;


            }

             after(grammarAccess.getArrayValuesAccess().getValuesAssignment_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group_2__0__Impl"


    // $ANTLR start "rule__ArrayValues__Group_2__1"
    // InternalOperation.g:2775:1: rule__ArrayValues__Group_2__1 : rule__ArrayValues__Group_2__1__Impl ;
    public final void rule__ArrayValues__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2779:1: ( rule__ArrayValues__Group_2__1__Impl )
            // InternalOperation.g:2780:2: rule__ArrayValues__Group_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group_2__1"


    // $ANTLR start "rule__ArrayValues__Group_2__1__Impl"
    // InternalOperation.g:2786:1: rule__ArrayValues__Group_2__1__Impl : ( ( rule__ArrayValues__Group_2_1__0 )* ) ;
    public final void rule__ArrayValues__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2790:1: ( ( ( rule__ArrayValues__Group_2_1__0 )* ) )
            // InternalOperation.g:2791:1: ( ( rule__ArrayValues__Group_2_1__0 )* )
            {
            // InternalOperation.g:2791:1: ( ( rule__ArrayValues__Group_2_1__0 )* )
            // InternalOperation.g:2792:2: ( rule__ArrayValues__Group_2_1__0 )*
            {
             before(grammarAccess.getArrayValuesAccess().getGroup_2_1()); 
            // InternalOperation.g:2793:2: ( rule__ArrayValues__Group_2_1__0 )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==26) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // InternalOperation.g:2793:3: rule__ArrayValues__Group_2_1__0
            	    {
            	    pushFollow(FOLLOW_11);
            	    rule__ArrayValues__Group_2_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

             after(grammarAccess.getArrayValuesAccess().getGroup_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group_2__1__Impl"


    // $ANTLR start "rule__ArrayValues__Group_2_1__0"
    // InternalOperation.g:2802:1: rule__ArrayValues__Group_2_1__0 : rule__ArrayValues__Group_2_1__0__Impl rule__ArrayValues__Group_2_1__1 ;
    public final void rule__ArrayValues__Group_2_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2806:1: ( rule__ArrayValues__Group_2_1__0__Impl rule__ArrayValues__Group_2_1__1 )
            // InternalOperation.g:2807:2: rule__ArrayValues__Group_2_1__0__Impl rule__ArrayValues__Group_2_1__1
            {
            pushFollow(FOLLOW_21);
            rule__ArrayValues__Group_2_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group_2_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group_2_1__0"


    // $ANTLR start "rule__ArrayValues__Group_2_1__0__Impl"
    // InternalOperation.g:2814:1: rule__ArrayValues__Group_2_1__0__Impl : ( ',' ) ;
    public final void rule__ArrayValues__Group_2_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2818:1: ( ( ',' ) )
            // InternalOperation.g:2819:1: ( ',' )
            {
            // InternalOperation.g:2819:1: ( ',' )
            // InternalOperation.g:2820:2: ','
            {
             before(grammarAccess.getArrayValuesAccess().getCommaKeyword_2_1_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getArrayValuesAccess().getCommaKeyword_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group_2_1__0__Impl"


    // $ANTLR start "rule__ArrayValues__Group_2_1__1"
    // InternalOperation.g:2829:1: rule__ArrayValues__Group_2_1__1 : rule__ArrayValues__Group_2_1__1__Impl ;
    public final void rule__ArrayValues__Group_2_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2833:1: ( rule__ArrayValues__Group_2_1__1__Impl )
            // InternalOperation.g:2834:2: rule__ArrayValues__Group_2_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArrayValues__Group_2_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group_2_1__1"


    // $ANTLR start "rule__ArrayValues__Group_2_1__1__Impl"
    // InternalOperation.g:2840:1: rule__ArrayValues__Group_2_1__1__Impl : ( ( rule__ArrayValues__ValuesAssignment_2_1_1 ) ) ;
    public final void rule__ArrayValues__Group_2_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2844:1: ( ( ( rule__ArrayValues__ValuesAssignment_2_1_1 ) ) )
            // InternalOperation.g:2845:1: ( ( rule__ArrayValues__ValuesAssignment_2_1_1 ) )
            {
            // InternalOperation.g:2845:1: ( ( rule__ArrayValues__ValuesAssignment_2_1_1 ) )
            // InternalOperation.g:2846:2: ( rule__ArrayValues__ValuesAssignment_2_1_1 )
            {
             before(grammarAccess.getArrayValuesAccess().getValuesAssignment_2_1_1()); 
            // InternalOperation.g:2847:2: ( rule__ArrayValues__ValuesAssignment_2_1_1 )
            // InternalOperation.g:2847:3: rule__ArrayValues__ValuesAssignment_2_1_1
            {
            pushFollow(FOLLOW_2);
            rule__ArrayValues__ValuesAssignment_2_1_1();

            state._fsp--;


            }

             after(grammarAccess.getArrayValuesAccess().getValuesAssignment_2_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__Group_2_1__1__Impl"


    // $ANTLR start "rule__ArrayType__Group__0"
    // InternalOperation.g:2856:1: rule__ArrayType__Group__0 : rule__ArrayType__Group__0__Impl rule__ArrayType__Group__1 ;
    public final void rule__ArrayType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2860:1: ( rule__ArrayType__Group__0__Impl rule__ArrayType__Group__1 )
            // InternalOperation.g:2861:2: rule__ArrayType__Group__0__Impl rule__ArrayType__Group__1
            {
            pushFollow(FOLLOW_12);
            rule__ArrayType__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__0"


    // $ANTLR start "rule__ArrayType__Group__0__Impl"
    // InternalOperation.g:2868:1: rule__ArrayType__Group__0__Impl : ( () ) ;
    public final void rule__ArrayType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2872:1: ( ( () ) )
            // InternalOperation.g:2873:1: ( () )
            {
            // InternalOperation.g:2873:1: ( () )
            // InternalOperation.g:2874:2: ()
            {
             before(grammarAccess.getArrayTypeAccess().getArrayTypeAction_0()); 
            // InternalOperation.g:2875:2: ()
            // InternalOperation.g:2875:3: 
            {
            }

             after(grammarAccess.getArrayTypeAccess().getArrayTypeAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__0__Impl"


    // $ANTLR start "rule__ArrayType__Group__1"
    // InternalOperation.g:2883:1: rule__ArrayType__Group__1 : rule__ArrayType__Group__1__Impl rule__ArrayType__Group__2 ;
    public final void rule__ArrayType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2887:1: ( rule__ArrayType__Group__1__Impl rule__ArrayType__Group__2 )
            // InternalOperation.g:2888:2: rule__ArrayType__Group__1__Impl rule__ArrayType__Group__2
            {
            pushFollow(FOLLOW_27);
            rule__ArrayType__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__1"


    // $ANTLR start "rule__ArrayType__Group__1__Impl"
    // InternalOperation.g:2895:1: rule__ArrayType__Group__1__Impl : ( ( rule__ArrayType__Alternatives_1 ) ) ;
    public final void rule__ArrayType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2899:1: ( ( ( rule__ArrayType__Alternatives_1 ) ) )
            // InternalOperation.g:2900:1: ( ( rule__ArrayType__Alternatives_1 ) )
            {
            // InternalOperation.g:2900:1: ( ( rule__ArrayType__Alternatives_1 ) )
            // InternalOperation.g:2901:2: ( rule__ArrayType__Alternatives_1 )
            {
             before(grammarAccess.getArrayTypeAccess().getAlternatives_1()); 
            // InternalOperation.g:2902:2: ( rule__ArrayType__Alternatives_1 )
            // InternalOperation.g:2902:3: rule__ArrayType__Alternatives_1
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__Alternatives_1();

            state._fsp--;


            }

             after(grammarAccess.getArrayTypeAccess().getAlternatives_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__1__Impl"


    // $ANTLR start "rule__ArrayType__Group__2"
    // InternalOperation.g:2910:1: rule__ArrayType__Group__2 : rule__ArrayType__Group__2__Impl rule__ArrayType__Group__3 ;
    public final void rule__ArrayType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2914:1: ( rule__ArrayType__Group__2__Impl rule__ArrayType__Group__3 )
            // InternalOperation.g:2915:2: rule__ArrayType__Group__2__Impl rule__ArrayType__Group__3
            {
            pushFollow(FOLLOW_29);
            rule__ArrayType__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__2"


    // $ANTLR start "rule__ArrayType__Group__2__Impl"
    // InternalOperation.g:2922:1: rule__ArrayType__Group__2__Impl : ( '[' ) ;
    public final void rule__ArrayType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2926:1: ( ( '[' ) )
            // InternalOperation.g:2927:1: ( '[' )
            {
            // InternalOperation.g:2927:1: ( '[' )
            // InternalOperation.g:2928:2: '['
            {
             before(grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_2()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__2__Impl"


    // $ANTLR start "rule__ArrayType__Group__3"
    // InternalOperation.g:2937:1: rule__ArrayType__Group__3 : rule__ArrayType__Group__3__Impl rule__ArrayType__Group__4 ;
    public final void rule__ArrayType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2941:1: ( rule__ArrayType__Group__3__Impl rule__ArrayType__Group__4 )
            // InternalOperation.g:2942:2: rule__ArrayType__Group__3__Impl rule__ArrayType__Group__4
            {
            pushFollow(FOLLOW_5);
            rule__ArrayType__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__3"


    // $ANTLR start "rule__ArrayType__Group__3__Impl"
    // InternalOperation.g:2949:1: rule__ArrayType__Group__3__Impl : ( ']' ) ;
    public final void rule__ArrayType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2953:1: ( ( ']' ) )
            // InternalOperation.g:2954:1: ( ']' )
            {
            // InternalOperation.g:2954:1: ( ']' )
            // InternalOperation.g:2955:2: ']'
            {
             before(grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_3()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__3__Impl"


    // $ANTLR start "rule__ArrayType__Group__4"
    // InternalOperation.g:2964:1: rule__ArrayType__Group__4 : rule__ArrayType__Group__4__Impl rule__ArrayType__Group__5 ;
    public final void rule__ArrayType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2968:1: ( rule__ArrayType__Group__4__Impl rule__ArrayType__Group__5 )
            // InternalOperation.g:2969:2: rule__ArrayType__Group__4__Impl rule__ArrayType__Group__5
            {
            pushFollow(FOLLOW_20);
            rule__ArrayType__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__4"


    // $ANTLR start "rule__ArrayType__Group__4__Impl"
    // InternalOperation.g:2976:1: rule__ArrayType__Group__4__Impl : ( ( rule__ArrayType__NameAssignment_4 ) ) ;
    public final void rule__ArrayType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2980:1: ( ( ( rule__ArrayType__NameAssignment_4 ) ) )
            // InternalOperation.g:2981:1: ( ( rule__ArrayType__NameAssignment_4 ) )
            {
            // InternalOperation.g:2981:1: ( ( rule__ArrayType__NameAssignment_4 ) )
            // InternalOperation.g:2982:2: ( rule__ArrayType__NameAssignment_4 )
            {
             before(grammarAccess.getArrayTypeAccess().getNameAssignment_4()); 
            // InternalOperation.g:2983:2: ( rule__ArrayType__NameAssignment_4 )
            // InternalOperation.g:2983:3: rule__ArrayType__NameAssignment_4
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__NameAssignment_4();

            state._fsp--;


            }

             after(grammarAccess.getArrayTypeAccess().getNameAssignment_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__4__Impl"


    // $ANTLR start "rule__ArrayType__Group__5"
    // InternalOperation.g:2991:1: rule__ArrayType__Group__5 : rule__ArrayType__Group__5__Impl ;
    public final void rule__ArrayType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:2995:1: ( rule__ArrayType__Group__5__Impl )
            // InternalOperation.g:2996:2: rule__ArrayType__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__5"


    // $ANTLR start "rule__ArrayType__Group__5__Impl"
    // InternalOperation.g:3002:1: rule__ArrayType__Group__5__Impl : ( ( rule__ArrayType__Group_5__0 )? ) ;
    public final void rule__ArrayType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3006:1: ( ( ( rule__ArrayType__Group_5__0 )? ) )
            // InternalOperation.g:3007:1: ( ( rule__ArrayType__Group_5__0 )? )
            {
            // InternalOperation.g:3007:1: ( ( rule__ArrayType__Group_5__0 )? )
            // InternalOperation.g:3008:2: ( rule__ArrayType__Group_5__0 )?
            {
             before(grammarAccess.getArrayTypeAccess().getGroup_5()); 
            // InternalOperation.g:3009:2: ( rule__ArrayType__Group_5__0 )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==33) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // InternalOperation.g:3009:3: rule__ArrayType__Group_5__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ArrayType__Group_5__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArrayTypeAccess().getGroup_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group__5__Impl"


    // $ANTLR start "rule__ArrayType__Group_5__0"
    // InternalOperation.g:3018:1: rule__ArrayType__Group_5__0 : rule__ArrayType__Group_5__0__Impl rule__ArrayType__Group_5__1 ;
    public final void rule__ArrayType__Group_5__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3022:1: ( rule__ArrayType__Group_5__0__Impl rule__ArrayType__Group_5__1 )
            // InternalOperation.g:3023:2: rule__ArrayType__Group_5__0__Impl rule__ArrayType__Group_5__1
            {
            pushFollow(FOLLOW_27);
            rule__ArrayType__Group_5__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group_5__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5__0"


    // $ANTLR start "rule__ArrayType__Group_5__0__Impl"
    // InternalOperation.g:3030:1: rule__ArrayType__Group_5__0__Impl : ( '=' ) ;
    public final void rule__ArrayType__Group_5__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3034:1: ( ( '=' ) )
            // InternalOperation.g:3035:1: ( '=' )
            {
            // InternalOperation.g:3035:1: ( '=' )
            // InternalOperation.g:3036:2: '='
            {
             before(grammarAccess.getArrayTypeAccess().getEqualsSignKeyword_5_0()); 
            match(input,33,FOLLOW_2); 
             after(grammarAccess.getArrayTypeAccess().getEqualsSignKeyword_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5__0__Impl"


    // $ANTLR start "rule__ArrayType__Group_5__1"
    // InternalOperation.g:3045:1: rule__ArrayType__Group_5__1 : rule__ArrayType__Group_5__1__Impl rule__ArrayType__Group_5__2 ;
    public final void rule__ArrayType__Group_5__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3049:1: ( rule__ArrayType__Group_5__1__Impl rule__ArrayType__Group_5__2 )
            // InternalOperation.g:3050:2: rule__ArrayType__Group_5__1__Impl rule__ArrayType__Group_5__2
            {
            pushFollow(FOLLOW_28);
            rule__ArrayType__Group_5__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group_5__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5__1"


    // $ANTLR start "rule__ArrayType__Group_5__1__Impl"
    // InternalOperation.g:3057:1: rule__ArrayType__Group_5__1__Impl : ( '[' ) ;
    public final void rule__ArrayType__Group_5__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3061:1: ( ( '[' ) )
            // InternalOperation.g:3062:1: ( '[' )
            {
            // InternalOperation.g:3062:1: ( '[' )
            // InternalOperation.g:3063:2: '['
            {
             before(grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_5_1()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getArrayTypeAccess().getLeftSquareBracketKeyword_5_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5__1__Impl"


    // $ANTLR start "rule__ArrayType__Group_5__2"
    // InternalOperation.g:3072:1: rule__ArrayType__Group_5__2 : rule__ArrayType__Group_5__2__Impl rule__ArrayType__Group_5__3 ;
    public final void rule__ArrayType__Group_5__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3076:1: ( rule__ArrayType__Group_5__2__Impl rule__ArrayType__Group_5__3 )
            // InternalOperation.g:3077:2: rule__ArrayType__Group_5__2__Impl rule__ArrayType__Group_5__3
            {
            pushFollow(FOLLOW_28);
            rule__ArrayType__Group_5__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group_5__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5__2"


    // $ANTLR start "rule__ArrayType__Group_5__2__Impl"
    // InternalOperation.g:3084:1: rule__ArrayType__Group_5__2__Impl : ( ( rule__ArrayType__Group_5_2__0 )? ) ;
    public final void rule__ArrayType__Group_5__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3088:1: ( ( ( rule__ArrayType__Group_5_2__0 )? ) )
            // InternalOperation.g:3089:1: ( ( rule__ArrayType__Group_5_2__0 )? )
            {
            // InternalOperation.g:3089:1: ( ( rule__ArrayType__Group_5_2__0 )? )
            // InternalOperation.g:3090:2: ( rule__ArrayType__Group_5_2__0 )?
            {
             before(grammarAccess.getArrayTypeAccess().getGroup_5_2()); 
            // InternalOperation.g:3091:2: ( rule__ArrayType__Group_5_2__0 )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( ((LA24_0>=RULE_INT && LA24_0<=RULE_ID)||(LA24_0>=11 && LA24_0<=12)||LA24_0==32||LA24_0==34||LA24_0==36) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // InternalOperation.g:3091:3: rule__ArrayType__Group_5_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ArrayType__Group_5_2__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArrayTypeAccess().getGroup_5_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5__2__Impl"


    // $ANTLR start "rule__ArrayType__Group_5__3"
    // InternalOperation.g:3099:1: rule__ArrayType__Group_5__3 : rule__ArrayType__Group_5__3__Impl ;
    public final void rule__ArrayType__Group_5__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3103:1: ( rule__ArrayType__Group_5__3__Impl )
            // InternalOperation.g:3104:2: rule__ArrayType__Group_5__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__Group_5__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5__3"


    // $ANTLR start "rule__ArrayType__Group_5__3__Impl"
    // InternalOperation.g:3110:1: rule__ArrayType__Group_5__3__Impl : ( ']' ) ;
    public final void rule__ArrayType__Group_5__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3114:1: ( ( ']' ) )
            // InternalOperation.g:3115:1: ( ']' )
            {
            // InternalOperation.g:3115:1: ( ']' )
            // InternalOperation.g:3116:2: ']'
            {
             before(grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_5_3()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getArrayTypeAccess().getRightSquareBracketKeyword_5_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5__3__Impl"


    // $ANTLR start "rule__ArrayType__Group_5_2__0"
    // InternalOperation.g:3126:1: rule__ArrayType__Group_5_2__0 : rule__ArrayType__Group_5_2__0__Impl rule__ArrayType__Group_5_2__1 ;
    public final void rule__ArrayType__Group_5_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3130:1: ( rule__ArrayType__Group_5_2__0__Impl rule__ArrayType__Group_5_2__1 )
            // InternalOperation.g:3131:2: rule__ArrayType__Group_5_2__0__Impl rule__ArrayType__Group_5_2__1
            {
            pushFollow(FOLLOW_10);
            rule__ArrayType__Group_5_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group_5_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5_2__0"


    // $ANTLR start "rule__ArrayType__Group_5_2__0__Impl"
    // InternalOperation.g:3138:1: rule__ArrayType__Group_5_2__0__Impl : ( ( rule__ArrayType__ValuesAssignment_5_2_0 ) ) ;
    public final void rule__ArrayType__Group_5_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3142:1: ( ( ( rule__ArrayType__ValuesAssignment_5_2_0 ) ) )
            // InternalOperation.g:3143:1: ( ( rule__ArrayType__ValuesAssignment_5_2_0 ) )
            {
            // InternalOperation.g:3143:1: ( ( rule__ArrayType__ValuesAssignment_5_2_0 ) )
            // InternalOperation.g:3144:2: ( rule__ArrayType__ValuesAssignment_5_2_0 )
            {
             before(grammarAccess.getArrayTypeAccess().getValuesAssignment_5_2_0()); 
            // InternalOperation.g:3145:2: ( rule__ArrayType__ValuesAssignment_5_2_0 )
            // InternalOperation.g:3145:3: rule__ArrayType__ValuesAssignment_5_2_0
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__ValuesAssignment_5_2_0();

            state._fsp--;


            }

             after(grammarAccess.getArrayTypeAccess().getValuesAssignment_5_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5_2__0__Impl"


    // $ANTLR start "rule__ArrayType__Group_5_2__1"
    // InternalOperation.g:3153:1: rule__ArrayType__Group_5_2__1 : rule__ArrayType__Group_5_2__1__Impl ;
    public final void rule__ArrayType__Group_5_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3157:1: ( rule__ArrayType__Group_5_2__1__Impl )
            // InternalOperation.g:3158:2: rule__ArrayType__Group_5_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__Group_5_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5_2__1"


    // $ANTLR start "rule__ArrayType__Group_5_2__1__Impl"
    // InternalOperation.g:3164:1: rule__ArrayType__Group_5_2__1__Impl : ( ( rule__ArrayType__Group_5_2_1__0 )* ) ;
    public final void rule__ArrayType__Group_5_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3168:1: ( ( ( rule__ArrayType__Group_5_2_1__0 )* ) )
            // InternalOperation.g:3169:1: ( ( rule__ArrayType__Group_5_2_1__0 )* )
            {
            // InternalOperation.g:3169:1: ( ( rule__ArrayType__Group_5_2_1__0 )* )
            // InternalOperation.g:3170:2: ( rule__ArrayType__Group_5_2_1__0 )*
            {
             before(grammarAccess.getArrayTypeAccess().getGroup_5_2_1()); 
            // InternalOperation.g:3171:2: ( rule__ArrayType__Group_5_2_1__0 )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==26) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // InternalOperation.g:3171:3: rule__ArrayType__Group_5_2_1__0
            	    {
            	    pushFollow(FOLLOW_11);
            	    rule__ArrayType__Group_5_2_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

             after(grammarAccess.getArrayTypeAccess().getGroup_5_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5_2__1__Impl"


    // $ANTLR start "rule__ArrayType__Group_5_2_1__0"
    // InternalOperation.g:3180:1: rule__ArrayType__Group_5_2_1__0 : rule__ArrayType__Group_5_2_1__0__Impl rule__ArrayType__Group_5_2_1__1 ;
    public final void rule__ArrayType__Group_5_2_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3184:1: ( rule__ArrayType__Group_5_2_1__0__Impl rule__ArrayType__Group_5_2_1__1 )
            // InternalOperation.g:3185:2: rule__ArrayType__Group_5_2_1__0__Impl rule__ArrayType__Group_5_2_1__1
            {
            pushFollow(FOLLOW_21);
            rule__ArrayType__Group_5_2_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArrayType__Group_5_2_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5_2_1__0"


    // $ANTLR start "rule__ArrayType__Group_5_2_1__0__Impl"
    // InternalOperation.g:3192:1: rule__ArrayType__Group_5_2_1__0__Impl : ( ',' ) ;
    public final void rule__ArrayType__Group_5_2_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3196:1: ( ( ',' ) )
            // InternalOperation.g:3197:1: ( ',' )
            {
            // InternalOperation.g:3197:1: ( ',' )
            // InternalOperation.g:3198:2: ','
            {
             before(grammarAccess.getArrayTypeAccess().getCommaKeyword_5_2_1_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getArrayTypeAccess().getCommaKeyword_5_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5_2_1__0__Impl"


    // $ANTLR start "rule__ArrayType__Group_5_2_1__1"
    // InternalOperation.g:3207:1: rule__ArrayType__Group_5_2_1__1 : rule__ArrayType__Group_5_2_1__1__Impl ;
    public final void rule__ArrayType__Group_5_2_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3211:1: ( rule__ArrayType__Group_5_2_1__1__Impl )
            // InternalOperation.g:3212:2: rule__ArrayType__Group_5_2_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__Group_5_2_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5_2_1__1"


    // $ANTLR start "rule__ArrayType__Group_5_2_1__1__Impl"
    // InternalOperation.g:3218:1: rule__ArrayType__Group_5_2_1__1__Impl : ( ( rule__ArrayType__ValuesAssignment_5_2_1_1 ) ) ;
    public final void rule__ArrayType__Group_5_2_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3222:1: ( ( ( rule__ArrayType__ValuesAssignment_5_2_1_1 ) ) )
            // InternalOperation.g:3223:1: ( ( rule__ArrayType__ValuesAssignment_5_2_1_1 ) )
            {
            // InternalOperation.g:3223:1: ( ( rule__ArrayType__ValuesAssignment_5_2_1_1 ) )
            // InternalOperation.g:3224:2: ( rule__ArrayType__ValuesAssignment_5_2_1_1 )
            {
             before(grammarAccess.getArrayTypeAccess().getValuesAssignment_5_2_1_1()); 
            // InternalOperation.g:3225:2: ( rule__ArrayType__ValuesAssignment_5_2_1_1 )
            // InternalOperation.g:3225:3: rule__ArrayType__ValuesAssignment_5_2_1_1
            {
            pushFollow(FOLLOW_2);
            rule__ArrayType__ValuesAssignment_5_2_1_1();

            state._fsp--;


            }

             after(grammarAccess.getArrayTypeAccess().getValuesAssignment_5_2_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__Group_5_2_1__1__Impl"


    // $ANTLR start "rule__EInt__Group__0"
    // InternalOperation.g:3234:1: rule__EInt__Group__0 : rule__EInt__Group__0__Impl rule__EInt__Group__1 ;
    public final void rule__EInt__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3238:1: ( rule__EInt__Group__0__Impl rule__EInt__Group__1 )
            // InternalOperation.g:3239:2: rule__EInt__Group__0__Impl rule__EInt__Group__1
            {
            pushFollow(FOLLOW_22);
            rule__EInt__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EInt__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EInt__Group__0"


    // $ANTLR start "rule__EInt__Group__0__Impl"
    // InternalOperation.g:3246:1: rule__EInt__Group__0__Impl : ( ( '-' )? ) ;
    public final void rule__EInt__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3250:1: ( ( ( '-' )? ) )
            // InternalOperation.g:3251:1: ( ( '-' )? )
            {
            // InternalOperation.g:3251:1: ( ( '-' )? )
            // InternalOperation.g:3252:2: ( '-' )?
            {
             before(grammarAccess.getEIntAccess().getHyphenMinusKeyword_0()); 
            // InternalOperation.g:3253:2: ( '-' )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==36) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // InternalOperation.g:3253:3: '-'
                    {
                    match(input,36,FOLLOW_2); 

                    }
                    break;

            }

             after(grammarAccess.getEIntAccess().getHyphenMinusKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EInt__Group__0__Impl"


    // $ANTLR start "rule__EInt__Group__1"
    // InternalOperation.g:3261:1: rule__EInt__Group__1 : rule__EInt__Group__1__Impl ;
    public final void rule__EInt__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3265:1: ( rule__EInt__Group__1__Impl )
            // InternalOperation.g:3266:2: rule__EInt__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__EInt__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EInt__Group__1"


    // $ANTLR start "rule__EInt__Group__1__Impl"
    // InternalOperation.g:3272:1: rule__EInt__Group__1__Impl : ( RULE_INT ) ;
    public final void rule__EInt__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3276:1: ( ( RULE_INT ) )
            // InternalOperation.g:3277:1: ( RULE_INT )
            {
            // InternalOperation.g:3277:1: ( RULE_INT )
            // InternalOperation.g:3278:2: RULE_INT
            {
             before(grammarAccess.getEIntAccess().getINTTerminalRuleCall_1()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getEIntAccess().getINTTerminalRuleCall_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EInt__Group__1__Impl"


    // $ANTLR start "rule__EFloat__Group__0"
    // InternalOperation.g:3288:1: rule__EFloat__Group__0 : rule__EFloat__Group__0__Impl rule__EFloat__Group__1 ;
    public final void rule__EFloat__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3292:1: ( rule__EFloat__Group__0__Impl rule__EFloat__Group__1 )
            // InternalOperation.g:3293:2: rule__EFloat__Group__0__Impl rule__EFloat__Group__1
            {
            pushFollow(FOLLOW_23);
            rule__EFloat__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EFloat__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__0"


    // $ANTLR start "rule__EFloat__Group__0__Impl"
    // InternalOperation.g:3300:1: rule__EFloat__Group__0__Impl : ( ( '-' )? ) ;
    public final void rule__EFloat__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3304:1: ( ( ( '-' )? ) )
            // InternalOperation.g:3305:1: ( ( '-' )? )
            {
            // InternalOperation.g:3305:1: ( ( '-' )? )
            // InternalOperation.g:3306:2: ( '-' )?
            {
             before(grammarAccess.getEFloatAccess().getHyphenMinusKeyword_0()); 
            // InternalOperation.g:3307:2: ( '-' )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==36) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // InternalOperation.g:3307:3: '-'
                    {
                    match(input,36,FOLLOW_2); 

                    }
                    break;

            }

             after(grammarAccess.getEFloatAccess().getHyphenMinusKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__0__Impl"


    // $ANTLR start "rule__EFloat__Group__1"
    // InternalOperation.g:3315:1: rule__EFloat__Group__1 : rule__EFloat__Group__1__Impl rule__EFloat__Group__2 ;
    public final void rule__EFloat__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3319:1: ( rule__EFloat__Group__1__Impl rule__EFloat__Group__2 )
            // InternalOperation.g:3320:2: rule__EFloat__Group__1__Impl rule__EFloat__Group__2
            {
            pushFollow(FOLLOW_23);
            rule__EFloat__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EFloat__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__1"


    // $ANTLR start "rule__EFloat__Group__1__Impl"
    // InternalOperation.g:3327:1: rule__EFloat__Group__1__Impl : ( ( RULE_INT )? ) ;
    public final void rule__EFloat__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3331:1: ( ( ( RULE_INT )? ) )
            // InternalOperation.g:3332:1: ( ( RULE_INT )? )
            {
            // InternalOperation.g:3332:1: ( ( RULE_INT )? )
            // InternalOperation.g:3333:2: ( RULE_INT )?
            {
             before(grammarAccess.getEFloatAccess().getINTTerminalRuleCall_1()); 
            // InternalOperation.g:3334:2: ( RULE_INT )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_INT) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // InternalOperation.g:3334:3: RULE_INT
                    {
                    match(input,RULE_INT,FOLLOW_2); 

                    }
                    break;

            }

             after(grammarAccess.getEFloatAccess().getINTTerminalRuleCall_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__1__Impl"


    // $ANTLR start "rule__EFloat__Group__2"
    // InternalOperation.g:3342:1: rule__EFloat__Group__2 : rule__EFloat__Group__2__Impl rule__EFloat__Group__3 ;
    public final void rule__EFloat__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3346:1: ( rule__EFloat__Group__2__Impl rule__EFloat__Group__3 )
            // InternalOperation.g:3347:2: rule__EFloat__Group__2__Impl rule__EFloat__Group__3
            {
            pushFollow(FOLLOW_26);
            rule__EFloat__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EFloat__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__2"


    // $ANTLR start "rule__EFloat__Group__2__Impl"
    // InternalOperation.g:3354:1: rule__EFloat__Group__2__Impl : ( '.' ) ;
    public final void rule__EFloat__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3358:1: ( ( '.' ) )
            // InternalOperation.g:3359:1: ( '.' )
            {
            // InternalOperation.g:3359:1: ( '.' )
            // InternalOperation.g:3360:2: '.'
            {
             before(grammarAccess.getEFloatAccess().getFullStopKeyword_2()); 
            match(input,32,FOLLOW_2); 
             after(grammarAccess.getEFloatAccess().getFullStopKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__2__Impl"


    // $ANTLR start "rule__EFloat__Group__3"
    // InternalOperation.g:3369:1: rule__EFloat__Group__3 : rule__EFloat__Group__3__Impl rule__EFloat__Group__4 ;
    public final void rule__EFloat__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3373:1: ( rule__EFloat__Group__3__Impl rule__EFloat__Group__4 )
            // InternalOperation.g:3374:2: rule__EFloat__Group__3__Impl rule__EFloat__Group__4
            {
            pushFollow(FOLLOW_30);
            rule__EFloat__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EFloat__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__3"


    // $ANTLR start "rule__EFloat__Group__3__Impl"
    // InternalOperation.g:3381:1: rule__EFloat__Group__3__Impl : ( RULE_INT ) ;
    public final void rule__EFloat__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3385:1: ( ( RULE_INT ) )
            // InternalOperation.g:3386:1: ( RULE_INT )
            {
            // InternalOperation.g:3386:1: ( RULE_INT )
            // InternalOperation.g:3387:2: RULE_INT
            {
             before(grammarAccess.getEFloatAccess().getINTTerminalRuleCall_3()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getEFloatAccess().getINTTerminalRuleCall_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__3__Impl"


    // $ANTLR start "rule__EFloat__Group__4"
    // InternalOperation.g:3396:1: rule__EFloat__Group__4 : rule__EFloat__Group__4__Impl ;
    public final void rule__EFloat__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3400:1: ( rule__EFloat__Group__4__Impl )
            // InternalOperation.g:3401:2: rule__EFloat__Group__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__EFloat__Group__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__4"


    // $ANTLR start "rule__EFloat__Group__4__Impl"
    // InternalOperation.g:3407:1: rule__EFloat__Group__4__Impl : ( ( rule__EFloat__Group_4__0 )? ) ;
    public final void rule__EFloat__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3411:1: ( ( ( rule__EFloat__Group_4__0 )? ) )
            // InternalOperation.g:3412:1: ( ( rule__EFloat__Group_4__0 )? )
            {
            // InternalOperation.g:3412:1: ( ( rule__EFloat__Group_4__0 )? )
            // InternalOperation.g:3413:2: ( rule__EFloat__Group_4__0 )?
            {
             before(grammarAccess.getEFloatAccess().getGroup_4()); 
            // InternalOperation.g:3414:2: ( rule__EFloat__Group_4__0 )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( ((LA29_0>=13 && LA29_0<=14)) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // InternalOperation.g:3414:3: rule__EFloat__Group_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__EFloat__Group_4__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getEFloatAccess().getGroup_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group__4__Impl"


    // $ANTLR start "rule__EFloat__Group_4__0"
    // InternalOperation.g:3423:1: rule__EFloat__Group_4__0 : rule__EFloat__Group_4__0__Impl rule__EFloat__Group_4__1 ;
    public final void rule__EFloat__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3427:1: ( rule__EFloat__Group_4__0__Impl rule__EFloat__Group_4__1 )
            // InternalOperation.g:3428:2: rule__EFloat__Group_4__0__Impl rule__EFloat__Group_4__1
            {
            pushFollow(FOLLOW_22);
            rule__EFloat__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EFloat__Group_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group_4__0"


    // $ANTLR start "rule__EFloat__Group_4__0__Impl"
    // InternalOperation.g:3435:1: rule__EFloat__Group_4__0__Impl : ( ( rule__EFloat__Alternatives_4_0 ) ) ;
    public final void rule__EFloat__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3439:1: ( ( ( rule__EFloat__Alternatives_4_0 ) ) )
            // InternalOperation.g:3440:1: ( ( rule__EFloat__Alternatives_4_0 ) )
            {
            // InternalOperation.g:3440:1: ( ( rule__EFloat__Alternatives_4_0 ) )
            // InternalOperation.g:3441:2: ( rule__EFloat__Alternatives_4_0 )
            {
             before(grammarAccess.getEFloatAccess().getAlternatives_4_0()); 
            // InternalOperation.g:3442:2: ( rule__EFloat__Alternatives_4_0 )
            // InternalOperation.g:3442:3: rule__EFloat__Alternatives_4_0
            {
            pushFollow(FOLLOW_2);
            rule__EFloat__Alternatives_4_0();

            state._fsp--;


            }

             after(grammarAccess.getEFloatAccess().getAlternatives_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group_4__0__Impl"


    // $ANTLR start "rule__EFloat__Group_4__1"
    // InternalOperation.g:3450:1: rule__EFloat__Group_4__1 : rule__EFloat__Group_4__1__Impl rule__EFloat__Group_4__2 ;
    public final void rule__EFloat__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3454:1: ( rule__EFloat__Group_4__1__Impl rule__EFloat__Group_4__2 )
            // InternalOperation.g:3455:2: rule__EFloat__Group_4__1__Impl rule__EFloat__Group_4__2
            {
            pushFollow(FOLLOW_22);
            rule__EFloat__Group_4__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EFloat__Group_4__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group_4__1"


    // $ANTLR start "rule__EFloat__Group_4__1__Impl"
    // InternalOperation.g:3462:1: rule__EFloat__Group_4__1__Impl : ( ( '-' )? ) ;
    public final void rule__EFloat__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3466:1: ( ( ( '-' )? ) )
            // InternalOperation.g:3467:1: ( ( '-' )? )
            {
            // InternalOperation.g:3467:1: ( ( '-' )? )
            // InternalOperation.g:3468:2: ( '-' )?
            {
             before(grammarAccess.getEFloatAccess().getHyphenMinusKeyword_4_1()); 
            // InternalOperation.g:3469:2: ( '-' )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==36) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // InternalOperation.g:3469:3: '-'
                    {
                    match(input,36,FOLLOW_2); 

                    }
                    break;

            }

             after(grammarAccess.getEFloatAccess().getHyphenMinusKeyword_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group_4__1__Impl"


    // $ANTLR start "rule__EFloat__Group_4__2"
    // InternalOperation.g:3477:1: rule__EFloat__Group_4__2 : rule__EFloat__Group_4__2__Impl ;
    public final void rule__EFloat__Group_4__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3481:1: ( rule__EFloat__Group_4__2__Impl )
            // InternalOperation.g:3482:2: rule__EFloat__Group_4__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__EFloat__Group_4__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group_4__2"


    // $ANTLR start "rule__EFloat__Group_4__2__Impl"
    // InternalOperation.g:3488:1: rule__EFloat__Group_4__2__Impl : ( RULE_INT ) ;
    public final void rule__EFloat__Group_4__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3492:1: ( ( RULE_INT ) )
            // InternalOperation.g:3493:1: ( RULE_INT )
            {
            // InternalOperation.g:3493:1: ( RULE_INT )
            // InternalOperation.g:3494:2: RULE_INT
            {
             before(grammarAccess.getEFloatAccess().getINTTerminalRuleCall_4_2()); 
            match(input,RULE_INT,FOLLOW_2); 
             after(grammarAccess.getEFloatAccess().getINTTerminalRuleCall_4_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EFloat__Group_4__2__Impl"


    // $ANTLR start "rule__EDate__Group__0"
    // InternalOperation.g:3504:1: rule__EDate__Group__0 : rule__EDate__Group__0__Impl rule__EDate__Group__1 ;
    public final void rule__EDate__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3508:1: ( rule__EDate__Group__0__Impl rule__EDate__Group__1 )
            // InternalOperation.g:3509:2: rule__EDate__Group__0__Impl rule__EDate__Group__1
            {
            pushFollow(FOLLOW_31);
            rule__EDate__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EDate__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__0"


    // $ANTLR start "rule__EDate__Group__0__Impl"
    // InternalOperation.g:3516:1: rule__EDate__Group__0__Impl : ( ruleDay ) ;
    public final void rule__EDate__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3520:1: ( ( ruleDay ) )
            // InternalOperation.g:3521:1: ( ruleDay )
            {
            // InternalOperation.g:3521:1: ( ruleDay )
            // InternalOperation.g:3522:2: ruleDay
            {
             before(grammarAccess.getEDateAccess().getDayParserRuleCall_0()); 
            pushFollow(FOLLOW_2);
            ruleDay();

            state._fsp--;

             after(grammarAccess.getEDateAccess().getDayParserRuleCall_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__0__Impl"


    // $ANTLR start "rule__EDate__Group__1"
    // InternalOperation.g:3531:1: rule__EDate__Group__1 : rule__EDate__Group__1__Impl rule__EDate__Group__2 ;
    public final void rule__EDate__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3535:1: ( rule__EDate__Group__1__Impl rule__EDate__Group__2 )
            // InternalOperation.g:3536:2: rule__EDate__Group__1__Impl rule__EDate__Group__2
            {
            pushFollow(FOLLOW_26);
            rule__EDate__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EDate__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__1"


    // $ANTLR start "rule__EDate__Group__1__Impl"
    // InternalOperation.g:3543:1: rule__EDate__Group__1__Impl : ( '-' ) ;
    public final void rule__EDate__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3547:1: ( ( '-' ) )
            // InternalOperation.g:3548:1: ( '-' )
            {
            // InternalOperation.g:3548:1: ( '-' )
            // InternalOperation.g:3549:2: '-'
            {
             before(grammarAccess.getEDateAccess().getHyphenMinusKeyword_1()); 
            match(input,36,FOLLOW_2); 
             after(grammarAccess.getEDateAccess().getHyphenMinusKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__1__Impl"


    // $ANTLR start "rule__EDate__Group__2"
    // InternalOperation.g:3558:1: rule__EDate__Group__2 : rule__EDate__Group__2__Impl rule__EDate__Group__3 ;
    public final void rule__EDate__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3562:1: ( rule__EDate__Group__2__Impl rule__EDate__Group__3 )
            // InternalOperation.g:3563:2: rule__EDate__Group__2__Impl rule__EDate__Group__3
            {
            pushFollow(FOLLOW_31);
            rule__EDate__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EDate__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__2"


    // $ANTLR start "rule__EDate__Group__2__Impl"
    // InternalOperation.g:3570:1: rule__EDate__Group__2__Impl : ( ruleMonth ) ;
    public final void rule__EDate__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3574:1: ( ( ruleMonth ) )
            // InternalOperation.g:3575:1: ( ruleMonth )
            {
            // InternalOperation.g:3575:1: ( ruleMonth )
            // InternalOperation.g:3576:2: ruleMonth
            {
             before(grammarAccess.getEDateAccess().getMonthParserRuleCall_2()); 
            pushFollow(FOLLOW_2);
            ruleMonth();

            state._fsp--;

             after(grammarAccess.getEDateAccess().getMonthParserRuleCall_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__2__Impl"


    // $ANTLR start "rule__EDate__Group__3"
    // InternalOperation.g:3585:1: rule__EDate__Group__3 : rule__EDate__Group__3__Impl rule__EDate__Group__4 ;
    public final void rule__EDate__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3589:1: ( rule__EDate__Group__3__Impl rule__EDate__Group__4 )
            // InternalOperation.g:3590:2: rule__EDate__Group__3__Impl rule__EDate__Group__4
            {
            pushFollow(FOLLOW_26);
            rule__EDate__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EDate__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__3"


    // $ANTLR start "rule__EDate__Group__3__Impl"
    // InternalOperation.g:3597:1: rule__EDate__Group__3__Impl : ( '-' ) ;
    public final void rule__EDate__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3601:1: ( ( '-' ) )
            // InternalOperation.g:3602:1: ( '-' )
            {
            // InternalOperation.g:3602:1: ( '-' )
            // InternalOperation.g:3603:2: '-'
            {
             before(grammarAccess.getEDateAccess().getHyphenMinusKeyword_3()); 
            match(input,36,FOLLOW_2); 
             after(grammarAccess.getEDateAccess().getHyphenMinusKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__3__Impl"


    // $ANTLR start "rule__EDate__Group__4"
    // InternalOperation.g:3612:1: rule__EDate__Group__4 : rule__EDate__Group__4__Impl ;
    public final void rule__EDate__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3616:1: ( rule__EDate__Group__4__Impl )
            // InternalOperation.g:3617:2: rule__EDate__Group__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__EDate__Group__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__4"


    // $ANTLR start "rule__EDate__Group__4__Impl"
    // InternalOperation.g:3623:1: rule__EDate__Group__4__Impl : ( ruleYear ) ;
    public final void rule__EDate__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3627:1: ( ( ruleYear ) )
            // InternalOperation.g:3628:1: ( ruleYear )
            {
            // InternalOperation.g:3628:1: ( ruleYear )
            // InternalOperation.g:3629:2: ruleYear
            {
             before(grammarAccess.getEDateAccess().getYearParserRuleCall_4()); 
            pushFollow(FOLLOW_2);
            ruleYear();

            state._fsp--;

             after(grammarAccess.getEDateAccess().getYearParserRuleCall_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EDate__Group__4__Impl"


    // $ANTLR start "rule__DataModel__UnorderedGroup"
    // InternalOperation.g:3639:1: rule__DataModel__UnorderedGroup : rule__DataModel__UnorderedGroup__0 {...}?;
    public final void rule__DataModel__UnorderedGroup() throws RecognitionException {

        		int stackSize = keepStackSize();
        		getUnorderedGroupHelper().enter(grammarAccess.getDataModelAccess().getUnorderedGroup());
        	
        try {
            // InternalOperation.g:3644:1: ( rule__DataModel__UnorderedGroup__0 {...}?)
            // InternalOperation.g:3645:2: rule__DataModel__UnorderedGroup__0 {...}?
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__UnorderedGroup__0();

            state._fsp--;

            if ( ! getUnorderedGroupHelper().canLeave(grammarAccess.getDataModelAccess().getUnorderedGroup()) ) {
                throw new FailedPredicateException(input, "rule__DataModel__UnorderedGroup", "getUnorderedGroupHelper().canLeave(grammarAccess.getDataModelAccess().getUnorderedGroup())");
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	getUnorderedGroupHelper().leave(grammarAccess.getDataModelAccess().getUnorderedGroup());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__UnorderedGroup"


    // $ANTLR start "rule__DataModel__UnorderedGroup__Impl"
    // InternalOperation.g:3653:1: rule__DataModel__UnorderedGroup__Impl : ( ({...}? => ( ( ( rule__DataModel__Group_0__0 ) ) ) ) | ({...}? => ( ( ( rule__DataModel__Group_1__0 ) ) ) ) ) ;
    public final void rule__DataModel__UnorderedGroup__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
        	
        try {
            // InternalOperation.g:3658:1: ( ( ({...}? => ( ( ( rule__DataModel__Group_0__0 ) ) ) ) | ({...}? => ( ( ( rule__DataModel__Group_1__0 ) ) ) ) ) )
            // InternalOperation.g:3659:3: ( ({...}? => ( ( ( rule__DataModel__Group_0__0 ) ) ) ) | ({...}? => ( ( ( rule__DataModel__Group_1__0 ) ) ) ) )
            {
            // InternalOperation.g:3659:3: ( ({...}? => ( ( ( rule__DataModel__Group_0__0 ) ) ) ) | ({...}? => ( ( ( rule__DataModel__Group_1__0 ) ) ) ) )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( LA31_0 == 29 && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
                alt31=1;
            }
            else if ( ( LA31_0 == 25 || LA31_0 == 31 ) && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // InternalOperation.g:3660:3: ({...}? => ( ( ( rule__DataModel__Group_0__0 ) ) ) )
                    {
                    // InternalOperation.g:3660:3: ({...}? => ( ( ( rule__DataModel__Group_0__0 ) ) ) )
                    // InternalOperation.g:3661:4: {...}? => ( ( ( rule__DataModel__Group_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
                        throw new FailedPredicateException(input, "rule__DataModel__UnorderedGroup__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0)");
                    }
                    // InternalOperation.g:3661:103: ( ( ( rule__DataModel__Group_0__0 ) ) )
                    // InternalOperation.g:3662:5: ( ( rule__DataModel__Group_0__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0);
                    				

                    					selected = true;
                    				
                    // InternalOperation.g:3668:5: ( ( rule__DataModel__Group_0__0 ) )
                    // InternalOperation.g:3669:6: ( rule__DataModel__Group_0__0 )
                    {
                     before(grammarAccess.getDataModelAccess().getGroup_0()); 
                    // InternalOperation.g:3670:6: ( rule__DataModel__Group_0__0 )
                    // InternalOperation.g:3670:7: rule__DataModel__Group_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__DataModel__Group_0__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getDataModelAccess().getGroup_0()); 

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalOperation.g:3675:3: ({...}? => ( ( ( rule__DataModel__Group_1__0 ) ) ) )
                    {
                    // InternalOperation.g:3675:3: ({...}? => ( ( ( rule__DataModel__Group_1__0 ) ) ) )
                    // InternalOperation.g:3676:4: {...}? => ( ( ( rule__DataModel__Group_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
                        throw new FailedPredicateException(input, "rule__DataModel__UnorderedGroup__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1)");
                    }
                    // InternalOperation.g:3676:103: ( ( ( rule__DataModel__Group_1__0 ) ) )
                    // InternalOperation.g:3677:5: ( ( rule__DataModel__Group_1__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1);
                    				

                    					selected = true;
                    				
                    // InternalOperation.g:3683:5: ( ( rule__DataModel__Group_1__0 ) )
                    // InternalOperation.g:3684:6: ( rule__DataModel__Group_1__0 )
                    {
                     before(grammarAccess.getDataModelAccess().getGroup_1()); 
                    // InternalOperation.g:3685:6: ( rule__DataModel__Group_1__0 )
                    // InternalOperation.g:3685:7: rule__DataModel__Group_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__DataModel__Group_1__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getDataModelAccess().getGroup_1()); 

                    }


                    }


                    }


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	if (selected)
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getDataModelAccess().getUnorderedGroup());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__UnorderedGroup__Impl"


    // $ANTLR start "rule__DataModel__UnorderedGroup__0"
    // InternalOperation.g:3698:1: rule__DataModel__UnorderedGroup__0 : rule__DataModel__UnorderedGroup__Impl ( rule__DataModel__UnorderedGroup__1 )? ;
    public final void rule__DataModel__UnorderedGroup__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3702:1: ( rule__DataModel__UnorderedGroup__Impl ( rule__DataModel__UnorderedGroup__1 )? )
            // InternalOperation.g:3703:2: rule__DataModel__UnorderedGroup__Impl ( rule__DataModel__UnorderedGroup__1 )?
            {
            pushFollow(FOLLOW_32);
            rule__DataModel__UnorderedGroup__Impl();

            state._fsp--;

            // InternalOperation.g:3704:2: ( rule__DataModel__UnorderedGroup__1 )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( LA32_0 == 29 && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 0) ) {
                alt32=1;
            }
            else if ( ( LA32_0 == 25 || LA32_0 == 31 ) && getUnorderedGroupHelper().canSelect(grammarAccess.getDataModelAccess().getUnorderedGroup(), 1) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalOperation.g:3704:2: rule__DataModel__UnorderedGroup__1
                    {
                    pushFollow(FOLLOW_2);
                    rule__DataModel__UnorderedGroup__1();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__UnorderedGroup__0"


    // $ANTLR start "rule__DataModel__UnorderedGroup__1"
    // InternalOperation.g:3710:1: rule__DataModel__UnorderedGroup__1 : rule__DataModel__UnorderedGroup__Impl ;
    public final void rule__DataModel__UnorderedGroup__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3714:1: ( rule__DataModel__UnorderedGroup__Impl )
            // InternalOperation.g:3715:2: rule__DataModel__UnorderedGroup__Impl
            {
            pushFollow(FOLLOW_2);
            rule__DataModel__UnorderedGroup__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__UnorderedGroup__1"


    // $ANTLR start "rule__OperationDescriptions__OperationsAssignment_1_0"
    // InternalOperation.g:3722:1: rule__OperationDescriptions__OperationsAssignment_1_0 : ( ruleOperation ) ;
    public final void rule__OperationDescriptions__OperationsAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3726:1: ( ( ruleOperation ) )
            // InternalOperation.g:3727:2: ( ruleOperation )
            {
            // InternalOperation.g:3727:2: ( ruleOperation )
            // InternalOperation.g:3728:3: ruleOperation
            {
             before(grammarAccess.getOperationDescriptionsAccess().getOperationsOperationParserRuleCall_1_0_0()); 
            pushFollow(FOLLOW_2);
            ruleOperation();

            state._fsp--;

             after(grammarAccess.getOperationDescriptionsAccess().getOperationsOperationParserRuleCall_1_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__OperationsAssignment_1_0"


    // $ANTLR start "rule__OperationDescriptions__OperationsAssignment_1_1"
    // InternalOperation.g:3737:1: rule__OperationDescriptions__OperationsAssignment_1_1 : ( ruleOperation ) ;
    public final void rule__OperationDescriptions__OperationsAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3741:1: ( ( ruleOperation ) )
            // InternalOperation.g:3742:2: ( ruleOperation )
            {
            // InternalOperation.g:3742:2: ( ruleOperation )
            // InternalOperation.g:3743:3: ruleOperation
            {
             before(grammarAccess.getOperationDescriptionsAccess().getOperationsOperationParserRuleCall_1_1_0()); 
            pushFollow(FOLLOW_2);
            ruleOperation();

            state._fsp--;

             after(grammarAccess.getOperationDescriptionsAccess().getOperationsOperationParserRuleCall_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OperationDescriptions__OperationsAssignment_1_1"


    // $ANTLR start "rule__Operation__NameAssignment_2"
    // InternalOperation.g:3752:1: rule__Operation__NameAssignment_2 : ( ruleEString ) ;
    public final void rule__Operation__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3756:1: ( ( ruleEString ) )
            // InternalOperation.g:3757:2: ( ruleEString )
            {
            // InternalOperation.g:3757:2: ( ruleEString )
            // InternalOperation.g:3758:3: ruleEString
            {
             before(grammarAccess.getOperationAccess().getNameEStringParserRuleCall_2_0()); 
            pushFollow(FOLLOW_2);
            ruleEString();

            state._fsp--;

             after(grammarAccess.getOperationAccess().getNameEStringParserRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__NameAssignment_2"


    // $ANTLR start "rule__Operation__InputParametersAssignment_4_0"
    // InternalOperation.g:3767:1: rule__Operation__InputParametersAssignment_4_0 : ( ruleParameter ) ;
    public final void rule__Operation__InputParametersAssignment_4_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3771:1: ( ( ruleParameter ) )
            // InternalOperation.g:3772:2: ( ruleParameter )
            {
            // InternalOperation.g:3772:2: ( ruleParameter )
            // InternalOperation.g:3773:3: ruleParameter
            {
             before(grammarAccess.getOperationAccess().getInputParametersParameterParserRuleCall_4_0_0()); 
            pushFollow(FOLLOW_2);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getOperationAccess().getInputParametersParameterParserRuleCall_4_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__InputParametersAssignment_4_0"


    // $ANTLR start "rule__Operation__InputParametersAssignment_4_1_1"
    // InternalOperation.g:3782:1: rule__Operation__InputParametersAssignment_4_1_1 : ( ruleParameter ) ;
    public final void rule__Operation__InputParametersAssignment_4_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3786:1: ( ( ruleParameter ) )
            // InternalOperation.g:3787:2: ( ruleParameter )
            {
            // InternalOperation.g:3787:2: ( ruleParameter )
            // InternalOperation.g:3788:3: ruleParameter
            {
             before(grammarAccess.getOperationAccess().getInputParametersParameterParserRuleCall_4_1_1_0()); 
            pushFollow(FOLLOW_2);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getOperationAccess().getInputParametersParameterParserRuleCall_4_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__InputParametersAssignment_4_1_1"


    // $ANTLR start "rule__Operation__ExecutableScriptAssignment_7_1"
    // InternalOperation.g:3797:1: rule__Operation__ExecutableScriptAssignment_7_1 : ( ruleEString ) ;
    public final void rule__Operation__ExecutableScriptAssignment_7_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3801:1: ( ( ruleEString ) )
            // InternalOperation.g:3802:2: ( ruleEString )
            {
            // InternalOperation.g:3802:2: ( ruleEString )
            // InternalOperation.g:3803:3: ruleEString
            {
             before(grammarAccess.getOperationAccess().getExecutableScriptEStringParserRuleCall_7_1_0()); 
            pushFollow(FOLLOW_2);
            ruleEString();

            state._fsp--;

             after(grammarAccess.getOperationAccess().getExecutableScriptEStringParserRuleCall_7_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__ExecutableScriptAssignment_7_1"


    // $ANTLR start "rule__Operation__OutputParametersAssignment_8_1"
    // InternalOperation.g:3812:1: rule__Operation__OutputParametersAssignment_8_1 : ( ruleParameter ) ;
    public final void rule__Operation__OutputParametersAssignment_8_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3816:1: ( ( ruleParameter ) )
            // InternalOperation.g:3817:2: ( ruleParameter )
            {
            // InternalOperation.g:3817:2: ( ruleParameter )
            // InternalOperation.g:3818:3: ruleParameter
            {
             before(grammarAccess.getOperationAccess().getOutputParametersParameterParserRuleCall_8_1_0()); 
            pushFollow(FOLLOW_2);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getOperationAccess().getOutputParametersParameterParserRuleCall_8_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Operation__OutputParametersAssignment_8_1"


    // $ANTLR start "rule__DataModel__NameAssignment_0_1"
    // InternalOperation.g:3827:1: rule__DataModel__NameAssignment_0_1 : ( ruleEString ) ;
    public final void rule__DataModel__NameAssignment_0_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3831:1: ( ( ruleEString ) )
            // InternalOperation.g:3832:2: ( ruleEString )
            {
            // InternalOperation.g:3832:2: ( ruleEString )
            // InternalOperation.g:3833:3: ruleEString
            {
             before(grammarAccess.getDataModelAccess().getNameEStringParserRuleCall_0_1_0()); 
            pushFollow(FOLLOW_2);
            ruleEString();

            state._fsp--;

             after(grammarAccess.getDataModelAccess().getNameEStringParserRuleCall_0_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__NameAssignment_0_1"


    // $ANTLR start "rule__DataModel__PrimitivesAssignment_0_3_2"
    // InternalOperation.g:3842:1: rule__DataModel__PrimitivesAssignment_0_3_2 : ( ruleParameter ) ;
    public final void rule__DataModel__PrimitivesAssignment_0_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3846:1: ( ( ruleParameter ) )
            // InternalOperation.g:3847:2: ( ruleParameter )
            {
            // InternalOperation.g:3847:2: ( ruleParameter )
            // InternalOperation.g:3848:3: ruleParameter
            {
             before(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__PrimitivesAssignment_0_3_2"


    // $ANTLR start "rule__DataModel__PrimitivesAssignment_0_3_3_1"
    // InternalOperation.g:3857:1: rule__DataModel__PrimitivesAssignment_0_3_3_1 : ( ruleParameter ) ;
    public final void rule__DataModel__PrimitivesAssignment_0_3_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3861:1: ( ( ruleParameter ) )
            // InternalOperation.g:3862:2: ( ruleParameter )
            {
            // InternalOperation.g:3862:2: ( ruleParameter )
            // InternalOperation.g:3863:3: ruleParameter
            {
             before(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getDataModelAccess().getPrimitivesParameterParserRuleCall_0_3_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__PrimitivesAssignment_0_3_3_1"


    // $ANTLR start "rule__DataModel__CompositesAssignment_1_0_2"
    // InternalOperation.g:3872:1: rule__DataModel__CompositesAssignment_1_0_2 : ( ( RULE_ID ) ) ;
    public final void rule__DataModel__CompositesAssignment_1_0_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3876:1: ( ( ( RULE_ID ) ) )
            // InternalOperation.g:3877:2: ( ( RULE_ID ) )
            {
            // InternalOperation.g:3877:2: ( ( RULE_ID ) )
            // InternalOperation.g:3878:3: ( RULE_ID )
            {
             before(grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_2_0()); 
            // InternalOperation.g:3879:3: ( RULE_ID )
            // InternalOperation.g:3880:4: RULE_ID
            {
             before(grammarAccess.getDataModelAccess().getCompositesDataModelIDTerminalRuleCall_1_0_2_0_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getCompositesDataModelIDTerminalRuleCall_1_0_2_0_1()); 

            }

             after(grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__CompositesAssignment_1_0_2"


    // $ANTLR start "rule__DataModel__CompositesAssignment_1_0_3_1"
    // InternalOperation.g:3891:1: rule__DataModel__CompositesAssignment_1_0_3_1 : ( ( RULE_ID ) ) ;
    public final void rule__DataModel__CompositesAssignment_1_0_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3895:1: ( ( ( RULE_ID ) ) )
            // InternalOperation.g:3896:2: ( ( RULE_ID ) )
            {
            // InternalOperation.g:3896:2: ( ( RULE_ID ) )
            // InternalOperation.g:3897:3: ( RULE_ID )
            {
             before(grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_3_1_0()); 
            // InternalOperation.g:3898:3: ( RULE_ID )
            // InternalOperation.g:3899:4: RULE_ID
            {
             before(grammarAccess.getDataModelAccess().getCompositesDataModelIDTerminalRuleCall_1_0_3_1_0_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getDataModelAccess().getCompositesDataModelIDTerminalRuleCall_1_0_3_1_0_1()); 

            }

             after(grammarAccess.getDataModelAccess().getCompositesDataModelCrossReference_1_0_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__DataModel__CompositesAssignment_1_0_3_1"


    // $ANTLR start "rule__SimpleType__TypeAssignment_1"
    // InternalOperation.g:3910:1: rule__SimpleType__TypeAssignment_1 : ( rulePrimitiveValueType ) ;
    public final void rule__SimpleType__TypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3914:1: ( ( rulePrimitiveValueType ) )
            // InternalOperation.g:3915:2: ( rulePrimitiveValueType )
            {
            // InternalOperation.g:3915:2: ( rulePrimitiveValueType )
            // InternalOperation.g:3916:3: rulePrimitiveValueType
            {
             before(grammarAccess.getSimpleTypeAccess().getTypePrimitiveValueTypeEnumRuleCall_1_0()); 
            pushFollow(FOLLOW_2);
            rulePrimitiveValueType();

            state._fsp--;

             after(grammarAccess.getSimpleTypeAccess().getTypePrimitiveValueTypeEnumRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__TypeAssignment_1"


    // $ANTLR start "rule__SimpleType__NameAssignment_2"
    // InternalOperation.g:3925:1: rule__SimpleType__NameAssignment_2 : ( ruleEString ) ;
    public final void rule__SimpleType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3929:1: ( ( ruleEString ) )
            // InternalOperation.g:3930:2: ( ruleEString )
            {
            // InternalOperation.g:3930:2: ( ruleEString )
            // InternalOperation.g:3931:3: ruleEString
            {
             before(grammarAccess.getSimpleTypeAccess().getNameEStringParserRuleCall_2_0()); 
            pushFollow(FOLLOW_2);
            ruleEString();

            state._fsp--;

             after(grammarAccess.getSimpleTypeAccess().getNameEStringParserRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__NameAssignment_2"


    // $ANTLR start "rule__SimpleType__ValueAssignment_3_1"
    // InternalOperation.g:3940:1: rule__SimpleType__ValueAssignment_3_1 : ( rulePrimitiveValue ) ;
    public final void rule__SimpleType__ValueAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3944:1: ( ( rulePrimitiveValue ) )
            // InternalOperation.g:3945:2: ( rulePrimitiveValue )
            {
            // InternalOperation.g:3945:2: ( rulePrimitiveValue )
            // InternalOperation.g:3946:3: rulePrimitiveValue
            {
             before(grammarAccess.getSimpleTypeAccess().getValuePrimitiveValueParserRuleCall_3_1_0()); 
            pushFollow(FOLLOW_2);
            rulePrimitiveValue();

            state._fsp--;

             after(grammarAccess.getSimpleTypeAccess().getValuePrimitiveValueParserRuleCall_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleType__ValueAssignment_3_1"


    // $ANTLR start "rule__AbstractType__TypeAssignment_1"
    // InternalOperation.g:3955:1: rule__AbstractType__TypeAssignment_1 : ( ( ruleQualifiedName ) ) ;
    public final void rule__AbstractType__TypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3959:1: ( ( ( ruleQualifiedName ) ) )
            // InternalOperation.g:3960:2: ( ( ruleQualifiedName ) )
            {
            // InternalOperation.g:3960:2: ( ( ruleQualifiedName ) )
            // InternalOperation.g:3961:3: ( ruleQualifiedName )
            {
             before(grammarAccess.getAbstractTypeAccess().getTypeDataModelCrossReference_1_0()); 
            // InternalOperation.g:3962:3: ( ruleQualifiedName )
            // InternalOperation.g:3963:4: ruleQualifiedName
            {
             before(grammarAccess.getAbstractTypeAccess().getTypeDataModelQualifiedNameParserRuleCall_1_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedName();

            state._fsp--;

             after(grammarAccess.getAbstractTypeAccess().getTypeDataModelQualifiedNameParserRuleCall_1_0_1()); 

            }

             after(grammarAccess.getAbstractTypeAccess().getTypeDataModelCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__TypeAssignment_1"


    // $ANTLR start "rule__AbstractType__NameAssignment_2"
    // InternalOperation.g:3974:1: rule__AbstractType__NameAssignment_2 : ( ruleEString ) ;
    public final void rule__AbstractType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3978:1: ( ( ruleEString ) )
            // InternalOperation.g:3979:2: ( ruleEString )
            {
            // InternalOperation.g:3979:2: ( ruleEString )
            // InternalOperation.g:3980:3: ruleEString
            {
             before(grammarAccess.getAbstractTypeAccess().getNameEStringParserRuleCall_2_0()); 
            pushFollow(FOLLOW_2);
            ruleEString();

            state._fsp--;

             after(grammarAccess.getAbstractTypeAccess().getNameEStringParserRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__NameAssignment_2"


    // $ANTLR start "rule__AbstractType__ValueAssignment_3_1"
    // InternalOperation.g:3989:1: rule__AbstractType__ValueAssignment_3_1 : ( ruleAbstractObjectValue ) ;
    public final void rule__AbstractType__ValueAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:3993:1: ( ( ruleAbstractObjectValue ) )
            // InternalOperation.g:3994:2: ( ruleAbstractObjectValue )
            {
            // InternalOperation.g:3994:2: ( ruleAbstractObjectValue )
            // InternalOperation.g:3995:3: ruleAbstractObjectValue
            {
             before(grammarAccess.getAbstractTypeAccess().getValueAbstractObjectValueParserRuleCall_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleAbstractObjectValue();

            state._fsp--;

             after(grammarAccess.getAbstractTypeAccess().getValueAbstractObjectValueParserRuleCall_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractType__ValueAssignment_3_1"


    // $ANTLR start "rule__PrimitiveValue__IntValueAssignment_0_1"
    // InternalOperation.g:4004:1: rule__PrimitiveValue__IntValueAssignment_0_1 : ( ruleEInt ) ;
    public final void rule__PrimitiveValue__IntValueAssignment_0_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4008:1: ( ( ruleEInt ) )
            // InternalOperation.g:4009:2: ( ruleEInt )
            {
            // InternalOperation.g:4009:2: ( ruleEInt )
            // InternalOperation.g:4010:3: ruleEInt
            {
             before(grammarAccess.getPrimitiveValueAccess().getIntValueEIntParserRuleCall_0_1_0()); 
            pushFollow(FOLLOW_2);
            ruleEInt();

            state._fsp--;

             after(grammarAccess.getPrimitiveValueAccess().getIntValueEIntParserRuleCall_0_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__IntValueAssignment_0_1"


    // $ANTLR start "rule__PrimitiveValue__FloatValueAssignment_1_1"
    // InternalOperation.g:4019:1: rule__PrimitiveValue__FloatValueAssignment_1_1 : ( ruleEFloat ) ;
    public final void rule__PrimitiveValue__FloatValueAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4023:1: ( ( ruleEFloat ) )
            // InternalOperation.g:4024:2: ( ruleEFloat )
            {
            // InternalOperation.g:4024:2: ( ruleEFloat )
            // InternalOperation.g:4025:3: ruleEFloat
            {
             before(grammarAccess.getPrimitiveValueAccess().getFloatValueEFloatParserRuleCall_1_1_0()); 
            pushFollow(FOLLOW_2);
            ruleEFloat();

            state._fsp--;

             after(grammarAccess.getPrimitiveValueAccess().getFloatValueEFloatParserRuleCall_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__FloatValueAssignment_1_1"


    // $ANTLR start "rule__PrimitiveValue__StringValueAssignment_2_1"
    // InternalOperation.g:4034:1: rule__PrimitiveValue__StringValueAssignment_2_1 : ( RULE_STRING ) ;
    public final void rule__PrimitiveValue__StringValueAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4038:1: ( ( RULE_STRING ) )
            // InternalOperation.g:4039:2: ( RULE_STRING )
            {
            // InternalOperation.g:4039:2: ( RULE_STRING )
            // InternalOperation.g:4040:3: RULE_STRING
            {
             before(grammarAccess.getPrimitiveValueAccess().getStringValueSTRINGTerminalRuleCall_2_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getPrimitiveValueAccess().getStringValueSTRINGTerminalRuleCall_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__StringValueAssignment_2_1"


    // $ANTLR start "rule__PrimitiveValue__BoolValueAssignment_3_1"
    // InternalOperation.g:4049:1: rule__PrimitiveValue__BoolValueAssignment_3_1 : ( ruleEBoolean ) ;
    public final void rule__PrimitiveValue__BoolValueAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4053:1: ( ( ruleEBoolean ) )
            // InternalOperation.g:4054:2: ( ruleEBoolean )
            {
            // InternalOperation.g:4054:2: ( ruleEBoolean )
            // InternalOperation.g:4055:3: ruleEBoolean
            {
             before(grammarAccess.getPrimitiveValueAccess().getBoolValueEBooleanParserRuleCall_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleEBoolean();

            state._fsp--;

             after(grammarAccess.getPrimitiveValueAccess().getBoolValueEBooleanParserRuleCall_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__BoolValueAssignment_3_1"


    // $ANTLR start "rule__PrimitiveValue__DateValueAssignment_4_1"
    // InternalOperation.g:4064:1: rule__PrimitiveValue__DateValueAssignment_4_1 : ( ruleEDate ) ;
    public final void rule__PrimitiveValue__DateValueAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4068:1: ( ( ruleEDate ) )
            // InternalOperation.g:4069:2: ( ruleEDate )
            {
            // InternalOperation.g:4069:2: ( ruleEDate )
            // InternalOperation.g:4070:3: ruleEDate
            {
             before(grammarAccess.getPrimitiveValueAccess().getDateValueEDateParserRuleCall_4_1_0()); 
            pushFollow(FOLLOW_2);
            ruleEDate();

            state._fsp--;

             after(grammarAccess.getPrimitiveValueAccess().getDateValueEDateParserRuleCall_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PrimitiveValue__DateValueAssignment_4_1"


    // $ANTLR start "rule__AbstractObjectValue__AbstractValueAssignment_1"
    // InternalOperation.g:4079:1: rule__AbstractObjectValue__AbstractValueAssignment_1 : ( RULE_ID ) ;
    public final void rule__AbstractObjectValue__AbstractValueAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4083:1: ( ( RULE_ID ) )
            // InternalOperation.g:4084:2: ( RULE_ID )
            {
            // InternalOperation.g:4084:2: ( RULE_ID )
            // InternalOperation.g:4085:3: RULE_ID
            {
             before(grammarAccess.getAbstractObjectValueAccess().getAbstractValueIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getAbstractObjectValueAccess().getAbstractValueIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AbstractObjectValue__AbstractValueAssignment_1"


    // $ANTLR start "rule__ArrayValues__ValuesAssignment_2_0"
    // InternalOperation.g:4094:1: rule__ArrayValues__ValuesAssignment_2_0 : ( rulePrimitiveValue ) ;
    public final void rule__ArrayValues__ValuesAssignment_2_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4098:1: ( ( rulePrimitiveValue ) )
            // InternalOperation.g:4099:2: ( rulePrimitiveValue )
            {
            // InternalOperation.g:4099:2: ( rulePrimitiveValue )
            // InternalOperation.g:4100:3: rulePrimitiveValue
            {
             before(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_0_0()); 
            pushFollow(FOLLOW_2);
            rulePrimitiveValue();

            state._fsp--;

             after(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__ValuesAssignment_2_0"


    // $ANTLR start "rule__ArrayValues__ValuesAssignment_2_1_1"
    // InternalOperation.g:4109:1: rule__ArrayValues__ValuesAssignment_2_1_1 : ( rulePrimitiveValue ) ;
    public final void rule__ArrayValues__ValuesAssignment_2_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4113:1: ( ( rulePrimitiveValue ) )
            // InternalOperation.g:4114:2: ( rulePrimitiveValue )
            {
            // InternalOperation.g:4114:2: ( rulePrimitiveValue )
            // InternalOperation.g:4115:3: rulePrimitiveValue
            {
             before(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_1_1_0()); 
            pushFollow(FOLLOW_2);
            rulePrimitiveValue();

            state._fsp--;

             after(grammarAccess.getArrayValuesAccess().getValuesPrimitiveValueParserRuleCall_2_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayValues__ValuesAssignment_2_1_1"


    // $ANTLR start "rule__ArrayType__PrimitiveTypeAssignment_1_0"
    // InternalOperation.g:4124:1: rule__ArrayType__PrimitiveTypeAssignment_1_0 : ( rulePrimitiveValueType ) ;
    public final void rule__ArrayType__PrimitiveTypeAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4128:1: ( ( rulePrimitiveValueType ) )
            // InternalOperation.g:4129:2: ( rulePrimitiveValueType )
            {
            // InternalOperation.g:4129:2: ( rulePrimitiveValueType )
            // InternalOperation.g:4130:3: rulePrimitiveValueType
            {
             before(grammarAccess.getArrayTypeAccess().getPrimitiveTypePrimitiveValueTypeEnumRuleCall_1_0_0()); 
            pushFollow(FOLLOW_2);
            rulePrimitiveValueType();

            state._fsp--;

             after(grammarAccess.getArrayTypeAccess().getPrimitiveTypePrimitiveValueTypeEnumRuleCall_1_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__PrimitiveTypeAssignment_1_0"


    // $ANTLR start "rule__ArrayType__DataModelTypeAssignment_1_1"
    // InternalOperation.g:4139:1: rule__ArrayType__DataModelTypeAssignment_1_1 : ( ( ruleQualifiedName ) ) ;
    public final void rule__ArrayType__DataModelTypeAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4143:1: ( ( ( ruleQualifiedName ) ) )
            // InternalOperation.g:4144:2: ( ( ruleQualifiedName ) )
            {
            // InternalOperation.g:4144:2: ( ( ruleQualifiedName ) )
            // InternalOperation.g:4145:3: ( ruleQualifiedName )
            {
             before(grammarAccess.getArrayTypeAccess().getDataModelTypeDataModelCrossReference_1_1_0()); 
            // InternalOperation.g:4146:3: ( ruleQualifiedName )
            // InternalOperation.g:4147:4: ruleQualifiedName
            {
             before(grammarAccess.getArrayTypeAccess().getDataModelTypeDataModelQualifiedNameParserRuleCall_1_1_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedName();

            state._fsp--;

             after(grammarAccess.getArrayTypeAccess().getDataModelTypeDataModelQualifiedNameParserRuleCall_1_1_0_1()); 

            }

             after(grammarAccess.getArrayTypeAccess().getDataModelTypeDataModelCrossReference_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__DataModelTypeAssignment_1_1"


    // $ANTLR start "rule__ArrayType__NameAssignment_4"
    // InternalOperation.g:4158:1: rule__ArrayType__NameAssignment_4 : ( ruleEString ) ;
    public final void rule__ArrayType__NameAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4162:1: ( ( ruleEString ) )
            // InternalOperation.g:4163:2: ( ruleEString )
            {
            // InternalOperation.g:4163:2: ( ruleEString )
            // InternalOperation.g:4164:3: ruleEString
            {
             before(grammarAccess.getArrayTypeAccess().getNameEStringParserRuleCall_4_0()); 
            pushFollow(FOLLOW_2);
            ruleEString();

            state._fsp--;

             after(grammarAccess.getArrayTypeAccess().getNameEStringParserRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__NameAssignment_4"


    // $ANTLR start "rule__ArrayType__ValuesAssignment_5_2_0"
    // InternalOperation.g:4173:1: rule__ArrayType__ValuesAssignment_5_2_0 : ( rulePrimitiveValue ) ;
    public final void rule__ArrayType__ValuesAssignment_5_2_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4177:1: ( ( rulePrimitiveValue ) )
            // InternalOperation.g:4178:2: ( rulePrimitiveValue )
            {
            // InternalOperation.g:4178:2: ( rulePrimitiveValue )
            // InternalOperation.g:4179:3: rulePrimitiveValue
            {
             before(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_0_0()); 
            pushFollow(FOLLOW_2);
            rulePrimitiveValue();

            state._fsp--;

             after(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__ValuesAssignment_5_2_0"


    // $ANTLR start "rule__ArrayType__ValuesAssignment_5_2_1_1"
    // InternalOperation.g:4188:1: rule__ArrayType__ValuesAssignment_5_2_1_1 : ( rulePrimitiveValue ) ;
    public final void rule__ArrayType__ValuesAssignment_5_2_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalOperation.g:4192:1: ( ( rulePrimitiveValue ) )
            // InternalOperation.g:4193:2: ( rulePrimitiveValue )
            {
            // InternalOperation.g:4193:2: ( rulePrimitiveValue )
            // InternalOperation.g:4194:3: rulePrimitiveValue
            {
             before(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_1_1_0()); 
            pushFollow(FOLLOW_2);
            rulePrimitiveValue();

            state._fsp--;

             after(grammarAccess.getArrayTypeAccess().getValuesPrimitiveValueParserRuleCall_5_2_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArrayType__ValuesAssignment_5_2_1_1"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    static final String dfa_1s = "\15\uffff";
    static final String dfa_2s = "\1\6\7\5\2\uffff\1\6\1\uffff\1\5";
    static final String dfa_3s = "\1\24\7\42\2\uffff\1\6\1\uffff\1\42";
    static final String dfa_4s = "\10\uffff\1\1\1\3\1\uffff\1\2\1\uffff";
    static final String dfa_5s = "\15\uffff}>";
    static final String[] dfa_6s = {
            "\1\7\10\uffff\1\1\1\2\1\3\1\4\1\5\1\6",
            "\2\10\33\uffff\1\11",
            "\2\10\33\uffff\1\11",
            "\2\10\33\uffff\1\11",
            "\2\10\33\uffff\1\11",
            "\2\10\33\uffff\1\11",
            "\2\10\33\uffff\1\11",
            "\2\13\31\uffff\1\12\1\uffff\1\11",
            "",
            "",
            "\1\14",
            "",
            "\2\13\31\uffff\1\12\1\uffff\1\11"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "543:1: rule__Parameter__Alternatives : ( ( ruleSimpleType ) | ( ruleAbstractType ) | ( ruleArrayType ) );";
        }
    }
    static final String dfa_7s = "\13\uffff";
    static final String dfa_8s = "\2\uffff\1\11\5\uffff\1\11\2\uffff";
    static final String dfa_9s = "\2\4\1\27\5\uffff\1\27\2\uffff";
    static final String dfa_10s = "\1\44\1\40\1\44\5\uffff\1\43\2\uffff";
    static final String dfa_11s = "\3\uffff\1\2\1\3\1\4\1\6\1\7\1\uffff\1\1\1\5";
    static final String dfa_12s = "\13\uffff}>";
    static final String[] dfa_13s = {
            "\1\2\1\4\1\7\4\uffff\2\5\23\uffff\1\3\1\uffff\1\6\1\uffff\1\1",
            "\1\10\33\uffff\1\3",
            "\1\11\1\uffff\2\11\5\uffff\1\3\2\uffff\1\11\1\12",
            "",
            "",
            "",
            "",
            "",
            "\1\11\1\uffff\2\11\5\uffff\1\3\2\uffff\1\11",
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

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_9;
            this.max = dfa_10;
            this.accept = dfa_11;
            this.special = dfa_12;
            this.transition = dfa_13;
        }
        public String getDescription() {
            return "570:1: rule__PrimitiveValue__Alternatives : ( ( ( rule__PrimitiveValue__Group_0__0 ) ) | ( ( rule__PrimitiveValue__Group_1__0 ) ) | ( ( rule__PrimitiveValue__Group_2__0 ) ) | ( ( rule__PrimitiveValue__Group_3__0 ) ) | ( ( rule__PrimitiveValue__Group_4__0 ) ) | ( ruleArrayValues ) | ( ruleAbstractObjectValue ) );";
        }
    }
 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x00000000009F8040L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x000000001A000000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x00000000001F8040L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x00000000001F8000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000001500001870L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000001000000010L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000001100000010L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000001D00001870L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x00000000A2000002L});

}
