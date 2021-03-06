<template>
  <v-row v-if="loading">
    <v-progress-circular
      :width="3"
      color="red"
      indeterminate
    ></v-progress-circular>
  </v-row>
  <v-row
    v-else
    justify="center"
  >
    <v-col
      lg="4"
      md="4"
      sm="6"
    > 
      <Alert
        v-if="alert.alerted"
        :message="alert.message"
        :color="alert.color ? alert.color : 'error'"
      />
      <div class="d-flex justify-center">
        <div>
          <div
            class="relative-container"
          > 
            <v-avatar
              size="9rem"
            >
              <img
                v-if="profileImageUrl"
                :src="profileImageUrl"
              >
              <img
                v-else
                src="@/assets/images/account.png"
              >
            </v-avatar>

            <!-- 프로필 삭제 버튼 시작 -->
            <v-btn
              class="bottom-right-position"
              icon
              @click="onDeleteProfileImageButton"
            >
              <v-icon
              x-large
              color="primary"
              >
              mdi-delete-empty-outline
              </v-icon>
            </v-btn>
            <!-- 프로필 삭제 버튼 끝 -->
          </div>

          <div class="d-flex justify-center my-4">
            <!-- <input type="file" ref="file" hidden>
            <v-btn @click="$refs.file.click().selectFile">사진 선택</v-btn> -->
            <v-btn @click="onPickFile">사진 선택</v-btn>
            <input
              type="file"
              hidden
              ref="fileInput"
              accept="image/*"
              @change="onFilePicked"/>
          </div>
        </div>
      </div>


      <!-- 닉네임, 자기소개 입력 창 + 유효성 검사 시작 -->
      <validation-observer
        ref="observer"
        v-slot="{ invalid }"
      >
        <form @submit.prevent="submit">
          <validation-provider
            mode="aggressive"
            v-slot="{ errors }"
            rules="required|max:8"
          >
            <v-text-field
              v-model="nickname"
              :error-messages="errors"
              label="닉네임"
            ></v-text-field>
          </validation-provider>
          <validation-provider
            mode="aggressive"
            v-slot="{ errors }"
            rules="max:50"
          >
            <v-text-field
              v-model="message"
              :error-messages="errors"
              label="소개"
              multi-line
            ></v-text-field>
          </validation-provider>
          <v-btn
            class="mr-4"
            type="submit"
            :disabled="invalid"
            block
          >
            submit
          </v-btn>
        </form>
      </validation-observer>
      <!-- 닉네임, 자기소개 입력 창 + 유효성 검사 끝 -->
      <Confirm 
        v-if="confirm.confirmed"
        :message="confirm.message"
        @on-confirmed="onDeleteConfirmed"
      />
    </v-col>
  </v-row>
</template>

<script>
import axios from 'axios'
import { required, max } from 'vee-validate/dist/rules'
import { extend, ValidationObserver, ValidationProvider } from 'vee-validate'
import Alert from '@/components/common/Alert'
import Confirm from '@/components/common/Confirm'

const SERVER_URL = process.env.VUE_APP_SERVER_URL

extend('required', {
  ...required,
  message: '필수 입력 항목입니다.'
})

extend('max', {
  ...max,
  params: ['length'],
  message: '{length}자 이하로 입력해주세요.'
})

