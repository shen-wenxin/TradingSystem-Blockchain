<template>
  <div class="main">
    <div id="tipsCard">
      <v-card
          class="mx-auto px-4 py-3"
          elevation="4"
          max-width="600"
      >
        <v-row justify="center" class="ma-6">
          <v-col cols="auto">
            <v-avatar size="200">
              <v-img
                  src="../../assets/bigguy.jpg"
              ></v-img>
            </v-avatar>
          </v-col>
        </v-row>
        <v-card-title class="text-h4 mb-2">这里是 404 页面，您走丢了哈~</v-card-title>
        <v-card-text class="text-body-1">
          <p>只要怀揣等待和希望，</p>
          <p>
            终有一天，
            你也能遇到与你相交的链表。
          </p>
          <p style="text-align: right">——By cccjh</p>
        </v-card-text>
        <v-row>
          <v-col cols="auto">
            <v-btn
                depressed
                to="/"
                text
                large
                color="primary"
            >
              <v-icon>mdi-chevron-left</v-icon>
              回到首页
            </v-btn>
          </v-col>
          <v-spacer/>
          <v-col cols="auto">
            <v-btn
                depressed
                text
                large
                href="https://leetcode-cn.com/problems/intersection-of-two-linked-lists/"
                color="warning"
            >
              相交链表是啥
              <v-icon>mdi-chevron-right</v-icon>
            </v-btn>
          </v-col>
        </v-row>

      </v-card>
    </div>


  </div>
</template>

<script>
export default {
  name: "404"
}
</script>

<style scoped>
.main {
  background-image: url("../../assets/pnfbg.png");
  background-repeat: no-repeat;
  background-attachment: fixed;
  background-size: cover;
  height: 100%;
}

#tipsCard {
  margin: auto;
  position: relative;
  top: 50%;
  transform: translateY(-60%);
}
</style>