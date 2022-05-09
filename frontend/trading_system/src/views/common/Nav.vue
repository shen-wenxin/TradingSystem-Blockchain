<script src="../../router/index.js"></script>
<template>
  <div>
    <v-app-bar app color="white" id="navbar" light>
      <div class="d-flex align-center">
        <v-img
          alt="Vuetify Logo"
          class="shrink mr-2"
          contain
          src="https://cdn.vuetifyjs.com/images/logos/vuetify-logo-dark.png"
          transition="scale-transition"
          width="40"
        />
      </div>

      <v-row class="mx-2">
        <v-menu
          attach=".v-app-bar"
          v-for="(navMenu, index) in navMenus"
          v-bind:key="index"
          offset-y
          content-class="customMenu"
          open-on-hover
        >
          <template v-slot:activator="{ on }">
            <v-btn
              v-on="on"
              text
              :to="navMenu.hasSubMenu ? '' : navMenu.route"
              class="mx-1"
              color="rgb(50, 50, 50)"
              active-class="customNavBtn"
            >
              {{ navMenu.name }}
              <v-icon v-if="navMenu.hasSubMenu" dense right
                >mdi-chevron-down</v-icon
              >
            </v-btn>
          </template>
          <v-card>
            <v-list v-if="navMenu.hasSubMenu">
              <v-list-item
                v-for="(item, index) in navMenu.subMenu"
                v-bind:key="index"
                link
                :to="item.route"
              >
                <v-list-item-subtitle>{{ item.name }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card>
        </v-menu>
      </v-row>

      <v-spacer></v-spacer>

      <v-btn icon @click="() => {}">
        <v-icon>mdi-magnify</v-icon>
      </v-btn>
      <v-btn icon to="/profile">
        <v-icon>mdi-account</v-icon>
      </v-btn>
      <v-btn icon @click="logout">
        <v-icon>mdi-logout-variant</v-icon>
      </v-btn>
    </v-app-bar>
    <div>
      <router-view name="main"></router-view>
    </div>
  </div>
</template>

<script>
import { generateNavMenu } from "../../utils/nav-generator";

export default {
  name: "Nav",
  created() {
    this.navMenus = generateNavMenu(this.$store.state.user.permittedRouteList);
  },
  methods: {
    logout() {
      this.$store.dispatch("user/userLogout").then(() => {
        this.$router.push("/login");
      });
    },
  },
  components: {},
  data() {
    return {
      currentMenuIndex: 0,
      current: ["mail"],
      navMenus: [],
    };
  },
};
</script>

<style scoped>
.theme--light.v-btn--active::before {
  opacity: 0;
}

.customNavBtn {
  background-color: rgba(33, 150, 243, 0.1);
  color: rgb(33, 150, 243) !important;
  font-weight: bold;
}

/*.customMenu {*/
/*  box-shadow: rgba(9, 30, 66, 0.25) 0px 4px 8px -2px, rgba(9, 30, 66, 0.08) 0px 0px 0px 1px !important;*/
/*}*/

#navbar {
  border-bottom-color: rgb(220, 220, 220);
  border-bottom-width: 3px;
  box-shadow: 0 2px 10px rgb(230, 230, 230);
}
</style>
