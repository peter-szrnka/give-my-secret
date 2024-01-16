export const ROLES_USER_AND_VIEWER = ['ROLE_USER', 'ROLE_VIEWER'];
export const ROLES_ADMIN = ['ROLE_ADMIN'];

export const ROLE_ROUTE_MAP: any = {
  'secret/list': ROLES_USER_AND_VIEWER,
  'apikey/list': ROLES_USER_AND_VIEWER,
  'keystore/list': ROLES_USER_AND_VIEWER,
  'user/list': ROLES_ADMIN,
  'event/list': ROLES_ADMIN,
  'announcement/list': ROLES_ADMIN,
  'system_property/list': ROLES_ADMIN,
  'secret/:id': ROLES_USER_AND_VIEWER,
  'apikey/:id': ROLES_USER_AND_VIEWER,
  'keystore/:id': ROLES_USER_AND_VIEWER,
  'user/:id': ROLES_ADMIN,
  'event/:id': ROLES_ADMIN,
  'announcement/:id': ROLES_ADMIN,
  'system_property/:id': ROLES_ADMIN,
};