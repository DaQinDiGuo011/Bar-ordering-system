import { resolve } from 'path'
import { loadEnv } from 'vite'
import type { UserConfig, ConfigEnv } from 'vite'
import { createVitePlugins } from './build/vite'
import { include, exclude } from "./build/vite/optimize"
// 当前执行node命令时文件夹的地址(工作目录)
const root = process.cwd()

// 路径查找
function pathResolve(dir: string) {
  return resolve(root, '.', dir)
}

// https://vitejs.dev/config/
export default ({ command, mode }: ConfigEnv): UserConfig => {
  let env = {} as any
  const isBuild = command === 'build'
  if (!isBuild) {
    env = loadEnv((process.argv[3] === '--mode' ? process.argv[4] : process.argv[3]), root)
  } else {
    env = loadEnv(mode, root)
  }
  return {
    base: env.VITE_BASE_PATH,
    root: root,
    // 服务端渲染
    server: {
      port: env.VITE_PORT, // 端口号
      host: "127.0.0.1",
      open: env.VITE_OPEN === 'true',
      // 本地跨域代理. 目前注释的原因：暂时没有用途，server 端已经支持跨域
      proxy: {
        ['/system']: {
          target: 'http://localhost:48081/admin-api',
          ws: false,
          changeOrigin: true,
          rewrite: (path) => path.replace(new RegExp(`^/system`), '/system'),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('[Vite Proxy] 原始路径:', req.url);
              console.log('[Vite Proxy] 转发目标:', options.target + proxyReq.path);
            })
            proxy.on('error', (err) => {
              console.error('[Vite Proxy 异常]', err);
            })
          }
        },
        ['/store']: {
          target: 'http://localhost:48081/admin-api',
          ws: false,
          changeOrigin: true,
          // rewrite: (path) => path.replace(new RegExp(`^/`), '/'),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('[Vite Proxy] 原始路径:', req.url);
              console.log('[Vite Proxy] 转发目标:', options.target + proxyReq.path);
            })
            proxy.on('error', (err) => {
              console.error('[Vite Proxy 异常]', err);
            })
          }
        },
        ['/product']: {
          target: 'http://localhost:48081/admin-api',
          ws: false,
          changeOrigin: true,
          // rewrite: (path) => path.replace(new RegExp(`^/`), '/'),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('[Vite Proxy] 原始路径:', req.url);
              console.log('[Vite Proxy] 转发目标:', options.target + proxyReq.path);
            })
            proxy.on('error', (err) => {
              console.error('[Vite Proxy 异常]', err);
            })
          }
        },
        ['/order']: {
          target: 'http://localhost:48081/admin-api',
          ws: false,
          changeOrigin: true,
          // rewrite: (path) => path.replace(new RegExp(`^/`), '/'),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('[Vite Proxy] 原始路径:', req.url);
              console.log('[Vite Proxy] 转发目标:', options.target + proxyReq.path);
            })
            proxy.on('error', (err) => {
              console.error('[Vite Proxy 异常]', err);
            })
          }
        },
        ['/shop']: {
          target: 'http://localhost:48081/admin-api',
          ws: false,
          changeOrigin: true,
          // rewrite: (path) => path.replace(new RegExp(`^/`), '/'),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('[Vite Proxy] 原始路径:', req.url);
              console.log('[Vite Proxy] 转发目标:', options.target + proxyReq.path);
            })
            proxy.on('error', (err) => {
              console.error('[Vite Proxy 异常]', err);
            })
          }
        },
        ['/member']: {
          target: 'http://localhost:48081/admin-api',
          ws: false,
          changeOrigin: true,
          // rewrite: (path) => path.replace(new RegExp(`^/`), '/'),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('[Vite Proxy] 原始路径:', req.url);
              console.log('[Vite Proxy] 转发目标:', options.target + proxyReq.path);
            })
            proxy.on('error', (err) => {
              console.error('[Vite Proxy 异常]', err);
            })
          }
        },
        ['/coupon']: {
          target: 'http://localhost:48081/admin-api',
          ws: false,
          changeOrigin: true,
          // rewrite: (path) => path.replace(new RegExp(`^/`), '/'),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('[Vite Proxy] 原始路径:', req.url);
              console.log('[Vite Proxy] 转发目标:', options.target + proxyReq.path);
            })
            proxy.on('error', (err) => {
              console.error('[Vite Proxy 异常]', err);
            })
          }
        }, 
        ['/infra/job']: {
          target: 'http://localhost:48081/admin-api',
          ws: false,
          changeOrigin: true,
          // rewrite: (path) => path.replace(new RegExp(`^/`), '/'),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('[Vite Proxy] 原始路径:', req.url);
              console.log('[Vite Proxy] 转发目标:', options.target + proxyReq.path);
            })
            proxy.on('error', (err) => {
              console.error('[Vite Proxy 异常]', err);
            })
          }
        }, 
        '/infra/ws': {
          target: 'http://127.0.0.1:48081',
          ws: true, // ✅开启websocket代理，核心！
          changeOrigin: true,
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req) => {
              console.log('[WS代理]原始url:', req.url);
            })
            proxy.on('close', () => console.log('[WS代理]代理链路关闭'))
            proxy.on('error', (err) => console.error('[WS代理异常]', err))
          }
        },
        ['/file/']: {
           target: 'http://localhost:48081',
          changeOrigin: true,
          ws: false
        },
      },
    },
    // 项目使用的vite插件。 单独提取到build/vite/plugin中管理
    plugins: createVitePlugins(),
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '@import "./src/styles/variables.scss";',
          javascriptEnabled: true,
          api: 'modern-compiler',
          silenceDeprecations: ['legacy-js-api', 'import']
        }
      }
    },
    resolve: {
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.scss', '.css'],
      alias: [
        {
          find: 'vue-i18n',
          replacement: 'vue-i18n/dist/vue-i18n.cjs.js'
        },
        {
          find: /\@\//,
          replacement: `${pathResolve('src')}/`
        },
        {
          find: 'element-plus/es/components/input-tag/style/css',
          replacement: 'virtual:empty-module'
        },
        {
          find: 'element-plus/es/components/mention/style/css',
          replacement: 'virtual:empty-module'
        }
      ]
    },
    build: {
      minify: 'terser',
      outDir: env.VITE_OUT_DIR || 'dist',
      sourcemap: env.VITE_SOURCEMAP === 'true' ? 'inline' : false,
      // brotliSize: false,
      terserOptions: {
        compress: {
          drop_debugger: env.VITE_DROP_DEBUGGER === 'true',
          drop_console: env.VITE_DROP_CONSOLE === 'true'
        }
      }
    },
    optimizeDeps: { include, exclude }
  }
}
