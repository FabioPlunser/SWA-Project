import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'
import { resolve } from 'path'

export default defineConfig({
  plugins: [svelte()],
  root: "./src/",
  build: {
    outDir: "../../swa/src/main/resources/static",
    rollupOptions: {
      input: {
        main: resolve(__dirname, "src", "index.html"),
        login: resolve(__dirname, "src", "login", "index.html"),
        error: resolve(__dirname, "src", "error", "index.html"),
        learn: resolve(__dirname, "src", "learn", "index.html"),
        cards: resolve(__dirname, "src", "listCards", "index.html"),

      }
    }
  }
})
