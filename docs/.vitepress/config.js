import { defineConfig } from 'vitepress'
import { withMermaid } from "vitepress-plugin-mermaid";

let reportPath = '/report'

export default withMermaid(
    defineConfig({
    ignoreDeadLinks: true,
    base: '/pps-24-psim/relazione/',
    title: 'Relazione progetto',
    description: 'Relazione del progetto PlagueSim',
    themeConfig: {
        nav: [
            { text: 'Home', link: '/' },
            { text: 'Indice', link: '/index' }
        ],
        sidebar: [
            {
                text: 'Contenuti',
                items: [
                    { text: 'Introduzione', link: '${reportPath}/1-intro' }
                ]
            }
        ]
    }
}))
