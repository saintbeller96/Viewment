<template>
  <div
    class="d-flex"
  >
    <v-avatar
      :size="`${size}rem`"
    >
      <img
        v-if="this.profileImage"
        :src="prefix + profileImage.path"
      >
      <img
        v-else
        src="@/assets/images/account.png"
      >
    </v-avatar>
    <div class="pl-1 align-self-center">
      <span :style="`font-size: ${fontSize}rem;`">{{nickname}}</span>
      <p class="mb-0" style="color: grey;" v-if="writeDate">{{writeDate}}</p>
    </div>
  </div>
</template>

<script>

const SERVER_URL = process.env.VUE_APP_SERVER_URL

export default {
  name: 'UserProfileImage',
  props: {
    profileImage: Object,
    nickname: {
      type: String,
      default: '',
    },
    size: {
      type: Number,
      default: 3,
    },
    fontSize: {
      type: Number,
      default: 1,
    },
    writeDate: String
  },
  data() {
    return {
      prefix: `${SERVER_URL}/images/`,
      imagePath: null,
    }
  },
  watch: {
    profileImage() {
      this.imagePath = this.profileImage
    }
  },
  created() {
    this.imagePath = this.profileImage
  }
}
</script>

<style>

</style>