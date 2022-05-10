export function generateNavMenu(routes) {
  let navMenuList = [];
  routes.forEach(route => {
    if (route.meta && route.meta.nav) {
      navMenuList.push({
        name: route.meta.nav.name,
        hasSubMenu: false,
        route: route
      })
    } else {
      if (route.children) {
        navMenuList = navMenuList.concat(generateNavMenu(route.children));
      }
    }
  })
  return navMenuList;
}