package com.kide.enterprise.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kide.enterprise.audit.InMemoryAuditLedger;
import com.kide.enterprise.authorization.AuthorizationEnforcer;
import com.kide.enterprise.authorization.AuthorizationService;
import com.kide.enterprise.authorization.InMemoryAuthorizationPolicyStore;
import com.kide.enterprise.authorization.Role;
import com.kide.enterprise.authorization.RoleBinding;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationException;
import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.identity.PrincipalKind;
import com.kide.enterprise.modelrepo.FileModelRepository;
import com.kide.knowledge.EmbeddedKnowledgeRepository;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeProvenance;
import com.kide.knowledge.KnowledgeRepository;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTraceStore;
import com.kide.knowledge.KnowledgeTriple;
import com.kide.knowledge.KnowledgeVocabulary;
import com.kide.synthesis.SynthesisVocabulary;

public final class EnterpriseApiSelfCheckApplication implements IApplication {
    private volatile EnterpriseApiServer server;

    @Override
    public Object start(IApplicationContext applicationContext) {
        Path root = null;
        try {
            root = Files.createTempDirectory("kide-pr26-api-");
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "API Org", "API Portfolio", "API Project", "API Workspace");
            if (!provisioned.isReady()) throw new IllegalStateException(provisioned.summary());
            EnterpriseContext context = provisioned.context().orElseThrow();

            PrincipalIdentity principal = new PrincipalIdentity(
                    "selfcheck:api-engineer",
                    "API Engineer",
                    PrincipalKind.LOCAL_OFFLINE,
                    AuthenticationMethod.LOCAL_OFFLINE,
                    "",
                    "api-engineer",
                    Map.of("offline", "true"));
            PrincipalIdentity reviewer = new PrincipalIdentity(
                    "selfcheck:api-reviewer",
                    "API Reviewer",
                    PrincipalKind.LOCAL_OFFLINE,
                    AuthenticationMethod.LOCAL_OFFLINE,
                    "",
                    "api-reviewer",
                    Map.of("offline", "true"));

            InMemoryAuthorizationPolicyStore policies =
                    new InMemoryAuthorizationPolicyStore(List.of(
                            RoleBinding.allow(
                                    principal.id(), Role.ENGINEER, context.project().id()),
                            RoleBinding.allow(
                                    reviewer.id(), Role.REVIEWER, context.project().id())));
            ServerAuthorizationGate authorization = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(new AuthorizationService(policies)));

            Files.writeString(project.resolve("device.mncspec"),
                    "Model Golden\n"
                    + "InterfaceDescription Device {\n"
                    + "  commands { Start[] }\n"
                    + "  events { Publish Ready[] }\n"
                    + "}\n");
            Files.writeString(project.resolve("observe.cap"),
                    "Capability Observe compatible component interface Device {\n"
                    + "  providesControlCapabilities {\n"
                    + "    fireable commands : Start\n"
                    + "    receivable events : Ready\n"
                    + "  }\n"
                    + "}\n");
            Files.writeString(project.resolve("workflow.activity"),
                    "ActivityDiagram GoldenWorkflow\n"
                    + "has activities {\n"
                    + "  Activity ObserveStep {\n"
                    + "    requireCapability : Observe { Start, Ready }\n"
                    + "    nextActivity : ObserveStep\n"
                    + "  }\n"
                    + "}\n");
            Files.writeString(project.resolve("bindings.krl"),
                    "knowledge ApiBindings {\n"
                    + "  namespace kide = \"https://kide.dev/ontology/v1#\";\n"
                    + "  query FindObserve(capability: iri) {\n"
                    + "    match ?device kide:providesCapability ?capability;\n"
                    + "    select ?device;\n"
                    + "  }\n"
                    + "  template Binding(name: string, resource: iri) for java\n"
                    + "    body \"public final class ${name} { public static final String RESOURCE = \\\"${resource}\\\"; private ${name}() {} }\";\n"
                    + "  target Observe type java {\n"
                    + "    template Binding;\n"
                    + "    output \"generated/ApiObserveBinding.java\";\n"
                    + "    bind name: string = string \"ApiObserveBinding\";\n"
                    + "    bind resource: iri = query FindObserve(iri \"urn:kide:capability:Observe\").device;\n"
                    + "  }\n"
                    + "}\n");

