<template>
  <v-list-item v-if="replyInfo">
    <v-row
      v-if="isUpdating"
      space-between
    >
      <v-col>
        <!-- 유저 프로필 이미지 or 아이콘 / 유저 닉네임 시작 -->
        <UserProfileImage 
          :nickname="replyInfo.user.nickname"
          :profile-image="replyInfo.user.ProfileImage"
        />
        <!-- 유저 프로필 이미지 or 아이콘 / 유저 닉네임 끝 -->

        <!-- 댓글 수정 시작 -->
        <v-text-field
        v-model="updateContent"
        class="ma-0 pa-0"
        label="댓글을 수정해주세요"
        outlined
        hide-details
        ></v-text-field>
        <!-- 댓글 수정 끝-->
      </v-col>

      <v-col 
        cols="3"
      >
        <v-card>
          <!-- 작성자가 수정 중이라면 수정완료 버튼 표시 시작-->
          <div
          >
            <v-btn text color="primary" @click="onUpdateReplyComplete">완료</v-btn>
          </div>
          <!-- 작성자가 수정 중이라면 수정완료 버튼 표시 끝 -->

          <!-- 댓글 작성 시간 -->
          <p class="text-caption mb-0">{{replyInfo.wdate}}</p>
          <!-- 댓글 작성 시간 -->
        </v-card>
      </v-col>

    </v-row>
    <v-row
      v-else
      space-between
    >

      <v-col>
        <!-- 유저 프로필 이미지 or 아이콘 / 유저 닉네임 시작 -->
        <v-card>
          <UserProfileImage 
            :nickname="replyInfo.user.nickname"
            :profile-image="replyInfo.user.ProfileImage"
          />
          <v-btn 
            v-if="this.replyType === 'reply'"
            fab 
            bottom
            @click="showRereply = !showRereply"
          >대댓글</v-btn>
          <h1 v-if="showRereply">여기 이제 대댓글 쓰는 란!</h1>
          
        </v-card>
        <!-- 유저 프로필 이미지 or 아이콘 / 유저 닉네임 끝 -->

        <!-- 댓글 내용 시작 -->
        <div class="d-line mr-1">
          {{ replyInfo.contents }}
        </div>
        <!-- 댓글 내용 끝-->
      </v-col>

      <v-col 
        cols="3"
      >
        <v-card>
          <!-- 작성자가 본인이면 수정/삭제 버튼 표시 시작-->
          <div
            v-if="replyInfo.user.userId === loginUserId"
          >
            <v-btn text color="primary" @click="onUpdateReply">수정</v-btn>
            <v-btn text color="error" @click="onDeleteReply">삭제</v-btn>
          </div>
          <!-- 작성자가 본인이면 수정/삭제 버튼 표시 끝 -->

          <!-- 댓글 작성 시간 -->
          <p class="text-caption mb-0">{{replyInfo.wdate | dateFormat()}}</p>
          <!-- 댓글 작성 시간 -->
        </v-card>
      </v-col>
    </v-row>
    <!-- child라는 프로퍼티가 있고 이 것이 빈값이 아니라면 -->
    <v-row v-if="(typeof replyInfo.child) !== 'undefined' && replyInfo.child">
      <v-col
        cols="10"
        offset="2"
      >
      <ReplyList 
        :replies="replyInfo.child"
        :profileUserId="articleInfo.user.userId"
        :loginUserId="loginUserId"
        replyType="rereply"
      />
      </v-col>
    </v-row>
  </v-list-item>
</template>

<script>
import axios from 'axios'
import UserProfileImage from '@/components/user/UserProfileImage'
import ReplyList from '@/components/reply/ReplyList'

const SERVER_URL = process.env.VUE_APP_SERVER_URL

export default {
  name: 'Reply',
  components: {
    UserProfileImage,
    ReplyList
  },
  filters: {
    dateFormat(date) {
      const dateArray = date.split(' ')[0].split('-')
      return dateArray[0]+'년 '+dateArray[1]+'월 '+ dateArray[2]+'일'
    },
  },
  props: {
    reply: Object,
    profileUserId: String,
    loginUserId: String,
    replyType: String,
  },
  data: () => ({
      replyInfo: null,
      isUpdating: false,
      updateContent: '',
      showRereply: false,
    }),
  computed: {
    getToken(){
      const token = sessionStorage.getItem('jwt')
      const config = {
        headers: {
          'X-Authorization-Firebase': token
        }
      }
      return config
    }
  },
  methods: {
    fetchData() {
      this.replyInfo = this.reply
    },
    onCreateRereply() {
      
    },
    onUpdateReply() {
      this.updateContent = this.replyInfo.contents
      this.isUpdating = true
    },
    onDeleteReply() {
      var postFix = ''

      if (this.replyType === "rereply") {
        postFix = `/rereply/${this.replyInfo.rereplyId}`
      } else {
        postFix = `/${this.replyInfo.replyId}`
      }

      axios.delete(`${SERVER_URL}/repries${postFix}`, this.getToken)
      .then(() => {
        this.replyInfo = null
      })
      .catch(err => {
        alert(err)
      })
    },
    onUpdateReplyComplete() {
      var postFix = ''

      var params = {
        'contents': this.updateContent,
        'userId': this.replyInfo.userId,
      }

      if (this.replyType === "rereply") {
        params.parentId = this.replyInfo.parentId
        postFix = "/rereply"
      } else {
        params.articleId = this.replyInfo.articleId
      }

      axios.put(`${SERVER_URL}/repries${postFix}`, params, this.getToken)
      .than(() => {
        var date = new Date();
        date = date.getFullYear() + "년" + (date.getMonth() + 1) + "월" + date.getDate() + "일"
        this.replyInfo.wdate = date
        this.replyInfo.contents = this.updateContent
      })
      .than(()=> {
        this.isUpdating = false
      })
      .catch(err => {
        alert(err)
      })
    },
  },
}
</script>

<style>

</style>