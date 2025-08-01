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
                    { text: 'Introduzione', link: '${reportPath}/1-intro'},
                    { text: 'Requisiti', link: '${reportPath}/2-req' },
                    { text: 'Design Architetturale', link: '${reportPath}/3-design' },
                    { text: 'Design di dettaglio', link: '${reportPath}/4-design' },
                    { text: 'Implementazione', link: '${reportPath}/5-impl' },
                    { text: 'Testing', link: '${reportPath}/6-testing' },
                    { text: 'Conclusioni', link: '${reportPath}/7-end' }
                ]
            }
        ]
    }
}))