            InMemoryAuditLedger audit = new InMemoryAuditLedger(Clock.systemUTC());
            FileModelRepository modelRepository = new FileModelRepository(project);
            ProjectCollaborationService collaboration =
                    new ProjectCollaborationService(project, modelRepository, Clock.systemUTC());
            EmbeddedKnowledgeRepository knowledgeRepository =
                    new EmbeddedKnowledgeRepository(project);
            knowledgeRepository.replace(
                    new KnowledgeDataset(
                            KnowledgeDataset.CURRENT_SCHEMA,
                            "api-selfcheck",
                            "PROJECT",
                            context.project().id().value(),
                            new KnowledgeProvenance(
                                    "urn:kide:selfcheck:catalogue",
                                    "KIDE",
                                    principal.id(),
                                    Clock.systemUTC().millis(),
                                    "INTERNAL"),
                            List.of(
                                    new KnowledgeTriple(
                                            "urn:kide:capability:Observe",
                                            KnowledgeVocabulary.RDF_TYPE,
                                            KnowledgeTerm.iri(KnowledgeVocabulary.CAPABILITY)),
                                    new KnowledgeTriple(
                                            "urn:kide:capability:Observe",
                                            KnowledgeVocabulary.LABEL,
                                            KnowledgeTerm.literal("Observe")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            KnowledgeVocabulary.RDF_TYPE,
                                            KnowledgeTerm.iri(KnowledgeVocabulary.DEVICE)),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            KnowledgeVocabulary.LABEL,
                                            KnowledgeTerm.literal("Camera")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            SynthesisVocabulary.PROVIDES_CAPABILITY,
                                            KnowledgeTerm.iri("urn:kide:capability:Observe")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            SynthesisVocabulary.PROVIDES_INTERFACE,
                                            KnowledgeTerm.iri("urn:kide:interface:Device")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            SynthesisVocabulary.PRIORITY,
                                            KnowledgeTerm.literal("10")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            KnowledgeVocabulary.RDF_TYPE,
                                            KnowledgeTerm.iri(KnowledgeVocabulary.DEVICE)),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            KnowledgeVocabulary.LABEL,
                                            KnowledgeTerm.literal("Camera B")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            SynthesisVocabulary.PROVIDES_CAPABILITY,
                                            KnowledgeTerm.iri("urn:kide:capability:Observe")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            SynthesisVocabulary.PROVIDES_INTERFACE,
                                            KnowledgeTerm.iri("urn:kide:interface:Device")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            SynthesisVocabulary.PRIORITY,
                                            KnowledgeTerm.literal("5")))),
                    KnowledgeRepository.MISSING_ETAG);
            ProjectKnowledgeService knowledge = new ProjectKnowledgeService(
                    knowledgeRepository,
                    new KnowledgeTraceStore(project, Clock.systemUTC()),
                    modelRepository);
            ProjectSynthesisService synthesis = new ProjectSynthesisService(
                    project, modelRepository, knowledgeRepository);
            ProjectGenerationService generation = new ProjectGenerationService(
                    project, modelRepository, knowledgeRepository, synthesis);
            server = new EnterpriseApiServer(
                    new EnterpriseApiConfig(
                            "127.0.0.1", 0, 1024 * 1024, Duration.ofSeconds(20),
                            false, false, Set.of(), Set.of("https://web.example.test")),
                    header -> {
                        if ("Bearer pr26-self-check".equals(header)) {
                            return new AuthenticatedSession(principal, null, Clock.systemUTC());
                        }
                        if ("Bearer pr33-reviewer".equals(header)) {
                            return new AuthenticatedSession(reviewer, null, Clock.systemUTC());
                        }
                        throw new AuthenticationException("Bearer authentication is invalid");
                    },
                    context,
                    authorization,
                    modelRepository,
                    collaboration,
                    knowledge,
                    synthesis,
                    generation,
                    audit,
                    Clock.systemUTC());
            server.start();

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5)).build();
            URI base = URI.create("http://127.0.0.1:" + server.localPort());

            HttpResponse<String> health = send(client, base.resolve("/api/v1/health"), "GET", null, null);
            requireStatus(health, 200);
            if (!"UP".equals(json(health).get("status").getAsString())) {
                throw new AssertionError("health status is not UP");
            }

            HttpResponse<String> denied = send(
                    client,
                    base.resolve("/api/v1/projects/" + context.project().id().value()),
                    "GET", null, null);
            requireStatus(denied, 401);

            HttpResponse<String> projectResponse = send(
                    client,
                    base.resolve("/api/v1/projects/" + context.project().id().value()),
                    "GET", "Bearer pr26-self-check", null);
            requireStatus(projectResponse, 200);
            JsonObject projectJson = json(projectResponse);
            if (!context.project().id().value().equals(projectJson.get("id").getAsString())) {
                throw new AssertionError("project identity mismatch");
            }
            if (!context.workspace().id().value().equals(projectJson.get("workspaceId").getAsString())) {
                throw new AssertionError("workspace identity missing from browser project contract");
            }

            String modelUri = "/api/v1/projects/" + context.project().id().value()
                    + "/models/selfcheck.dml";
            JsonObject create = new JsonObject();
            create.addProperty("content", "domain SelfCheck");
            create.addProperty("expectedRevision", "0".repeat(64));
            create.addProperty("mediaType", "text/x-kide-dml");
            HttpResponse<String> written = send(
                    client, base.resolve(modelUri), "PUT", "Bearer pr26-self-check", create.toString());
            requireStatus(written, 200);
            String etag = json(written).get("etag").getAsString();

            JsonObject stale = new JsonObject();
            stale.addProperty("content", "domain Stale");
            stale.addProperty("expectedRevision", "0".repeat(64));
            HttpResponse<String> conflict = send(
                    client, base.resolve(modelUri), "PUT", "Bearer pr26-self-check", stale.toString());
            requireStatus(conflict, 409);

            HttpResponse<String> read = send(
                    client, base.resolve(modelUri), "GET", "Bearer pr26-self-check", null);
            requireStatus(read, 200);
            JsonObject readJson = json(read);
            if (!etag.equals(readJson.get("etag").getAsString())
                    || !"domain SelfCheck".equals(readJson.get("content").getAsString())) {
                throw new AssertionError("model round-trip mismatch");
            }

            String projectApi = "/api/v1/projects/" + context.project().id().value();
            String presenceUri = projectApi + "/collaboration/sessions";

            JsonObject joinEngineer = new JsonObject();
            joinEngineer.addProperty("modelId", "selfcheck.dml");
            HttpResponse<String> engineerPresence = send(
                    client, base.resolve(presenceUri), "POST",
                    "Bearer pr26-self-check", joinEngineer.toString());
            requireStatus(engineerPresence, 200);
            String engineerSessionId = json(engineerPresence).get("id").getAsString();

            HttpResponse<String> reviewerPresence = send(
                    client, base.resolve(presenceUri), "POST",
                    "Bearer pr33-reviewer", "{}");
            requireStatus(reviewerPresence, 200);

            HttpResponse<String> presenceList = send(
                    client, base.resolve(presenceUri), "GET",
                    "Bearer pr26-self-check", null);
            requireStatus(presenceList, 200);
            if (json(presenceList).getAsJsonArray("items").size() != 2) {
                throw new AssertionError("two-user presence was not visible");
            }

            HttpResponse<String> departed = send(
                    client, base.resolve(presenceUri + "/" + engineerSessionId),
                    "DELETE", "Bearer pr26-self-check", null);
            requireStatus(departed, 200);

            JsonObject rejoin = new JsonObject();
            rejoin.addProperty("sessionId", engineerSessionId);
            rejoin.addProperty("modelId", "selfcheck.dml");
            HttpResponse<String> rejoined = send(
                    client, base.resolve(presenceUri), "POST",
                    "Bearer pr26-self-check", rejoin.toString());
            requireStatus(rejoined, 200);
            if (!engineerSessionId.equals(json(rejoined).get("id").getAsString())) {
                throw new AssertionError("presence session did not rejoin deterministically");
            }

            JsonObject concurrentWrite = new JsonObject();
            concurrentWrite.addProperty("content", "domain Concurrent");
            concurrentWrite.addProperty("expectedRevision", etag);
            concurrentWrite.addProperty("mediaType", "text/x-kide-dml");
            HttpResponse<String> concurrent = send(
                    client, base.resolve(modelUri), "PUT",
                    "Bearer pr26-self-check", concurrentWrite.toString());
            requireStatus(concurrent, 200);
            String concurrentEtag = json(concurrent).get("etag").getAsString();

            String reviewsUri = projectApi + "/reviews/changesets";
            JsonObject proposal = new JsonObject();
            proposal.addProperty("modelId", "selfcheck.dml");
            proposal.addProperty("baseEtag", etag);
            proposal.addProperty("proposedContent", "domain Proposed");
            proposal.addProperty("mediaType", "text/x-kide-dml");
            HttpResponse<String> proposed = send(
                    client, base.resolve(reviewsUri), "POST",
                    "Bearer pr26-self-check", proposal.toString());
            requireStatus(proposed, 200);
            JsonObject proposedJson = json(proposed);
            if (!"CONFLICT".equals(proposedJson.get("status").getAsString())) {
                throw new AssertionError("stale proposal was not classified as conflict");
            }
            String changeSetId = proposedJson.get("id").getAsString();

            HttpResponse<String> reviewerCannotAuthor = send(
                    client, base.resolve(reviewsUri), "POST",
                    "Bearer pr33-reviewer", proposal.toString());
            requireStatus(reviewerCannotAuthor, 403);

            HttpResponse<String> review = send(
                    client, base.resolve(reviewsUri + "/" + changeSetId), "GET",
                    "Bearer pr33-reviewer", null);
            requireStatus(review, 200);
            JsonObject reviewJson = json(review);
            if (!reviewJson.get("conflicted").getAsBoolean()
                    || !"domain Concurrent".equals(
                            reviewJson.getAsJsonObject("currentModel")
                                    .get("content").getAsString())) {
                throw new AssertionError("conflict review did not expose the current server revision");
            }

            JsonObject rebased = new JsonObject();
            rebased.addProperty("expectedCurrentEtag", concurrentEtag);
            rebased.addProperty("proposedContent", "domain Merged");
            HttpResponse<String> rebasedResponse = send(
                    client, base.resolve(reviewsUri + "/" + changeSetId), "PUT",
                    "Bearer pr26-self-check", rebased.toString());
            requireStatus(rebasedResponse, 200);
            if (!"DRAFT".equals(json(rebasedResponse).get("status").getAsString())) {
                throw new AssertionError("explicit conflict rebase did not return to draft");
            }

            HttpResponse<String> ready = send(
                    client, base.resolve(reviewsUri + "/" + changeSetId + "/ready"), "POST",
                    "Bearer pr26-self-check", null);
            requireStatus(ready, 200);
            if (!"READY".equals(json(ready).get("status").getAsString())) {
                throw new AssertionError("change set was not review-ready");
            }

            JsonObject commentBody = new JsonObject();
            commentBody.addProperty("body", "Conflict resolution checked.");
            commentBody.addProperty("anchor", "selfcheck.dml:1");
            HttpResponse<String> comment = send(
                    client, base.resolve(reviewsUri + "/" + changeSetId + "/comments"), "POST",
                    "Bearer pr33-reviewer", commentBody.toString());
            requireStatus(comment, 200);
            String commentId = json(comment).get("id").getAsString();

            HttpResponse<String> blockedApproval = send(
                    client, base.resolve(reviewsUri + "/" + changeSetId + "/approve"), "POST",
                    "Bearer pr33-reviewer", null);
            requireStatus(blockedApproval, 400);

            JsonObject resolve = new JsonObject();
            resolve.addProperty("resolved", true);
            HttpResponse<String> resolved = send(
                    client,
                    base.resolve(reviewsUri + "/" + changeSetId + "/comments/" + commentId),
                    "PUT", "Bearer pr33-reviewer", resolve.toString());
            requireStatus(resolved, 200);

            HttpResponse<String> authorCannotApprove = send(
                    client, base.resolve(reviewsUri + "/" + changeSetId + "/approve"), "POST",
                    "Bearer pr26-self-check", null);
            requireStatus(authorCannotApprove, 403);

            HttpResponse<String> approved = send(
                    client, base.resolve(reviewsUri + "/" + changeSetId + "/approve"), "POST",
                    "Bearer pr33-reviewer", null);
            requireStatus(approved, 200);
            if (!"APPROVED".equals(json(approved).get("status").getAsString())) {
                throw new AssertionError("independent review approval failed");
            }

            HttpResponse<String> applied = send(
                    client, base.resolve(reviewsUri + "/" + changeSetId + "/apply"), "POST",
                    "Bearer pr26-self-check", null);
            requireStatus(applied, 200);
            if (!"APPLIED".equals(
                    json(applied).getAsJsonObject("changeSet").get("status").getAsString())) {
                throw new AssertionError("approved change set was not applied");
            }

            HttpResponse<String> mergedRead = send(
                    client, base.resolve(modelUri), "GET",
                    "Bearer pr26-self-check", null);
            requireStatus(mergedRead, 200);
            if (!"domain Merged".equals(json(mergedRead).get("content").getAsString())) {
                throw new AssertionError("review apply did not update the canonical model");
            }

            ProjectCollaborationService reloadedCollaboration =
                    new ProjectCollaborationService(project, modelRepository, Clock.systemUTC());
            if (reloadedCollaboration.listChangeSets().stream().noneMatch(
                    set -> set.id().equals(changeSetId)
                            && set.status()
                                    == ProjectCollaborationService.ChangeSetStatus.APPLIED)) {
                throw new AssertionError("review state did not survive service rejoin");
            }

            String knowledgeBase = projectApi + "/knowledge";
            JsonObject knowledgeQuery = new JsonObject();
            knowledgeQuery.addProperty("query", "Observe");
            knowledgeQuery.addProperty("typeIri", "CAPABILITY");
            knowledgeQuery.addProperty("limit", 10);
            HttpResponse<String> knowledgeResult = send(
                    client, base.resolve(knowledgeBase + "/query"), "POST",
                    "Bearer pr26-self-check", knowledgeQuery.toString());
            requireStatus(knowledgeResult, 200);
            JsonObject knowledgeJson = json(knowledgeResult);
            if (knowledgeJson.getAsJsonArray("items").size() != 1
                    || !"Observe".equals(
                            knowledgeJson.getAsJsonArray("items")
                                    .get(0).getAsJsonObject().get("label").getAsString())) {
                throw new AssertionError("knowledge catalogue query did not return Observe");
            }

            HttpResponse<String> reviewerKnowledge = send(
                    client, base.resolve(knowledgeBase + "/query"), "POST",
                    "Bearer pr33-reviewer", knowledgeQuery.toString());
            requireStatus(reviewerKnowledge, 200);

            HttpResponse<String> emptyTraces = send(
                    client, base.resolve(knowledgeBase + "/traces"), "GET",
                    "Bearer pr26-self-check", null);
            requireStatus(emptyTraces, 200);
            String emptyTraceEtag = json(emptyTraces).get("etag").getAsString();

            JsonObject traceCreate = new JsonObject();
            traceCreate.addProperty("knowledgeIri", "urn:kide:capability:Observe");
            traceCreate.addProperty("modelId", "selfcheck.dml");
            traceCreate.addProperty("semanticId", "domain:SelfCheck");
            traceCreate.addProperty("relation", "REALIZES");
            traceCreate.addProperty("expectedTraceEtag", emptyTraceEtag);

            HttpResponse<String> reviewerCannotTrace = send(
                    client, base.resolve(knowledgeBase + "/traces"), "POST",
                    "Bearer pr33-reviewer", traceCreate.toString());
            requireStatus(reviewerCannotTrace, 403);

            HttpResponse<String> traceCreated = send(
                    client, base.resolve(knowledgeBase + "/traces"), "POST",
                    "Bearer pr26-self-check", traceCreate.toString());
            requireStatus(traceCreated, 200);
            JsonObject traceState = json(traceCreated);
            if (traceState.getAsJsonArray("links").size() != 1) {
                throw new AssertionError("knowledge trace was not persisted");
            }
            String traceId = traceState.getAsJsonArray("links")
                    .get(0).getAsJsonObject().get("id").getAsString();
            String traceEtag = traceState.get("etag").getAsString();

            HttpResponse<String> staleTrace = send(
                    client, base.resolve(knowledgeBase + "/traces"), "POST",
                    "Bearer pr26-self-check", traceCreate.toString());
            requireStatus(staleTrace, 409);

            JsonObject impactQuery = new JsonObject();
            impactQuery.addProperty("knowledgeIri", "urn:kide:capability:Observe");
            HttpResponse<String> impact = send(
                    client, base.resolve(knowledgeBase + "/impact"), "POST",
                    "Bearer pr26-self-check", impactQuery.toString());
            requireStatus(impact, 200);
            if (json(impact).getAsJsonArray("items").size() != 1) {
                throw new AssertionError("knowledge impact query did not return the trace");
            }

            JsonObject rebindTrace = new JsonObject();
            rebindTrace.addProperty("modelId", "selfcheck.dml");
            rebindTrace.addProperty("semanticId", "domain:Merged");
            rebindTrace.addProperty("expectedTraceEtag", traceEtag);
            HttpResponse<String> rebound = send(
                    client, base.resolve(knowledgeBase + "/traces/" + traceId), "PUT",
                    "Bearer pr26-self-check", rebindTrace.toString());
            requireStatus(rebound, 200);
            if (!traceId.equals(
                    json(rebound).getAsJsonArray("links")
                            .get(0).getAsJsonObject().get("id").getAsString())) {
                throw new AssertionError("trace identity changed during rebind");
            }

            HttpResponse<String> synthesisSource = send(
                    client, base.resolve(projectApi + "/models/workflow.activity"),
                    "GET", "Bearer pr26-self-check", null);
            requireStatus(synthesisSource, 200);
            String synthesisEtag = json(synthesisSource).get("etag").getAsString();
            String synthesisSourceBefore = Files.readString(project.resolve("workflow.activity"));

            JsonObject synthesisRequest = new JsonObject();
            synthesisRequest.addProperty("modelId", "workflow.activity");
            synthesisRequest.addProperty("modelRevision", synthesisEtag);
            HttpResponse<String> synthesisResponse = send(
                    client, base.resolve(projectApi + "/synthesis"), "POST",
                    "Bearer pr26-self-check", synthesisRequest.toString());
            requireStatus(synthesisResponse, 200);
            JsonObject synthesisJson = json(synthesisResponse);
            if (!"SUCCESS".equals(synthesisJson.get("status").getAsString())
                    || synthesisJson.getAsJsonArray("selections").size() != 1
                    || !synthesisJson.get("generatedMnc").getAsString()
                            .contains("Model GoldenWorkflow")) {
                throw new AssertionError("deterministic synthesis API did not produce MNC output");
            }
            String synthesisFingerprint = synthesisJson.get("fingerprint").getAsString();

            HttpResponse<String> repeatedSynthesis = send(
                    client, base.resolve(projectApi + "/synthesis"), "POST",
                    "Bearer pr26-self-check", synthesisRequest.toString());
            requireStatus(repeatedSynthesis, 200);
            if (!synthesisFingerprint.equals(
                    json(repeatedSynthesis).get("fingerprint").getAsString())) {
                throw new AssertionError("synthesis result was not deterministic");
            }
            if (!synthesisSourceBefore.equals(
                    Files.readString(project.resolve("workflow.activity")))) {
                throw new AssertionError("synthesis mutated the canonical Activity source");
            }

            HttpResponse<String> krlSource = send(
                    client, base.resolve(projectApi + "/models/bindings.krl"),
                    "GET", "Bearer pr26-self-check", null);
            requireStatus(krlSource, 200);
            String krlEtag = json(krlSource).get("etag").getAsString();
            String krlSourceBefore = Files.readString(project.resolve("bindings.krl"));

            JsonObject generationRequest = new JsonObject();
            generationRequest.addProperty("sourceModelId", "workflow.activity");
            generationRequest.addProperty("sourceRevision", synthesisEtag);
            generationRequest.addProperty("krlModelId", "bindings.krl");
            generationRequest.addProperty("krlRevision", krlEtag);
            generationRequest.addProperty("synthesisFingerprint", synthesisFingerprint);

            HttpResponse<String> generated = send(
                    client, base.resolve(projectApi + "/generation"), "POST",
                    "Bearer pr26-self-check", generationRequest.toString());
            requireStatus(generated, 200);
            JsonObject generatedJson = json(generated);
            if (!synthesisFingerprint.equals(
                            generatedJson.get("synthesisFingerprint").getAsString())
                    || generatedJson.getAsJsonArray("artifacts").size() != 1
                    || !"generated/ApiObserveBinding.java".equals(
                            generatedJson.getAsJsonArray("artifacts").get(0)
                                    .getAsJsonObject().get("path").getAsString())
                    || !"java".equals(
                            generatedJson.getAsJsonArray("artifacts").get(0)
                                    .getAsJsonObject().get("targetId").getAsString())
                    || !generatedJson.get("manifestJson").getAsString()
                            .contains("\"schemaVersion\":\"1\"")) {
                throw new AssertionError("semantic generation API evidence is incomplete");
            }
            String generationFingerprint =
                    generatedJson.get("fingerprint").getAsString();

            HttpResponse<String> repeatedGeneration = send(
                    client, base.resolve(projectApi + "/generation"), "POST",
                    "Bearer pr26-self-check", generationRequest.toString());
            requireStatus(repeatedGeneration, 200);
            if (!generationFingerprint.equals(
                    json(repeatedGeneration).get("fingerprint").getAsString())) {
                throw new AssertionError("semantic generation result was not deterministic");
            }
            if (!synthesisSourceBefore.equals(
                            Files.readString(project.resolve("workflow.activity")))
                    || !krlSourceBefore.equals(
                            Files.readString(project.resolve("bindings.krl")))) {
                throw new AssertionError("semantic generation mutated canonical source files");
            }

            JsonObject staleGeneration = generationRequest.deepCopy();
            staleGeneration.addProperty("krlRevision", "0".repeat(64));
            HttpResponse<String> staleGenerationResponse = send(
                    client, base.resolve(projectApi + "/generation"), "POST",
                    "Bearer pr26-self-check", staleGeneration.toString());
            requireStatus(staleGenerationResponse, 409);

            HttpResponse<String> reviewerCannotGenerate = send(
                    client, base.resolve(projectApi + "/generation"), "POST",
                    "Bearer pr33-reviewer", generationRequest.toString());
            requireStatus(reviewerCannotGenerate, 403);

            String currentKnowledgeEtag =
                    knowledgeRepository.snapshot().orElseThrow().etag();
            knowledgeRepository.replace(
                    new KnowledgeDataset(
                            KnowledgeDataset.CURRENT_SCHEMA,
                            "api-selfcheck-reconfigured",
                            "PROJECT",
                            context.project().id().value(),
                            new KnowledgeProvenance(
                                    "urn:kide:selfcheck:reconfiguration",
                                    "KIDE",
                                    principal.id(),
                                    Clock.systemUTC().millis(),
                                    "INTERNAL"),
                            List.of(
                                    new KnowledgeTriple(
                                            "urn:kide:capability:Observe",
                                            KnowledgeVocabulary.RDF_TYPE,
                                            KnowledgeTerm.iri(KnowledgeVocabulary.CAPABILITY)),
                                    new KnowledgeTriple(
                                            "urn:kide:capability:Observe",
                                            KnowledgeVocabulary.LABEL,
                                            KnowledgeTerm.literal("Observe")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            KnowledgeVocabulary.RDF_TYPE,
                                            KnowledgeTerm.iri(KnowledgeVocabulary.DEVICE)),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            SynthesisVocabulary.PROVIDES_CAPABILITY,
                                            KnowledgeTerm.iri("urn:kide:capability:Observe")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            SynthesisVocabulary.PROVIDES_INTERFACE,
                                            KnowledgeTerm.iri("urn:kide:interface:Device")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            SynthesisVocabulary.AVAILABLE,
                                            KnowledgeTerm.literal("false")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera",
                                            SynthesisVocabulary.PRIORITY,
                                            KnowledgeTerm.literal("10")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            KnowledgeVocabulary.RDF_TYPE,
                                            KnowledgeTerm.iri(KnowledgeVocabulary.DEVICE)),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            SynthesisVocabulary.PROVIDES_CAPABILITY,
                                            KnowledgeTerm.iri("urn:kide:capability:Observe")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            SynthesisVocabulary.PROVIDES_INTERFACE,
                                            KnowledgeTerm.iri("urn:kide:interface:Device")),
                                    new KnowledgeTriple(
                                            "urn:kide:device:camera-b",
                                            SynthesisVocabulary.PRIORITY,
                                            KnowledgeTerm.literal("5")))),
                    currentKnowledgeEtag);

            JsonObject reconfigurationRequest = new JsonObject();
            reconfigurationRequest.addProperty("modelId", "workflow.activity");
            reconfigurationRequest.addProperty("modelRevision", synthesisEtag);
            reconfigurationRequest.addProperty("cause", "RESOURCE_LOSS");
            com.google.gson.JsonArray previousBindings = new com.google.gson.JsonArray();
            for (var selection : synthesisJson.getAsJsonArray("selections")) {
                JsonObject value = selection.getAsJsonObject();
                JsonObject binding = new JsonObject();
                binding.addProperty("requirementId", value.get("requirementId").getAsString());
                binding.addProperty("resourceId", value.get("resourceId").getAsString());
                previousBindings.add(binding);
            }
            reconfigurationRequest.add("previousBindings", previousBindings);

            HttpResponse<String> reconfigured = send(
                    client, base.resolve(projectApi + "/reconfiguration"), "POST",
                    "Bearer pr26-self-check", reconfigurationRequest.toString());
            requireStatus(reconfigured, 200);
            JsonObject reconfiguredJson = json(reconfigured);
            if (!"RECONFIGURED".equals(reconfiguredJson.get("status").getAsString())
                    || reconfiguredJson.getAsJsonArray("selections").size() != 1
                    || !"urn:kide:device:camera-b".equals(
                            reconfiguredJson.getAsJsonArray("selections").get(0)
                                    .getAsJsonObject().get("resourceId").getAsString())
                    || !"MIGRATE".equals(
                            reconfiguredJson.getAsJsonArray("migrations").get(0)
                                    .getAsJsonObject().get("policy").getAsString())) {
                throw new AssertionError("device-loss reconfiguration did not migrate to standby");
            }
            String reconfigurationFingerprint =
                    reconfiguredJson.get("fingerprint").getAsString();

            HttpResponse<String> repeatedReconfiguration = send(
                    client, base.resolve(projectApi + "/reconfiguration"), "POST",
                    "Bearer pr26-self-check", reconfigurationRequest.toString());
            requireStatus(repeatedReconfiguration, 200);
            if (!reconfigurationFingerprint.equals(
                    json(repeatedReconfiguration).get("fingerprint").getAsString())) {
                throw new AssertionError("reconfiguration result was not deterministic");
            }
            if (!synthesisSourceBefore.equals(
                    Files.readString(project.resolve("workflow.activity")))) {
                throw new AssertionError("reconfiguration mutated the canonical Activity source");
            }

            HttpResponse<String> reviewerCannotReconfigure = send(
                    client, base.resolve(projectApi + "/reconfiguration"), "POST",
                    "Bearer pr33-reviewer", reconfigurationRequest.toString());
            requireStatus(reviewerCannotReconfigure, 403);

            JsonObject staleSynthesis = new JsonObject();
            staleSynthesis.addProperty("modelId", "workflow.activity");
            staleSynthesis.addProperty("modelRevision", "0".repeat(64));
            HttpResponse<String> staleSynthesisResponse = send(
                    client, base.resolve(projectApi + "/synthesis"), "POST",
                    "Bearer pr26-self-check", staleSynthesis.toString());
            requireStatus(staleSynthesisResponse, 409);

            HttpResponse<String> reviewerCannotSynthesize = send(
                    client, base.resolve(projectApi + "/synthesis"), "POST",
                    "Bearer pr33-reviewer", synthesisRequest.toString());
            requireStatus(reviewerCannotSynthesize, 403);

            if (!audit.verify() || audit.snapshot().size() < 20) {
                throw new AssertionError("audit evidence missing or invalid");
            }

            System.out.println("KIDE PR26 ENTERPRISE API SELF-CHECK OK");
            System.out.println("KIDE PR33 ENTERPRISE API COLLABORATION SELF-CHECK OK");
            System.out.println("KIDE PR35 ENTERPRISE KNOWLEDGE CATALOGUE SELF-CHECK OK");
            System.out.println("KIDE PR36 ENTERPRISE SYNTHESIS SELF-CHECK OK");
            System.out.println("KIDE PR37 ENTERPRISE RECONFIGURATION SELF-CHECK OK");
            System.out.println("KIDE PR38 ENTERPRISE GENERATION SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Throwable failure) {
            String detail = failure.getMessage();
            if (detail == null || detail.isBlank()) detail = "no detail";
            detail = detail.replace('\r', ' ').replace('\n', ' ');
            System.err.println("KIDE PR38 enterprise API self-check failed: "
                    + failure.getClass().getSimpleName() + " - " + detail);
            return Integer.valueOf(2);
        } finally {
            if (server != null) {
                try { server.close(); } catch (RuntimeException ignored) { }
            }
            deleteTree(root);
        }
    }

    @Override
    public void stop() {
        EnterpriseApiServer current = server;
        if (current != null) {
            try { current.close(); } catch (RuntimeException ignored) { }
        }
    }

    private static HttpResponse<String> send(
            HttpClient client, URI uri, String method, String authorization, String body)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(8))
                .header("Accept", "application/json")
                .header("Origin", "https://web.example.test");
        if (authorization != null) builder.header("Authorization", authorization);
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static JsonObject json(HttpResponse<String> response) {
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private static void requireStatus(HttpResponse<?> response, int expected) {
        if (response.statusCode() != expected) {
            throw new AssertionError(
                    "expected HTTP " + expected + " but received " + response.statusCode());
        }
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) { }
            });
        } catch (IOException ignored) { }
    }
}
