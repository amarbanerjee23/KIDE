import React from 'react';
import { ProjectHeader } from './ProjectHeader';

interface Props {
  showAssistant?: boolean;
  setShowAssistant?: (show: boolean | ((prev: boolean) => boolean)) => void;
}

/**
 * Legacy wrapper maintaining backwards compatibility.
 * Replaced by the modular five-region architecture:
 * - ProjectHeader (Region B)
 * - WorkflowStepper (Region C)
 * - WorkspaceNavigation (Region D)
 */
export const UnifiedWorkspaceHeader: React.FC<Props> = () => {
  return <ProjectHeader />;
};

export default UnifiedWorkspaceHeader;
