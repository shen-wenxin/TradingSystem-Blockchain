import store from '../../store'

let GlobalMessage = {
  success(text) {
    store.dispatch('GlobalMsgbar/openSnackbar', {
      'msg': text,
      'color': 'success',
    })
  },
  info(text) {
    store.dispatch('GlobalMsgbar/openSnackbar', {
      'msg': text,
      'color': 'info',
    })
  },
  error(text) {
    store.dispatch('GlobalMsgbar/openSnackbar', {
      'msg': text,
      'color': 'error',
    })
  },
  warning(text) {
    store.dispatch('GlobalMsgbar/openSnackbar', {
      'msg': text,
      'color': 'warning',
    })
  },
  custom(text, color) {
    store.dispatch('GlobalMsgbar/openSnackbar', {
      'msg': text,
      'color': color,
    })
  }
}

export default GlobalMessage;