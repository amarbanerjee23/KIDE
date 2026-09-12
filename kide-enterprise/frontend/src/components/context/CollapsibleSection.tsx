import React, { useState } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';

interface CollapsibleSectionProps {
  title: string;
  count?: number;
  icon?: React.ReactNode;
  defaultExpanded?: boolean;
  badgeClass?: string;
  hideIfZero?: boolean;
  className?: string;
  children: React.ReactNode;
}

export const CollapsibleSection: React.FC<CollapsibleSectionProps> = ({
  title,
  count,
  icon,
  defaultExpanded = true,
  badgeClass = 'bg-gray-800 text-gray-400',
  hideIfZero = true,
  className = '',
  children
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(defaultExpanded);

  // If hideIfZero is true and count is explicitly 0, do not render this section at all
  if (hideIfZero && count === 0) {
    return null;
  }

  return (
    <div className={`border-b border-gray-800/60 last:border-none ${className}`}>
      <button
        type="button"
        onClick={() => setIsExpanded(prev => !prev)}
        className="w-full px-3 py-2 flex items-center justify-between hover:bg-gray-800/40 text-left transition-colors select-none group"
      >
        <div className="flex items-center gap-1.5 min-w-0">
          <span className="text-gray-500 group-hover:text-gray-300 transition-colors">
            {isExpanded ? <ChevronDown size={12} /> : <ChevronRight size={12} />}
          </span>
          {icon && <span className="text-gray-400 group-hover:text-gray-300 shrink-0">{icon}</span>}
          <span className="text-[11px] font-bold uppercase tracking-wider text-gray-400 group-hover:text-gray-200 truncate">
            {title}
          </span>
        </div>

        {count !== undefined && count > 0 && (
          <span className={`text-[10px] font-mono px-1.5 py-0.2 rounded-full font-bold shrink-0 ${badgeClass}`}>
            {count}
          </span>
        )}
      </button>

      {isExpanded && (
        <div className="px-3 pb-2.5 pt-0.5 text-xs text-gray-300">
          {children}
        </div>
      )}
    </div>
  );
};

