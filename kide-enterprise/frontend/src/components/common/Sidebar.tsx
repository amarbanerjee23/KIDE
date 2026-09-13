import React, { useState } from 'react';
import { NavLink, useMatch } from 'react-router-dom';
import { 
  LayoutDashboard, FolderOpen, Settings, LogOut, 
  PanelLeftClose, PanelLeftOpen, FolderTree, Search, 
  AlertCircle, ListTree, ShieldCheck 
} from 'lucide-react';
import { useAuthStore } from '../../stores/authStore';
import { useEditorStore } from '../../stores/editorStore';
import { ProjectNavigator } from '../navigator/ProjectNavigator';
import { NavigatorMode } from '../../types/navigator';

interface SidebarProps {
  isCollapsed?: boolean;
  onToggleCollapse?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ 
  isCollapsed: controlledCollapsed, 
  onToggleCollapse 
}) => {
  const { user, logout } = useAuthStore();
  const { projectId, validationErrors } = useEditorStore();
  
  // Check if we are inside a project workspace route
  const projectRouteMatch = useMatch({ path: '/projects/:id', end: false });
  const isInsideProject = Boolean(projectRouteMatch || projectId);

  // Active mode in Project Navigator
  const [activeNavigatorMode, setActiveNavigatorMode] = useState<NavigatorMode>('explorer');

  // Sidebar width resizing state
  const [sidebarWidth, setSidebarWidth] = useState<number>(() => {
    try {
      const saved = localStorage.getItem('kide_sidebar_width');
      if (saved) {
        const val = parseInt(saved, 10);
        if (!isNaN(val) && val >= 220 && val <= 360) return val;
      }
    } catch {}
    return 260;
  });
  const [isResizing, setIsResizing] = useState(false);

  // Collapsed state
  const [internalCollapsed, setInternalCollapsed] = useState<boolean>(() => {
    try {
      const saved = localStorage.getItem('kide_sidebar_collapsed');
      if (saved !== null) {
        return saved === 'true';
      }
    } catch {}
    return typeof window !== 'undefined' ? window.innerWidth < 1440 : false;
  });

  const isCollapsed = controlledCollapsed !== undefined ? controlledCollapsed : internalCollapsed;

  const setCollapsed = (val: boolean) => {
    if (onToggleCollapse && val !== isCollapsed) {
      onToggleCollapse();
    } else {
      setInternalCollapsed(val);
      localStorage.setItem('kide_sidebar_collapsed', String(val));
    }
  };

  const toggleCollapse = () => {
    setCollapsed(!isCollapsed);
  };

  // Handle expanding sidebar into a specific mode when clicked in collapsed mode
  const handleCollapsedAction = (mode: NavigatorMode) => {
    setActiveNavigatorMode(mode);
    setCollapsed(false);
  };

  // Handle dragging sidebar right edge to resize
  const handleResizeStart = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsResizing(true);

    const handleMouseMove = (moveEvent: MouseEvent) => {
      const clamped = Math.max(220, Math.min(360, moveEvent.clientX));
      setSidebarWidth(clamped);
      try {
        localStorage.setItem('kide_sidebar_width', String(clamped));
      } catch {}
    };

    const handleMouseUp = () => {
      setIsResizing(false);
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
  };

  const globalNavItems = [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/projects', icon: FolderOpen, label: 'Projects' },
    { to: '/settings', icon: Settings, label: 'Settings' },
    ...(user?.role === 'owner' ? [{ to: '/admin', icon: ShieldCheck, label: 'Admin Console' }] : [])
  ];

  // Count problems for collapsed mode badge
  const totalProblems = Object.values(validationErrors).reduce(
    (acc, errs) => acc + (errs ? errs.length : 0), 0
  );
  const hasErrors = Object.values(validationErrors).some(
    errs => errs && errs.some(e => e.severity === 'error')
  );

  return (
    <aside 
      aria-label="Global and Project Navigation"
      style={{ width: isCollapsed ? 68 : sidebarWidth }}
      className={`relative bg-[#0d1117] border-r border-gray-800 flex flex-col justify-between select-none shrink-0 z-30 h-screen ${
        isResizing ? 'transition-none cursor-col-resize select-none' : 'transition-[width] duration-200 ease-in-out'
      }`}
    >
      {/* ────────────────────────────────────────────────
          TIER 1: FIXED TOP - Brand Header & Global Navigation
         ──────────────────────────────────────────────── */}
      <div className="flex-none">
        {/* Brand Header */}
        <div className={`h-14 flex items-center border-b border-gray-800/80 px-3 ${
          isCollapsed ? 'justify-center' : 'justify-between'
        }`}>
          {!isCollapsed ? (
            <div className="flex items-center gap-2 overflow-hidden">
              <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-blue-600 to-indigo-600 flex items-center justify-center font-bold text-white text-sm shadow-sm shrink-0">
                K
              </div>
              <div className="flex items-center gap-1.5 font-bold tracking-wider text-sm text-gray-100 truncate">
                <span>KIDE</span>
                <span className="text-[#16c79a] font-semibold text-xs px-1.5 py-0.5 rounded bg-emerald-950/60 border border-emerald-800/50">
                  PRO
                </span>
              </div>
            </div>
          ) : (
            <div 
              className="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-600 to-indigo-600 flex items-center justify-center font-bold text-white text-sm shadow-sm"
              title="KIDE Enterprise"
            >
              K
            </div>
          )}

          <button
            onClick={toggleCollapse}
            aria-label={isCollapsed ? "Expand sidebar" : "Collapse sidebar"}
            title={isCollapsed ? "Expand sidebar" : "Collapse sidebar"}
            className={`p-1.5 rounded-lg text-gray-400 hover:text-white hover:bg-gray-800 transition ${
              isCollapsed ? 'hidden' : 'inline-flex'
            }`}
          >
            <PanelLeftClose className="w-4 h-4" />
          </button>
        </div>

        {/* Global Navigation Links */}
        <nav className="py-2.5 px-2 space-y-1">
          <div className={`${isCollapsed ? 'hidden' : 'px-2 py-0.5 text-[10px] font-bold uppercase tracking-wider text-gray-500'}`}>
            GLOBAL
          </div>
          {globalNavItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              title={isCollapsed ? item.label : undefined}
              className={({ isActive }) =>
                `flex items-center rounded-lg text-xs font-medium transition-all ${
                  isCollapsed ? 'justify-center px-2 py-2' : 'px-2.5 py-1.5'
                } ${
                  isActive 
                    ? 'bg-blue-600/15 text-blue-400 border border-blue-500/30 font-semibold shadow-sm' 
                    : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/60 border border-transparent'
                }`
              }
            >
              <item.icon className={`w-4 h-4 shrink-0 ${isCollapsed ? '' : 'mr-2.5'}`} />
              {!isCollapsed && <span className="truncate">{item.label}</span>}
            </NavLink>
          ))}
        </nav>
      </div>

      {/* ────────────────────────────────────────────────
          TIER 2: FLEXIBLE MIDDLE - Contextual Project Navigator
          Only useful & visible when inside a project workspace.
         ──────────────────────────────────────────────── */}
      {isInsideProject ? (
        !isCollapsed ? (
          <ProjectNavigator 
            activeMode={activeNavigatorMode}
            onModeChange={setActiveNavigatorMode}
          />
        ) : (
          /* Collapsed Mode Contextual Action Icons */
          <div className="flex-1 min-h-0 flex flex-col items-center py-3 border-t border-gray-800/80 space-y-2">
            <button
              onClick={() => handleCollapsedAction('explorer')}
              className="p-2 rounded-lg text-gray-400 hover:text-blue-400 hover:bg-blue-950/30 transition relative"
              title="Project Explorer"
              aria-label="Project Explorer"
            >
              <FolderTree className="w-4 h-4" />
            </button>

            <button
              onClick={() => handleCollapsedAction('search')}
              className="p-2 rounded-lg text-gray-400 hover:text-blue-400 hover:bg-blue-950/30 transition relative"
              title="Search Project"
              aria-label="Search Project"
            >
              <Search className="w-4 h-4" />
            </button>

            <button
              onClick={() => handleCollapsedAction('problems')}
              className="p-2 rounded-lg text-gray-400 hover:text-blue-400 hover:bg-blue-950/30 transition relative"
              title={`Problems (${totalProblems})`}
              aria-label={`Problems (${totalProblems})`}
            >
              <AlertCircle className="w-4 h-4" />
              {totalProblems > 0 && (
                <span
                  className={`absolute top-1 right-1 w-2 h-2 rounded-full ${
                    hasErrors ? 'bg-rose-500' : 'bg-amber-400'
                  }`}
                />
              )}
            </button>

            <button
              onClick={() => handleCollapsedAction('outline')}
              className="p-2 rounded-lg text-gray-400 hover:text-blue-400 hover:bg-blue-950/30 transition relative"
              title="Symbol Outline"
              aria-label="Symbol Outline"
            >
              <ListTree className="w-4 h-4" />
            </button>
          </div>
        )
      ) : (
        /* When not in a project workspace, leave clean flexible space (no decorative cards) */
        <div className="flex-1 min-h-0" />
      )}

      {/* ────────────────────────────────────────────────
          TIER 3: FIXED BOTTOM - User Account & Logout
         ──────────────────────────────────────────────── */}
      <div className="flex-none p-3 border-t border-gray-800/80 bg-[#0b0f19]/80">
        {isCollapsed && (
          <div className="flex justify-center mb-2.5">
            <button
              onClick={toggleCollapse}
              aria-label="Expand sidebar"
              title="Expand sidebar"
              className="p-1.5 rounded-lg text-gray-400 hover:text-white hover:bg-gray-800 transition"
            >
              <PanelLeftOpen className="w-4 h-4" />
            </button>
          </div>
        )}

        <div className={`flex items-center mb-2 ${isCollapsed ? 'justify-center' : 'px-1'}`}>
          <div 
            className="w-7 h-7 rounded-full bg-gradient-to-tr from-emerald-600 to-teal-500 flex items-center justify-center text-white font-bold text-xs shrink-0 shadow-sm"
            title={user?.full_name || user?.name || user?.email || 'User'}
          >
            {user?.full_name?.charAt(0) || user?.name?.charAt(0) || 'U'}
          </div>
          {!isCollapsed && (
            <div className="ml-2.5 min-w-0 flex-1">
              <p className="text-xs font-semibold text-gray-200 truncate">
                {user?.full_name || user?.name || 'Engineer'}
              </p>
              <p className="text-[11px] text-gray-400 truncate">
                {user?.email || 'engineer@kide.ai'}
              </p>
            </div>
          )}
        </div>

        <button
          onClick={logout}
          aria-label="Logout"
          title={isCollapsed ? "Logout" : undefined}
          className={`w-full flex items-center rounded-lg text-xs font-medium text-gray-400 hover:text-rose-400 hover:bg-rose-950/20 transition-colors ${
            isCollapsed ? 'justify-center py-1.5' : 'px-2 py-1.5'
          }`}
        >
          <LogOut className={`w-3.5 h-3.5 shrink-0 ${isCollapsed ? '' : 'mr-2'}`} />
          {!isCollapsed && <span>Logout</span>}
        </button>
      </div>

      {/* Resizer Handle (Only when expanded) */}
      {!isCollapsed && (
        <div
          onMouseDown={handleResizeStart}
          className="absolute right-0 top-0 bottom-0 w-1 cursor-col-resize hover:bg-blue-500/60 active:bg-blue-500 transition-colors z-40"
          title="Drag to resize sidebar width"
        />
      )}
    </aside>
  );
};

export default Sidebar;
