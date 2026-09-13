/**
 * Semantic Scope Resolver for KIDE Enterprise.
 * Faithfully ports and enriches the legacy Xtext ScopeProvider and ProposalProvider rules.
 * Enforces strict semantic visibility (e.g. Capability actions ONLY offer commands/events/alarms/datapoints
 * from compatible component interfaces).
 */

import { projectSymbolIndex, IndexedSymbol } from './symbolIndex';
import { CapabilityNode, ActivityDiagramNode, MncModelNode } from './ast';

export class ScopeResolver {
  /**
   * Returns commands accessible to a Capability.
   * STRICT FILTER: Only returns commands declared in the Capability's compatible component interfaces.
   */
  public getCommandsForCapability(cap: CapabilityNode | null): IndexedSymbol[] {
    if (!cap || !cap.componentInterfaces || cap.componentInterfaces.length === 0) {
      // If unbound, return no interface commands
      return [];
    }

    const validInterfaceNames = new Set(cap.componentInterfaces.map(i => i.name));
    const commands: IndexedSymbol[] = [];

    // Find InterfaceDescription symbols matching the declared names
    for (const ifName of validInterfaceNames) {
      const ifSym = projectSymbolIndex.findSymbolByName(ifName, 'InterfaceDescription');
      if (ifSym) {
        // Find all Command symbols belonging to this interface
        const fileSymbols = projectSymbolIndex.getSymbolsForFile(ifSym.fileId);
        for (const s of fileSymbols) {
          if (s.kind === 'Command' && s.containerName === ifName) {
            commands.push(s);
          }
        }
      }
    }

    return commands;
  }

  /**
   * Returns alarms accessible to a Capability.
   * STRICT FILTER: Only returns alarms declared in compatible interfaces.
   */
  public getAlarmsForCapability(cap: CapabilityNode | null): IndexedSymbol[] {
    if (!cap || !cap.componentInterfaces || cap.componentInterfaces.length === 0) return [];

    const validInterfaceNames = new Set(cap.componentInterfaces.map(i => i.name));
    const alarms: IndexedSymbol[] = [];

    for (const ifName of validInterfaceNames) {
      const ifSym = projectSymbolIndex.findSymbolByName(ifName, 'InterfaceDescription');
      if (ifSym) {
        const fileSymbols = projectSymbolIndex.getSymbolsForFile(ifSym.fileId);
        for (const s of fileSymbols) {
          if (s.kind === 'Alarm' && s.containerName === ifName) {
            alarms.push(s);
          }
        }
      }
    }

    return alarms;
  }

  /**
   * Returns events accessible to a Capability.
   * STRICT FILTER: Only returns events declared in compatible interfaces.
   */
  public getEventsForCapability(cap: CapabilityNode | null): IndexedSymbol[] {
    if (!cap || !cap.componentInterfaces || cap.componentInterfaces.length === 0) return [];

    const validInterfaceNames = new Set(cap.componentInterfaces.map(i => i.name));
    const events: IndexedSymbol[] = [];

    for (const ifName of validInterfaceNames) {
      const ifSym = projectSymbolIndex.findSymbolByName(ifName, 'InterfaceDescription');
      if (ifSym) {
        const fileSymbols = projectSymbolIndex.getSymbolsForFile(ifSym.fileId);
        for (const s of fileSymbols) {
          if (s.kind === 'Event' && s.containerName === ifName) {
            events.push(s);
          }
        }
      }
    }

    return events;
  }

  /**
   * Returns datapoints accessible to a Capability.
   * STRICT FILTER: Only returns datapoints declared in compatible interfaces.
   */
  public getDataPointsForCapability(cap: CapabilityNode | null): IndexedSymbol[] {
    if (!cap || !cap.componentInterfaces || cap.componentInterfaces.length === 0) return [];

    const validInterfaceNames = new Set(cap.componentInterfaces.map(i => i.name));
    const dataPoints: IndexedSymbol[] = [];

    for (const ifName of validInterfaceNames) {
      const ifSym = projectSymbolIndex.findSymbolByName(ifName, 'InterfaceDescription');
      if (ifSym) {
        const fileSymbols = projectSymbolIndex.getSymbolsForFile(ifSym.fileId);
        for (const s of fileSymbols) {
          if (s.kind === 'DataPoint' && s.containerName === ifName) {
            dataPoints.push(s);
          }
        }
      }
    }

    return dataPoints;
  }

  /**
   * Returns valid Activities that can be targeted by nextActivity inside a given ActivityDiagram.
   * Scoped exclusively to the current diagram activities.
   */
  public getActivitiesForDiagram(diagram: ActivityDiagramNode | null): IndexedSymbol[] {
    if (!diagram) {
      return projectSymbolIndex.findSymbolsByKind('Activity');
    }

    const currentDiagramActivities = new Set(diagram.activities.map(a => a.name));
    const allActivities = projectSymbolIndex.findSymbolsByKind('Activity');

    return allActivities.filter(a => currentDiagramActivities.has(a.name));
  }

  /**
   * Returns valid Capabilities for requireCapability in Activity diagrams.
   */
  public getCapabilitiesForActivity(): IndexedSymbol[] {
    return projectSymbolIndex.findSymbolsByKind('Capability');
  }

  /**
   * Returns valid Operations for requireOperation in Activity diagrams.
   */
  public getOperationsForActivity(): IndexedSymbol[] {
    return projectSymbolIndex.findSymbolsByKind('Operation');
  }

  /**
   * Returns valid DataModels for on context in Activity diagrams.
   */
  public getDataModelsForContext(): IndexedSymbol[] {
    return projectSymbolIndex.findSymbolsByKind('DataModel');
  }

  /**
   * Returns valid OperatingStates for state transitions in an MNC-ML model.
   */
  public getOperatingStatesForMnc(mnc: MncModelNode | null): IndexedSymbol[] {
    if (!mnc) {
      return projectSymbolIndex.findSymbolsByKind('State');
    }

    const localStates: IndexedSymbol[] = [];
    if (mnc.interfaceDescription?.operatingStates) {
      for (const st of mnc.interfaceDescription.operatingStates) {
        if (st.name) {
          const sym = projectSymbolIndex.findSymbolByName(st.name, 'State');
          if (sym) localStates.push(sym);
        }
      }
    }

    if (localStates.length > 0) return localStates;
    return projectSymbolIndex.findSymbolsByKind('State');
  }
}

export const scopeResolver = new ScopeResolver();
