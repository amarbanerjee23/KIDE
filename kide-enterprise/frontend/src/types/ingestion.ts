export interface IngestionJob {
  id: number;
  job_uuid: string;
  org_id: number;
  user_id?: number;
  filename: string;
  file_type: string;
  file_size_bytes: number;
  status: 'pending' | 'processing' | 'completed' | 'failed';
  progress_pct: number;
  stage_message: string;
  extracted_devices_count: number;
  extracted_capabilities_count: number;
  extracted_operations_count: number;
  extracted_parameters_count: number;
  error_message?: string;
  created_at: string;
  completed_at?: string;
}

export interface ExtractedDatapoint {
  name: string;
  type: string;
  unit: string;
  min_val: number;
  max_val: number;
  description: string;
}

export interface ExtractedCommand {
  name: string;
  parameters: any[];
  return_type: string;
  description: string;
}

export interface ExtractedAlarm {
  name: string;
  level: number;
  condition?: string;
  description: string;
}

export interface StagedKnowledgeArtifact {
  id: number;
  artifact_uuid: string;
  job_id: number;
  org_id: number;
  name: string;
  category: string;
  protocol: string;
  status: 'draft' | 'approved' | 'rejected' | 'promoted';
  device_metadata: Record<string, any>;
  extracted_datapoints: ExtractedDatapoint[];
  extracted_commands: ExtractedCommand[];
  extracted_alarms: ExtractedAlarm[];
  extracted_capabilities: any[];
  generated_dml: string;
  generated_mnc: string;
  generated_cap: string;
  generated_op: string;
  review_notes?: string;
  reviewed_by_user_id?: number;
  reviewed_at?: string;
  created_at: string;
}

export interface NotificationItem {
  id: number;
  org_id?: number;
  user_id?: number;
  recipient_email: string;
  notification_type: string;
  subject: string;
  body_text: string;
  body_html?: string;
  status: string;
  channel: string;
  metadata_json?: string;
  created_at: string;
}

export interface NotificationStats {
  total_notifications: number;
  sent_count: number;
  failed_count: number;
  latest_notification?: string;
}

export interface SampleTemplate {
  id: string;
  name: string;
  category: string;
  protocol: string;
  manufacturer: string;
  description: string;
  file_type: string;
}

