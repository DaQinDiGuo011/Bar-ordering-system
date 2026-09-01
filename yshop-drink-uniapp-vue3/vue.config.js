// module.exports = {
//   devServer: {
//     proxy: {
//       '/app-api': {
//         target: 'https://theonebar-dwjg.com',
//         changeOrigin: true,
//       },
//     },
//   },
// }

export default defineConfig({
  server:{
    hmr:{
      overlay: false
    }
  }
})