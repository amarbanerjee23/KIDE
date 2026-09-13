import { fetchClient } from './client';
import {
  Plan,
  Subscription,
  CheckoutPayload,
  CheckoutResponse,
  PortalResponse,
  ActivateTrialResponse
} from '../types/billing';

export const billingApi = {
  listPlans: (): Promise<Plan[]> =>
    fetchClient('/billing/plans'),

  getSubscription: (): Promise<Subscription> =>
    fetchClient('/billing/subscription'),

  createCheckout: (data: CheckoutPayload): Promise<CheckoutResponse> =>
    fetchClient('/billing/checkout', {
      method: 'POST',
      body: JSON.stringify(data)
    }),

  createPortal: (returnUrl?: string): Promise<PortalResponse> =>
    fetchClient('/billing/portal', {
      method: 'POST',
      body: JSON.stringify({ return_url: returnUrl })
    }),

  activateTrial: (): Promise<ActivateTrialResponse> =>
    fetchClient('/billing/trial/activate', {
      method: 'POST'
    })
};

