function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function sendMsgApi(data) {
    return $axios({
        'url' : '/user/sendMsg',
        'method' : 'post',
        data
    })

}


function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

/*
// 提交手机号，获取验证码
const postPhoneNumber = (phone) => {
    return $axios({
        url: `/user/postPhoneNumber`,
        method: 'post',
        params: { phone }
    })
}*/
