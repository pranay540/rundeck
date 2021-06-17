import {RootStore} from './RootStore'
import {RundeckClient} from '@rundeck/client'
import { action, computed, flow, observable } from 'mobx'

import { Serial } from '../utilities/Async'

import {ServiceType} from './Plugins'

export class WebhookStore {
    @observable webhooks: Array<Plugin> = []

    @observable loaded = new Map<string, boolean>()

    constructor(readonly root: RootStore, readonly client: RundeckClient) {}

    @action
    async load(project: string): Promise<void> {
        console.log('Loading')
        if (this.loaded.get(project))
            return

        const resp = await this.client.sendRequest({
            baseUrl: 'http://localhost:8080/api/32',
            method: 'GET',
            pathTemplate: 'project/{project}/webhooks',
            pathParameters: {
                project: project
            }
        })

        console.log('Loading')

        await this.root.plugins.load(ServiceType.WebhookEvent)

        console.log('Setting up ')

        this.webhooks = resp.parsedBody.map((i: any) => ({
            ...i,
            eventPlugin: this.root.plugins.getServicePlugins(ServiceType.WebhookEvent).find(p => p.artifactName == i.eventPlugin)
        }))

        console.log(this.webhooks)

        this.loaded.set(project, true)
    }
}


export interface Webhook {
    uuid: string
    enabled: boolean
    name: string
    creator: string
    config: any
    project: string
    roles: string
    user: string
    eventPlugin: Plugin
}