export default {
  name: 'EditProfile',
  components: {
    Alert,
    Confirm,
    ValidationProvider,
    ValidationObserver,
  },
  data() {
    return {
      alert: {
        alerted: false,
        message: '',
        color: '',
      },
      confirm: {
        confirmed: false,
        message: '',
      },
      profileUserId: sessionStorage.getItem('uid'),
      loading: true,
      profileUserInfo: null,
      nickname: '',
      message: '',
      profileImageUrl: null,
      isFileChanged: false,
    }
  },
  computed: {
    getToken() {
      const token = sessionStorage.getItem('jwt')

      const config = {
        headers: {
          'X-Authorization-Firebase': token
        }
      }
      return config
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    // 데이터 초기화 메서드
    fetchData() {
      axios.get(`${SERVER_URL}/users/${this.profileUserId}/page`, this.getToken)
      .then(res => {
        // 현재 보고있는 프로필 페이지 유저의 정보 초기화
        this.profileUserInfo = res.data
        this.nickname = this.profileUserInfo.nickname
        this.message = this.profileUserInfo.message

        // 현재 로그인한 유저의 uid 초기화
        this.loginUserId = sessionStorage.getItem('uid')
        if (this.profileUserInfo.profileImage) {
          this.profileImageUrl = `${SERVER_URL}/images/${this.profileUserInfo.profileImage.path}`
        }
      })
      .then(() => {
        this.loading = false
      })
      .catch(err => {
        this.alert.message = err.message
        this.alert.alerted = true
      })
    },
    // 프로필 변경사항 서버 전달 메서드
    submit () {
      this.$refs.observer.validate()
      
      // 닉네임과 메세지 변경사항
      const params = {
        'nickname': this.nickname,
        'message': this.message
      }

      // 닉네임과 메세지 변경사항에 대한 부분먼저 보낸다
      axios.patch(`${SERVER_URL}/accounts/${this.profileUserId}`, params, this.getToken)
      .then(() => {
        // 현재 페이지에 선택되어있는 사진이 있고
        if (this.isFileChanged) {
          // put, post 요청 시 필요한 헤더를 config 변수에 할당
          const config = {
            headers: {
              'Content-type': 'multipart/form-data',
              'X-Authorization-Firebase': sessionStorage.getItem('jwt'),
            },
          }

          // 기존에 프로필 이미지가 있었던 유저라면
          if (this.profileUserInfo.profileImage) {
            axios.put(`${SERVER_URL}/images/${this.profileUserInfo.profileImage.path}`, this.profileImageFile, config)
            .then(() => {
              this.goProfile()
            })
            .catch(err => {
              this.alert.message = err.message
              this.alert.alerted = true
            })
          // 기존에 프로필 이미지가 없었던 유저라면
          } else {
            axios.post(`${SERVER_URL}/images/profile/${this.profileUserId}`, this.profileImageFile, config)
            .then(() => {
              this.goProfile()
            })
            .catch(err => {
              this.alert.message = err.message
              this.alert.alerted = true
            })
          }
        // 현재 페이지에 선택한 이미지가 없고
        } else {
          // 기존에 프로필 이미지가 있었던 유저라면
          if (this.profileUserInfo.profileImage && this.profileImageUrl === null) {
            axios.delete(`${SERVER_URL}/images/${this.profileUserInfo.profileImage.path}`, this.getToken)
            .then(() => {
              this.goProfile()
            })
            .catch(err => {
              this.alert.message = err.message
              this.alert.alerted = true
            })
          } else {
            // 프로필 사진은 바뀐것이 없을 경우!
            this.goProfile()
          }
        }
      })
      .catch(err => {
        this.alert.message = err.message
        this.alert.alerted = true
      })
    },
    // 사진 파일을 불러오는 버튼
    onPickFile () {
      this.$refs.fileInput.click()
    },
    // 사진 파일을 불러오는 버튼
    onFilePicked (event) {
      const imageFile = event.target.files[0] 

      if (imageFile && imageFile.size < 20971520) {
        this.profileImageUrl = URL.createObjectURL(imageFile)
        this.profileImageFile = new FormData()
        this.profileImageFile.append('profileImage', imageFile)
        this.isFileChanged = true
      } else if (imageFile) {
        this.alert.message = '파일 크기는 20MB를 넘길 수 없습니다.'
        this.alert.alerted = true
      }
    },
    // 프로필 이미지를 삭제하는 버튼
    onDeleteProfileImageButton () {
      if (this.profileImageUrl) {
        this.confirm.confirmed = true
        this.confirm.message = '프로필 이미지를 삭제하시겠습니까?'
      }
    },
    onDeleteConfirmed(confirmed) {
      if (confirmed) {
        this.profileImageUrl = null
        this.isFileChanged = false
        this.confirm.confirmed = false
      } else {
        this.confirm.confirmed = false
      }
    },
    goProfile() {
      this.$router.push({ 
        name: 'Profile', 
        params: { profileUserId : this.profileUserId }
      })
    }
  }
}
</script>

<style scoped>
/* 프로필 이미지 삭제 버튼의 위치를 설정한다 */
  .bottom-right-position {
    position: absolute; 
    bottom: 0px; 
    right: 0px;
  }

/* 컨테이너를 relative position으로 바꾼다. */
.relative-container {
  position: relative
}
</style>