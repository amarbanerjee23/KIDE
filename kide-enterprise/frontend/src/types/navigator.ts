export type NavigatorMode = 'explorer' | 'search' | 'problems' | 'outline';

export interface OutlineItem {
  id: string;
  name: string;
  kind: 'Package' | 'DataModel' | 'Field' | 'Capability' | 'Operation' | 'Activity' | 'Interface' | 'ControlNode' | 'State' | 'Command' | 'Event' | 'Alarm' | 'DataPoint' | 'Transition' | 'Script';
  line: number;
  column?: number;
  detail?: string;
  children?: OutlineItem[];
}

export interface SearchResultItem {
  id: string;
  fileId: string;
  fileName: string;
  line: number;
  matchText: string;
  matchType: 'filename' | 'symbol' | 'content';
  symbolKind?: string;
}

export interface ArtifactGroupDefinition {
  id: string;
  label: string;
  count: number;
  extensions: string[];
}

