<template>
  <v-row
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
      <validation-observer
        ref="observer"
        v-slot="{ invalid }"
      >
        <form @submit.prevent="submit">
          <validation-provider
            mode="lazy"
            rules="required|currentPassword"
            v-slot="{ errors }"
          >
            <v-text-field
              v-model="currentPassword"
              :error-messages="errors"
              label="현재 비밀번호"
              :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
              :type="showPassword ? 'text' : 'password'"
              @click:append="showPassword = !showPassword"
            ></v-text-field>
          </validation-provider>
          <validation-provider
            mode="aggressive"
            name="password"
            rules="required|min:8"
            v-slot="{ errors }"
          >
            <v-text-field
              v-model="password"
              :error-messages="errors"
              label="비밀번호"
              placeholder="비밀번호를 입력해주세요."
              :type="showPassword ? 'text' : 'password'"
            ></v-text-field>
          </validation-provider>
          <validation-provider
            mode="aggressive"
            rules="required|min:8|password:@password"
            v-slot="{ errors }"
          >
            <v-text-field
              v-model="passwordConfirm"
              :error-messages="errors"
              label="비밀번호 확인"
              placeholder="비밀번호를 다시한번 입력하세요."
              :type="showPassword ? 'text' : 'password'"
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
    </v-col>
  </v-row>
</template>

<script>
  import firebase from 'firebase/app'
  import { required, min } from 'vee-validate/dist/rules'
  import { extend, ValidationObserver, ValidationProvider } from 'vee-validate'
  import Alert from '@/components/common/Alert'



  // 유효성 검사 규칙 커스터마이징
  extend('currentPassword', { // 비밀번호 변경을 위해 로그인한 유저의 재인증을 위한 현재 비밀번호 검사
    validate: async value => { // promise 요청으로 유효성을 검사하기 때문에 promise 함수를 동기식으로 바꿔줘야 올바른 결과값을 얻는다
      const user = firebase.auth().currentUser
      var flag = false

      if (user) {
        // 재인증을 위한 인증 param, 현재 유저가 로그인상태이고 해당 유저의 이메일과 입력값으로 받은 현재 비밀번호가 일치하면        
        const credential = firebase.auth.EmailAuthProvider.credential(
            user.email, 
            value
        )
        
        await user.reauthenticateWithCredential(credential)
        .then(() => {
          flag = true
        })
        .catch(err => {
          console.log(err)
        })
      }

      return flag
    },
    message: '현재 비밀번호와 다릅니다.'
  });

  extend('password', { // 비밀번호, 비밀번호 확인 입력 값 일치 검사
    params: ['target'],
    validate(value, { target }) {
      return value === target;
    },
    message: '비밀번호가 일치하지 않습니다.'
  });

  extend('required', {
    ...required,
    message: '필수 입력 항목입니다.'
  })

  extend('min', {
    ...min,
    message: '비밀번호는 8글자 이상이어야 합니다.'
  })
  // 유효성 검사 규칙 커스터마이징 끝

  export default {
    name: 'ChangePassword',
    components: {
      Alert,
      ValidationProvider,
      ValidationObserver,
    },
    data: () => ({
      alert: {
        alerted: false,
        message: '',
        color: '',
      },
      currentPassword: '',
      password: '',
      passwordConfirm: '',
      showPassword: false,
    }),
    methods: {
      submit () { // submit 버튼 클릭 시 발생 이벤트
        this.alert.alerted = false
        this.$refs.observer.validate()

        var user = firebase.auth().currentUser;
        const self = this

        user.updatePassword(self.password)
        .then(() => {
          this.alert.message = '비밀번호 변경이 완료되었습니다.'
          this.alert.color = 'primary'
          this.alert.alerted = true
          setTimeout(() => self.onLogout(), 2000)
        })
        .catch(err => {
          this.alert.message = err.message
          this.alert.alerted = true
        })
      },
      onLogout() {
        sessionStorage.removeItem('jwt')
        this.$router.push({ name: 'Login' })
      },
    },
  }
</script>

<style>

</style>