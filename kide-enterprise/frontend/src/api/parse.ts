import { ParseResult } from '../types/models';
import { fetchClient } from './client';

export const parseText = async (language: string, text: string): Promise<ParseResult> => {
  let lang = language.toLowerCase();
  if (lang === 'activitydsl') lang = 'activity';
  else if (lang === 'mncml') lang = 'mnc';
  else if (lang === 'dmldsl') lang = 'dml';
  else if (lang === 'capabilitydsl') lang = 'capability';
  else if (lang === 'operationdsl') lang = 'operation';

  try {
    const data = await fetchClient(`/parse/${lang}`, {
      method: 'POST',
      body: JSON.stringify({ content: text }),
    });
    
    const errors = (data.errors || []).map((err: any) => 
      typeof err === 'string' ? { message: err, severity: 'error' as const } : err
    );

    return {
      ast: data.ast || null,
      errors
    };
  } catch (error: any) {
    console.error('Failed to parse:', error);
    return { ast: null, errors: [{ message: error.message || String(error), severity: 'error' }] };
  }
};

