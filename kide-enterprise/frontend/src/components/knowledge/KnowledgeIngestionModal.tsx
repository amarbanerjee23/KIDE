import React, { useState, useEffect } from 'react';
import {
  UploadCloud, FileText, CheckCircle2, AlertTriangle,
  Layers, Copy, Check, Download, X,
  RefreshCw, Sparkles, Bell, Table
} from 'lucide-react';
import { ingestionApi } from '../../api/ingestion';
import {
  IngestionJob,
  StagedKnowledgeArtifact,
  NotificationItem,
  SampleTemplate
} from '../../types/ingestion';
import { useEditorStore } from '../../stores/editorStore';
import { projectsApi } from '../../api/projects';

interface KnowledgeIngestionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onPromoted?: () => void;
}

export const KnowledgeIngestionModal: React.FC<KnowledgeIngestionModalProps> = ({
  isOpen,
  onClose,
  onPromoted
}) => {
  const { projectId } = useEditorStore();
  const [activeTab, setActiveTab] = useState<'ingest' | 'staged' | 'notifications'>('ingest');
  
  // Ingest tab state
  const [templates, setTemplates] = useState<SampleTemplate[]>([]);
  const [jobs, setJobs] = useState<IngestionJob[]>([]);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isUploading, setIsUploading] = useState<boolean>(false);
  const [ingestingSampleId, setIngestingSampleId] = useState<string | null>(null);
  const [statusMessage, setStatusMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Staged tab state
  const [stagedArtifacts, setStagedArtifacts] = useState<StagedKnowledgeArtifact[]>([]);
  const [selectedArtifact, setSelectedArtifact] = useState<StagedKnowledgeArtifact | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [dslTab, setDslTab] = useState<'dml' | 'mnc' | 'cap' | 'op'>('dml');
  const [copiedDsl, setCopiedDsl] = useState<boolean>(false);
  const [isApproving, setIsApproving] = useState<boolean>(false);
  const [isImporting, setIsImporting] = useState<boolean>(false);
  const [reviewNotes, setReviewNotes] = useState<string>('');

  // Notifications tab state
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [isLoadingNotifs, setIsLoadingNotifs] = useState<boolean>(false);

  useEffect(() => {
    if (!isOpen) return;
    loadTemplates();
    loadJobs();
    loadStaged();
  }, [isOpen]);

  useEffect(() => {
    if (activeTab === 'notifications') {
      loadNotifications();
    } else if (activeTab === 'staged') {
      loadStaged();
    } else if (activeTab === 'ingest') {
      loadJobs();
    }
  }, [activeTab]);

  const loadTemplates = async () => {
    try {
      const data = await ingestionApi.getTemplates();
      setTemplates(data);
    } catch (err) {
      console.error('Failed to load sample templates:', err);
    }
  };

  const loadJobs = async () => {
    try {
      const data = await ingestionApi.getJobs();
      setJobs(data);
    } catch (err) {
      console.error('Failed to load ingestion jobs:', err);
    }
  };

  const loadStaged = async () => {
    try {
      const filter = statusFilter === 'all' ? undefined : statusFilter;
      const data = await ingestionApi.getAllStagedArtifacts(filter);
      setStagedArtifacts(data);
      if (data.length > 0 && !selectedArtifact) {
        setSelectedArtifact(data[0]);
      }
    } catch (err) {
      console.error('Failed to load staged artifacts:', err);
    }
  };

  const loadNotifications = async () => {
    setIsLoadingNotifs(true);
    try {
      const data = await ingestionApi.getNotifications();
      setNotifications(data);
    } catch (err) {
      console.error('Failed to load notifications:', err);
    } finally {
      setIsLoadingNotifs(false);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const handleUploadSubmit = async () => {
    if (!selectedFile) return;
    setIsUploading(true);
    setStatusMessage(null);
    try {
      const job = await ingestionApi.uploadDocument(selectedFile);
      setStatusMessage({ type: 'success', text: `Document '${job.filename}' processed successfully! Staged ${job.extracted_devices_count} device models.` });
      setSelectedFile(null);
      await loadJobs();
      await loadStaged();
      setActiveTab('staged');
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: err.message || 'Upload failed' });
    } finally {
      setIsUploading(false);
    }
  };

  const handleSampleIngest = async (sampleId: string) => {
    setIngestingSampleId(sampleId);
    setStatusMessage(null);
    try {
      const job = await ingestionApi.ingestSample(sampleId);
      setStatusMessage({ type: 'success', text: `Sample '${job.filename}' extracted and staged successfully!` });
      await loadJobs();
      await loadStaged();
      setActiveTab('staged');
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: err.message || 'Sample extraction failed' });
    } finally {
      setIngestingSampleId(null);
    }
  };

  const handleApproveArtifact = async (artifact: StagedKnowledgeArtifact) => {
    setIsApproving(true);
    try {
      const updated = await ingestionApi.approveArtifact(artifact.id, reviewNotes || undefined);
      setSelectedArtifact(updated);
      await loadStaged();
      setStatusMessage({ type: 'success', text: `Equipment '${updated.name}' approved and promoted to global Knowledge Hub!` });
      if (onPromoted) onPromoted();
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: err.message || 'Approval failed' });
    } finally {
      setIsApproving(false);
    }
  };

  const handleRejectArtifact = async (artifact: StagedKnowledgeArtifact) => {
    try {
      const updated = await ingestionApi.rejectArtifact(artifact.id, reviewNotes || undefined);
      setSelectedArtifact(updated);
      await loadStaged();
      setStatusMessage({ type: 'success', text: `Artifact '${updated.name}' marked as rejected.` });
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: err.message || 'Rejection failed' });
    }
  };

  const handleImportToProject = async (artifact: StagedKnowledgeArtifact) => {
    if (!projectId) {
      setStatusMessage({ type: 'error', text: 'No active project selected to import into.' });
      return;
    }
    setIsImporting(true);
    try {
      const res = await ingestionApi.importArtifactToProject(artifact.id, projectId);
      setStatusMessage({ type: 'success', text: res.message });
      // Reload project files
      const rawFiles = await projectsApi.listFiles(projectId);
      const mapped = rawFiles.map(f => ({
        id: String(f.id),
        name: f.filename || f.name || 'untitled',
        content: f.content,
        language: f.filename?.endsWith('.dml') ? 'dmldsl' : f.filename?.endsWith('.mnc') ? 'mncml' : 'plaintext'
      }));
      useEditorStore.getState().setFiles(mapped);
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: err.message || 'Import to project failed' });
    } finally {
      setIsImporting(false);
    }
  };

  const copyCurrentDsl = (content: string) => {
    navigator.clipboard.writeText(content);
    setCopiedDsl(true);
    setTimeout(() => setCopiedDsl(false), 2000);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="relative w-full max-w-6xl bg-slate-900 border border-slate-700 rounded-xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        
        {/* Header */}
        <div className="px-6 py-4 bg-slate-950 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-sky-500/10 border border-sky-500/30 rounded-lg text-sky-400">
              <UploadCloud className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-lg font-bold text-white tracking-tight">Enterprise Knowledge Ingestion Pipeline</h2>
                <span className="text-xs px-2 py-0.5 rounded bg-sky-500/20 text-sky-300 font-semibold border border-sky-500/30">
                  PhD Thesis Req 24 & 50
                </span>
              </div>
              <p className="text-xs text-slate-400">
                Automated datasheet extraction, register discovery, and Canonical KIDE DSL synthesis
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            {/* Tabs */}
            <div className="flex bg-slate-900 border border-slate-800 p-1 rounded-lg">
              <button
                onClick={() => setActiveTab('ingest')}
                className={`px-3 py-1.5 rounded-md text-xs font-medium flex items-center gap-1.5 transition-colors ${
                  activeTab === 'ingest' ? 'bg-sky-600 text-white shadow-sm' : 'text-slate-400 hover:text-white'
                }`}
              >
                <UploadCloud className="w-3.5 h-3.5" />
                Ingest Datasheet
              </button>
              <button
                onClick={() => setActiveTab('staged')}
                className={`px-3 py-1.5 rounded-md text-xs font-medium flex items-center gap-1.5 transition-colors ${
                  activeTab === 'staged' ? 'bg-sky-600 text-white shadow-sm' : 'text-slate-400 hover:text-white'
                }`}
              >
                <Layers className="w-3.5 h-3.5" />
                Staged Review ({stagedArtifacts.filter(a => a.status === 'draft').length})
              </button>
              <button
                onClick={() => setActiveTab('notifications')}
                className={`px-3 py-1.5 rounded-md text-xs font-medium flex items-center gap-1.5 transition-colors ${
                  activeTab === 'notifications' ? 'bg-sky-600 text-white shadow-sm' : 'text-slate-400 hover:text-white'
                }`}
              >
                <Bell className="w-3.5 h-3.5" />
                Notifications
              </button>
            </div>

            <button
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-white hover:bg-slate-800 rounded-lg transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Global status alert */}
        {statusMessage && (
          <div className={`px-6 py-2.5 text-xs flex items-center justify-between border-b ${
            statusMessage.type === 'success'
              ? 'bg-emerald-950/50 border-emerald-800/50 text-emerald-300'
              : 'bg-rose-950/50 border-rose-800/50 text-rose-300'
          }`}>
            <div className="flex items-center gap-2">
              {statusMessage.type === 'success' ? <CheckCircle2 className="w-4 h-4" /> : <AlertTriangle className="w-4 h-4" />}
              <span>{statusMessage.text}</span>
            </div>
            <button onClick={() => setStatusMessage(null)} className="text-slate-400 hover:text-white text-xs underline">
              Dismiss
            </button>
          </div>
        )}

        {/* Modal Body */}
        <div className="flex-1 overflow-y-auto p-6 bg-slate-900/60">
          
          {/* TAB 1: Ingest Datasheet */}
          {activeTab === 'ingest' && (
            <div className="space-y-6">
              {/* Upload Dropzone */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="md:col-span-2 border-2 border-dashed border-slate-700 hover:border-sky-500 rounded-xl p-8 bg-slate-950/40 text-center flex flex-col items-center justify-center transition-colors">
                  <div className="w-12 h-12 rounded-full bg-sky-500/10 border border-sky-500/20 text-sky-400 flex items-center justify-center mb-3">
                    <UploadCloud className="w-6 h-6" />
                  </div>
                  <h3 className="text-sm font-semibold text-white mb-1">
                    Upload Technical Datasheet, Manual, or Register Map
                  </h3>
                  <p className="text-xs text-slate-400 max-w-md mb-4">
                    Supports PDF, TXT, CSV register lists, and JSON device specifications. The engine automatically extracts electrical ratings, register tables, commands, alarms, and synthesizes KIDE DSL artifacts.
                  </p>

                  <div className="flex items-center gap-3">
                    <label className="cursor-pointer px-4 py-2 bg-slate-800 hover:bg-slate-700 border border-slate-600 rounded-lg text-xs font-medium text-white transition-colors">
                      Browse File
                      <input
                        type="file"
                        className="hidden"
                        accept=".pdf,.txt,.csv,.json,.yaml,.yml"
                        onChange={handleFileChange}
                      />
                    </label>

                    {selectedFile && (
                      <div className="flex items-center gap-2 bg-slate-800 px-3 py-1.5 rounded-lg border border-slate-700 text-xs text-slate-300">
                        <FileText className="w-3.5 h-3.5 text-sky-400" />
                        <span className="font-mono">{selectedFile.name}</span>
                        <span className="text-slate-500">({Math.round(selectedFile.size / 1024)} KB)</span>
                        <button
                          onClick={handleUploadSubmit}
                          disabled={isUploading}
                          className="ml-2 px-3 py-1 bg-sky-600 hover:bg-sky-500 text-white rounded font-medium flex items-center gap-1 transition-colors"
                        >
                          {isUploading ? <RefreshCw className="w-3.5 h-3.5 animate-spin" /> : <Sparkles className="w-3.5 h-3.5" />}
                          Run Extraction
                        </button>
                      </div>
                    )}
                  </div>
                </div>

                {/* Quick Info */}
                <div className="bg-slate-950/60 border border-slate-800 rounded-xl p-5 flex flex-col justify-between">
                  <div>
                    <h4 className="text-xs font-semibold uppercase tracking-wider text-slate-400 mb-3 flex items-center gap-2">
                      <Sparkles className="w-4 h-4 text-sky-400" />
                      Automated Pipeline Outputs
                    </h4>
                    <ul className="space-y-2 text-xs text-slate-300">
                      <li className="flex items-start gap-2">
                        <span className="w-1.5 h-1.5 rounded-full bg-sky-400 mt-1.5"></span>
                        <span><strong>DML DataModel:</strong> Parameter structures with engineering units & limits</span>
                      </li>
                      <li className="flex items-start gap-2">
                        <span className="w-1.5 h-1.5 rounded-full bg-cyan-400 mt-1.5"></span>
                        <span><strong>MNC-ML Interface:</strong> DataPoints, Commands, Responses & Alarms</span>
                      </li>
                      <li className="flex items-start gap-2">
                        <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 mt-1.5"></span>
                        <span><strong>Capability DSL:</strong> Control capabilities and outcomes</span>
                      </li>
                      <li className="flex items-start gap-2">
                        <span className="w-1.5 h-1.5 rounded-full bg-amber-400 mt-1.5"></span>
                        <span><strong>Operation DSL:</strong> Executable device invocation scripts</span>
                      </li>
                    </ul>
                  </div>

                  <div className="mt-4 pt-4 border-t border-slate-800 text-[11px] text-slate-400">
                    Engineered according to Section 8.1 & 8.2 of the doctoral thesis for continuous equipment discovery.
                  </div>
                </div>
              </div>

              {/* Pre-packaged Industrial Datasheets */}
              <div>
                <div className="flex items-center justify-between mb-3">
                  <div>
                    <h3 className="text-sm font-semibold text-white">Pre-Packaged Industrial Equipment Datasheets</h3>
                    <p className="text-xs text-slate-400">Test the ingestion pipeline immediately with realistic multi-domain hardware specifications</p>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  {templates.map((tpl) => (
                    <div
                      key={tpl.id}
                      className="bg-slate-950/80 border border-slate-800 hover:border-slate-700 rounded-xl p-4 flex flex-col justify-between transition-all"
                    >
                      <div>
                        <div className="flex items-center justify-between mb-2">
                          <span className="text-[10px] uppercase font-bold px-2 py-0.5 rounded bg-slate-800 text-sky-400 border border-slate-700">
                            {tpl.protocol}
                          </span>
                          <span className="text-[10px] text-slate-400">{tpl.manufacturer}</span>
                        </div>
                        <h4 className="text-xs font-bold text-white mb-1">{tpl.name}</h4>
                        <p className="text-[11px] text-slate-400 line-clamp-2 mb-3">{tpl.description}</p>
                      </div>

                      <button
                        onClick={() => handleSampleIngest(tpl.id)}
                        disabled={ingestingSampleId === tpl.id}
                        className="w-full py-1.5 bg-slate-800 hover:bg-sky-600 border border-slate-700 hover:border-sky-500 rounded-lg text-xs font-medium text-slate-200 hover:text-white flex items-center justify-center gap-1.5 transition-colors"
                      >
                        {ingestingSampleId === tpl.id ? (
                          <>
                            <RefreshCw className="w-3.5 h-3.5 animate-spin text-sky-400" />
                            <span>Ingesting...</span>
                          </>
                        ) : (
                          <>
                            <UploadCloud className="w-3.5 h-3.5" />
                            <span>Ingest Datasheet</span>
                          </>
                        )}
                      </button>
                    </div>
                  ))}
                </div>
              </div>

              {/* Recent Ingestion Jobs Table */}
              <div>
                <h3 className="text-sm font-semibold text-white mb-2">Recent Ingestion Jobs</h3>
                <div className="bg-slate-950 border border-slate-800 rounded-xl overflow-hidden">
                  <table className="w-full text-xs text-left">
                    <thead className="bg-slate-900 border-b border-slate-800 text-slate-400 uppercase text-[10px]">
                      <tr>
                        <th className="px-4 py-2.5">Document</th>
                        <th className="px-4 py-2.5">Type</th>
                        <th className="px-4 py-2.5">Status</th>
                        <th className="px-4 py-2.5">Progress</th>
                        <th className="px-4 py-2.5">Extracted Entities</th>
                        <th className="px-4 py-2.5">Created</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-800/60">
                      {jobs.length === 0 ? (
                        <tr>
                          <td colSpan={6} className="px-4 py-6 text-center text-slate-500">
                            No ingestion jobs executed yet. Upload a datasheet or select a sample above.
                          </td>
                        </tr>
                      ) : (
                        jobs.slice(0, 5).map((j) => (
                          <tr key={j.id} className="hover:bg-slate-900/40">
                            <td className="px-4 py-3 font-medium text-slate-200">{j.filename}</td>
                            <td className="px-4 py-3 uppercase text-[10px] text-slate-400">{j.file_type}</td>
                            <td className="px-4 py-3">
                              <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-semibold ${
                                j.status === 'completed' ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30' :
                                j.status === 'processing' ? 'bg-sky-500/10 text-sky-400 border border-sky-500/30' :
                                j.status === 'failed' ? 'bg-rose-500/10 text-rose-400 border border-rose-500/30' :
                                'bg-slate-800 text-slate-400'
                              }`}>
                                {j.status === 'completed' && <CheckCircle2 className="w-3 h-3" />}
                                {j.status === 'processing' && <RefreshCw className="w-3 h-3 animate-spin" />}
                                {j.status === 'failed' && <AlertTriangle className="w-3 h-3" />}
                                {j.status}
                              </span>
                            </td>
                            <td className="px-4 py-3">
                              <div className="w-24 bg-slate-800 rounded-full h-1.5 overflow-hidden">
                                <div
                                  className={`h-full ${j.status === 'failed' ? 'bg-rose-500' : 'bg-sky-500'}`}
                                  style={{ width: `${j.progress_pct}%` }}
                                />
                              </div>
                            </td>
                            <td className="px-4 py-3 text-slate-300">
                              {j.extracted_devices_count} Devices, {j.extracted_parameters_count} Registers
                            </td>
                            <td className="px-4 py-3 text-slate-400 text-[11px]">
                              {new Date(j.created_at).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>

            </div>
          )}

          {/* TAB 2: Staged Artifact Review Console */}
          {activeTab === 'staged' && (
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
              
              {/* Artifacts List (Left) */}
              <div className="lg:col-span-5 space-y-4">
                <div className="flex items-center justify-between">
                  <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                    Staged Artifacts ({stagedArtifacts.length})
                  </h3>
                  <select
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                    className="bg-slate-950 border border-slate-800 rounded-md text-xs px-2.5 py-1 text-slate-300"
                  >
                    <option value="all">All Statuses</option>
                    <option value="draft">Draft (Needs Review)</option>
                    <option value="approved">Approved & Promoted</option>
                    <option value="rejected">Rejected</option>
                  </select>
                </div>

                <div className="space-y-2 max-h-[600px] overflow-y-auto pr-1">
                  {stagedArtifacts.length === 0 ? (
                    <div className="text-center p-8 bg-slate-950/40 border border-slate-800 rounded-xl text-slate-500 text-xs">
                      No staged artifacts found matching current filter.
                    </div>
                  ) : (
                    stagedArtifacts.map((art) => (
                      <div
                        key={art.id}
                        onClick={() => setSelectedArtifact(art)}
                        className={`p-3.5 rounded-xl border cursor-pointer transition-all ${
                          selectedArtifact?.id === art.id
                            ? 'bg-sky-950/40 border-sky-500 shadow-md'
                            : 'bg-slate-950/60 border-slate-800 hover:border-slate-700'
                        }`}
                      >
                        <div className="flex items-center justify-between mb-1.5">
                          <span className="text-xs font-bold text-white truncate max-w-[200px]">
                            {art.name}
                          </span>
                          <span className={`text-[10px] px-2 py-0.5 rounded font-semibold ${
                            art.status === 'approved' ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30' :
                            art.status === 'draft' ? 'bg-amber-500/10 text-amber-400 border border-amber-500/30' :
                            'bg-rose-500/10 text-rose-400 border border-rose-500/30'
                          }`}>
                            {art.status.toUpperCase()}
                          </span>
                        </div>

                        <div className="flex items-center gap-3 text-[11px] text-slate-400 mb-2">
                          <span>{art.category}</span>
                          <span>•</span>
                          <span className="text-sky-400 font-mono">{art.protocol}</span>
                        </div>

                        <div className="flex items-center gap-2 text-[10px] text-slate-400">
                          <span className="px-1.5 py-0.5 rounded bg-slate-800">
                            {art.extracted_datapoints.length} Datapoints
                          </span>
                          <span className="px-1.5 py-0.5 rounded bg-slate-800">
                            {art.extracted_commands.length} Commands
                          </span>
                          <span className="px-1.5 py-0.5 rounded bg-slate-800">
                            {art.extracted_alarms.length} Alarms
                          </span>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>

              {/* Artifact Detail Inspector (Right) */}
              <div className="lg:col-span-7 bg-slate-950 border border-slate-800 rounded-xl p-5 flex flex-col">
                {selectedArtifact ? (
                  <div className="space-y-4 flex-1 flex flex-col">
                    {/* Header */}
                    <div className="flex items-start justify-between pb-3 border-b border-slate-800">
                      <div>
                        <div className="flex items-center gap-2 mb-1">
                          <h3 className="text-sm font-bold text-white">{selectedArtifact.name}</h3>
                          <span className={`text-[10px] px-2 py-0.5 rounded font-semibold ${
                            selectedArtifact.status === 'approved' ? 'bg-emerald-500/20 text-emerald-300' :
                            selectedArtifact.status === 'draft' ? 'bg-amber-500/20 text-amber-300' :
                            'bg-rose-500/20 text-rose-300'
                          }`}>
                            {selectedArtifact.status.toUpperCase()}
                          </span>
                        </div>
                        <p className="text-xs text-slate-400">
                          {selectedArtifact.category} • Protocol: {selectedArtifact.protocol}
                        </p>
                      </div>

                      {/* Action buttons */}
                      <div className="flex items-center gap-2">
                        {selectedArtifact.status === 'draft' && (
                          <>
                            <input
                              type="text"
                              placeholder="Review notes..."
                              value={reviewNotes}
                              onChange={(e) => setReviewNotes(e.target.value)}
                              className="px-2.5 py-1 text-xs bg-slate-900 border border-slate-700 rounded-lg text-slate-200 placeholder-slate-500 w-36 focus:outline-none focus:border-sky-500"
                            />
                            <button
                              onClick={() => handleApproveArtifact(selectedArtifact)}
                              disabled={isApproving}
                              className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-medium flex items-center gap-1.5 transition-colors shadow-sm"
                            >
                              <CheckCircle2 className="w-3.5 h-3.5" />
                              Approve & Promote
                            </button>
                            <button
                              onClick={() => handleRejectArtifact(selectedArtifact)}
                              className="px-3 py-1.5 bg-slate-800 hover:bg-rose-600 hover:text-white text-slate-300 rounded-lg text-xs font-medium transition-colors"
                            >
                              Reject
                            </button>
                          </>
                        )}
                        {projectId && (
                          <button
                            onClick={() => handleImportToProject(selectedArtifact)}
                            disabled={isImporting}
                            className="px-3 py-1.5 bg-sky-600 hover:bg-sky-500 text-white rounded-lg text-xs font-medium flex items-center gap-1.5 transition-colors shadow-sm"
                          >
                            <Download className="w-3.5 h-3.5" />
                            Import to Project
                          </button>
                        )}
                      </div>
                    </div>

                    {/* Metadata & Electrical Specs */}
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-3 text-xs bg-slate-900/80 p-3 rounded-lg border border-slate-800">
                      {Object.entries(selectedArtifact.device_metadata || {}).map(([key, val]) => (
                        <div key={key}>
                          <span className="text-[10px] text-slate-400 uppercase">{key.replace(/_/g, ' ')}:</span>
                          <div className="font-medium text-slate-200 truncate">{String(val)}</div>
                        </div>
                      ))}
                    </div>

                    {/* Datapoints / Registers Table */}
                    <div>
                      <h4 className="text-xs font-semibold text-slate-400 mb-1.5 flex items-center gap-1.5">
                        <Table className="w-3.5 h-3.5 text-sky-400" />
                        Discovered Registers & Measurement Signals ({selectedArtifact.extracted_datapoints.length})
                      </h4>
                      <div className="max-h-36 overflow-y-auto border border-slate-800 rounded-lg bg-slate-900/40">
                        <table className="w-full text-xs text-left">
                          <thead className="bg-slate-900 text-slate-400 text-[10px] sticky top-0">
                            <tr>
                              <th className="px-3 py-1.5">Register</th>
                              <th className="px-3 py-1.5">Type</th>
                              <th className="px-3 py-1.5">Unit</th>
                              <th className="px-3 py-1.5">Range</th>
                              <th className="px-3 py-1.5">Description</th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-slate-800">
                            {selectedArtifact.extracted_datapoints.map((dp, i) => (
                              <tr key={i} className="hover:bg-slate-800/40">
                                <td className="px-3 py-1 font-mono text-sky-300">{dp.name}</td>
                                <td className="px-3 py-1 text-slate-400">{dp.type}</td>
                                <td className="px-3 py-1 text-slate-300">{dp.unit}</td>
                                <td className="px-3 py-1 text-slate-400">{dp.min_val} to {dp.max_val}</td>
                                <td className="px-3 py-1 text-slate-400 truncate max-w-xs">{dp.description}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>

                    {/* Synthesized DSL Code Tabs */}
                    <div className="flex-1 flex flex-col min-h-0">
                      <div className="flex items-center justify-between pb-2">
                        <div className="flex bg-slate-900 border border-slate-800 p-0.5 rounded-lg text-xs">
                          <button
                            onClick={() => setDslTab('dml')}
                            className={`px-3 py-1 rounded font-medium ${
                              dslTab === 'dml' ? 'bg-sky-600 text-white' : 'text-slate-400 hover:text-white'
                            }`}
                          >
                            DML DataModel
                          </button>
                          <button
                            onClick={() => setDslTab('mnc')}
                            className={`px-3 py-1 rounded font-medium ${
                              dslTab === 'mnc' ? 'bg-sky-600 text-white' : 'text-slate-400 hover:text-white'
                            }`}
                          >
                            MNC Interface
                          </button>
                          <button
                            onClick={() => setDslTab('cap')}
                            className={`px-3 py-1 rounded font-medium ${
                              dslTab === 'cap' ? 'bg-sky-600 text-white' : 'text-slate-400 hover:text-white'
                            }`}
                          >
                            Capability DSL
                          </button>
                          <button
                            onClick={() => setDslTab('op')}
                            className={`px-3 py-1 rounded font-medium ${
                              dslTab === 'op' ? 'bg-sky-600 text-white' : 'text-slate-400 hover:text-white'
                            }`}
                          >
                            Operation DSL
                          </button>
                        </div>

                        <button
                          onClick={() => {
                            const code =
                              dslTab === 'dml' ? selectedArtifact.generated_dml :
                              dslTab === 'mnc' ? selectedArtifact.generated_mnc :
                              dslTab === 'cap' ? selectedArtifact.generated_cap :
                              selectedArtifact.generated_op;
                            copyCurrentDsl(code);
                          }}
                          className="px-2.5 py-1 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded text-xs flex items-center gap-1"
                        >
                          {copiedDsl ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                          {copiedDsl ? 'Copied' : 'Copy DSL'}
                        </button>
                      </div>

                      <div className="flex-1 bg-slate-900/90 border border-slate-800 rounded-lg p-3 font-mono text-xs text-sky-200 overflow-y-auto max-h-48 whitespace-pre">
                        {dslTab === 'dml' && selectedArtifact.generated_dml}
                        {dslTab === 'mnc' && selectedArtifact.generated_mnc}
                        {dslTab === 'cap' && selectedArtifact.generated_cap}
                        {dslTab === 'op' && selectedArtifact.generated_op}
                      </div>
                    </div>

                  </div>
                ) : (
                  <div className="flex-1 flex flex-col items-center justify-center text-slate-500 text-xs">
                    Select a staged artifact on the left to inspect parameters and synthesized DSL code.
                  </div>
                )}
              </div>

            </div>
          )}

          {/* TAB 3: Notifications Stream */}
          {activeTab === 'notifications' && (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-sm font-semibold text-white">Transactional Notification Audit Stream</h3>
                  <p className="text-xs text-slate-400">
                    PhD Requirement 50: Lifecycle alerting for document extraction completion, staged entity approvals, and billing events
                  </p>
                </div>
                <button
                  onClick={loadNotifications}
                  disabled={isLoadingNotifs}
                  className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-xs flex items-center gap-1.5"
                >
                  <RefreshCw className={`w-3.5 h-3.5 ${isLoadingNotifs ? 'animate-spin' : ''}`} />
                  Refresh
                </button>
              </div>

              <div className="bg-slate-950 border border-slate-800 rounded-xl overflow-hidden">
                <table className="w-full text-xs text-left">
                  <thead className="bg-slate-900 text-slate-400 text-[10px] uppercase">
                    <tr>
                      <th className="px-4 py-2.5">Type</th>
                      <th className="px-4 py-2.5">Recipient</th>
                      <th className="px-4 py-2.5">Subject</th>
                      <th className="px-4 py-2.5">Status</th>
                      <th className="px-4 py-2.5">Channel</th>
                      <th className="px-4 py-2.5">Timestamp</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60">
                    {notifications.length === 0 ? (
                      <tr>
                        <td colSpan={6} className="px-4 py-8 text-center text-slate-500">
                          No notifications recorded yet.
                        </td>
                      </tr>
                    ) : (
                      notifications.map((n) => (
                        <tr key={n.id} className="hover:bg-slate-900/40">
                          <td className="px-4 py-3">
                            <span className="font-mono text-sky-400 text-[11px]">{n.notification_type}</span>
                          </td>
                          <td className="px-4 py-3 text-slate-300">{n.recipient_email}</td>
                          <td className="px-4 py-3 font-medium text-slate-200">{n.subject}</td>
                          <td className="px-4 py-3">
                            <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                              {n.status.toUpperCase()}
                            </span>
                          </td>
                          <td className="px-4 py-3 uppercase text-[10px] text-slate-400">{n.channel}</td>
                          <td className="px-4 py-3 text-slate-400 text-[11px]">
                            {new Date(n.created_at).toLocaleString()}
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}

        </div>

      </div>
    </div>
  );
};
