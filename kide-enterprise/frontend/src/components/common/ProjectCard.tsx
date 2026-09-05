import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Folder, Clock, FileJson } from 'lucide-react';
import { Project } from '../../types/models';

interface ProjectCardProps {
  project: Project;
}

const ProjectCard: React.FC<ProjectCardProps> = ({ project }) => {
  const navigate = useNavigate();

  return (
    <div 
      onClick={() => navigate(`/projects/${project.id}`)}
      className="bg-[#16213e] border border-[#0f3460] rounded-lg p-5 hover:border-[#16c79a] cursor-pointer transition-all hover:-translate-y-1 group"
    >
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-[#0f3460] rounded-lg group-hover:bg-[#16c79a]/20 group-hover:text-[#16c79a] transition-colors">
            <Folder className="w-5 h-5 text-gray-300 group-hover:text-[#16c79a]" />
          </div>
          <h3 className="text-lg font-semibold text-white truncate">{project.name}</h3>
        </div>
      </div>
      
      <p className="text-gray-400 text-sm mb-4 line-clamp-2 min-h-[40px]">
        {project.description || 'No description provided.'}
      </p>
      
      <div className="flex items-center justify-between text-xs text-gray-500 pt-3 border-t border-[#0f3460]">
        <div className="flex items-center space-x-1">
          <FileJson className="w-3.5 h-3.5" />
          <span>{project.file_count} files</span>
        </div>
        <div className="flex items-center space-x-1">
          <Clock className="w-3.5 h-3.5" />
          <span>{new Date(project.updated_at).toLocaleDateString()}</span>
        </div>
      </div>
    </div>
  );
};

export default ProjectCard;

