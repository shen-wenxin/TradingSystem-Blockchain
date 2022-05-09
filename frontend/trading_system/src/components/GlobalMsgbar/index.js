const GlobalMsgbar = {
    namespaced: true,
    state: {
        msg: '',
        visible: false,
        showClose: true,
        timeout: 3000, // Snackbar 弹出后的自动消失计时默认为3s
        color: ''
    },
    mutations: {
        OPEN_SNACKBAR(state, options) {
            state.visible = true;
            state.msg = options.msg;
            state.color = options.color;
        },
        CLOSE_SNACKBAR(state) {
            state.visible = false;
        }
    },
    actions: {
        openSnackbar(context, options) {
            let timeout = context.state.timeout;
            context.commit('OPEN_SNACKBAR', {
                msg: options.msg,
                color: options.color,
            });

            setTimeout(() => {
                context.commit('CLOSE_SNACKBAR')
            },timeout);
        }
    }

}

export default GlobalMsgbar;