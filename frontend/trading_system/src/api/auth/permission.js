/**
 * 判断当前角色是否拥有传入的路由对象的访问权限
 * 依据是路由的meta信息，如果没有meta信息，当作开放无权限路由处理
 * @param route 单个路由对象
 * @param roleType 角色类型
 * @returns {boolean} 是否拥有当前路由的权限
 */
function hasRoutePermission(route, roleType) {
  if (route.meta && route.meta.roles) {
    return route.meta.roles.includes(roleType);
  } else {
    return true;
  }
}

/**
 * 递归过滤异步路由表，为当前角色生成合法路由表
 * @param routes 异步路由表
 * @param roleType 角色类型
 * @returns 当前用户权限内的路由列表
 */
export function filterAsyncRoutes(routes, roleType) {
  let filteredRouteList = [];
  routes.forEach(route => {
    let tmp = {...route};
    if (hasRoutePermission(route, roleType)) {
      if (route.children) {
        tmp.children = filterAsyncRoutes(route.children, roleType);
      }
      filteredRouteList.push(tmp);
    }
  });
  return filteredRouteList;
}