<template>
  <v-card
    class="mx-auto"
    max-width="344"
    outlined
  >
    <v-list-item three-line>
      <v-list-item-content>
        <div class="overline mb-4">Error</div>
        <v-list-item-title class="headline mb-1">Sorry</v-list-item-title>
        <v-list-item-subtitle>Something Wrong</v-list-item-subtitle>
      </v-list-item-content>

      <v-list-item-avatar
        tile
        size="80"
        color="grey"
      ></v-list-item-avatar>
    </v-list-item>

    <v-card-actions>
      <v-btn text color="primary" @click="goPrevious">이전</v-btn>
    </v-card-actions>
  </v-card>
</template>
<script>
export default {
  methods: {
    goPrevious() {
      this.$router.go(-1)
    }
  }
}
</script>

<style>

</style>