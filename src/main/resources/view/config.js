import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js'
import { ToggleModule } from './toggle-module/ToggleModule.js'
import { TooltipModule } from './tooltip-module/TooltipModule.js'
import { EndScreenModule } from './endscreen-module/EndScreenModule.js'
import {AnimatedEventModule} from './event/AnimatedEventModule.js'

export const modules = [
  GraphicEntityModule,
  AnimatedEventModule,
  ToggleModule,
  TooltipModule,
  EndScreenModule
]

export const playerColors = [
  '#ff1d5c', // radical red
  '#22a1e4', // curious blue
  '#de6ddf', // lavender pink
  '#6ac371', // mantis green
  '#9975e2', // medium purple
  '#3ac5ca', // scooter blue
  '#ff0000' // solid red
]
export const gameName = 'Spring2020'

export const stepByStepAnimateSpeed = 1

export const options = [
  ToggleModule.defineToggle({
    toggle: 'messageToggle',
    title: 'SHOW MESSAGES',
    values: {
      'ON': true,
      'OFF': false
    },
    default: true
  }),
]