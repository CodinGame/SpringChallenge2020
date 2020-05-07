import { WIDTH, HEIGHT } from '../core/constants.js'
import { api as entityModule } from '../entity-module/GraphicEntityModule.js'

/* global PIXI */

function getSpriteMouseMoveFunc(entity, tooltip) {
  return function (event) {
    if (entity.graphics.containsPoint(event.data.global)) {
      tooltip.inside[entity.id] = true
    } else {
      delete tooltip.inside[entity.id]
    }
  }
}

function getEntityCurrentSubStates(entity, frame) {
  if (entity.states[frame]) {
    return entity.states[frame]
  }
  let frameNumbers = Object.keys(entity.states)
  let index = frameNumbers.length - 1

  while (index >= 0 && frameNumbers[index] > frame) {
    index--
  }
  return entity.states[frameNumbers[index]] || []
}

function getEntityState(entity, frame) {
  const subStates = getEntityCurrentSubStates(entity, frame)
  if (subStates && subStates.length) {
    return subStates[subStates.length - 1]
  }
  return null
}

function convertPosition(pos, globalData) {
  return {
    x: (pos.x - globalData.x) / (globalData.coefficient * globalData.cellSize),
    y: (pos.y - globalData.y) / (globalData.coefficient * globalData.cellSize)
  }
}

function getMouseMoveFunc(tooltip, container, module) {
  return function (ev) {
    if (tooltip) {
      var pos = ev.data.getLocalPosition(container)
      tooltip.x = pos.x
      tooltip.y = pos.y

      const showing = []
      const ids = Object.keys(tooltip.inside).map(n => +n)

      module.interactive.forEach(id => {
        const entity = entityModule.entities.get(id)

        if (entity && entity.container != null) {
          entity.container.interactive = true
          entity.container.mousemove = getSpriteMouseMoveFunc(entity, tooltip)
        }
      })

      for (let id of ids) {
        if (tooltip.inside[id]) {
          const entity = entityModule.entities.get(id)
          const state = entity && getEntityState(entity, module.currentFrame.number)
          if (!state) {
            delete tooltip.inside[id]
          } else {
            showing.push(id)
          }
        }
      }


      const tooltipBlocks = []

      if (showing.length) {
        for (let show of showing) {
          const entity = entityModule.entities.get(show)
          const state = getEntityState(entity, module.currentFrame.number)
          if (state !== null) {
            const text = module.currentFrame.registered[show]
            if (text && text.length && String(text).valueOf() !== '0') {
              tooltipBlocks.push(text)
            }
          }
        }
      }

      const point = convertPosition(pos, module.globalData)
      if (point.y >= 0 && point.x >= 0 && point.x < module.globalData.width && point.y < module.globalData.height) {
        const x = Math.floor(point.x)
        const y = Math.floor(point.y)

        let tooltipBlock = 'x: ' + x + '\ny: ' + y
        tooltipBlocks.push(tooltipBlock)
      }


      tooltip.visible = tooltipBlocks.length
      if (tooltip.visible) {
        tooltip.label.text = tooltipBlocks.join('\n──────────\n')
      }


      tooltip.background.width = tooltip.label.width + 20
      tooltip.background.height = tooltip.label.height + 20

      tooltip.pivot.x = -30
      tooltip.pivot.y = -50

      if (tooltip.y - tooltip.pivot.y + tooltip.height > HEIGHT) {
        tooltip.pivot.y = 10 + tooltip.height
        tooltip.y -= tooltip.y - tooltip.pivot.y + tooltip.height - HEIGHT
      }

      if (tooltip.x - tooltip.pivot.x + tooltip.width > WIDTH) {
        tooltip.pivot.x = tooltip.width
      }
    }
  }
};

export class TooltipModule {
  constructor(assets) {
    this.interactive = []
    this.previousFrame = {
      registered: {}
    }
    this.lastProgress = 1
    this.lastFrame = 0
  }

  static get name() {
    return 'tooltips'
  }

  updateScene(previousData, currentData, progress) {
    this.currentFrame = currentData
    this.currentProgress = progress
  }

  handleFrameData(frameInfo, data = []) {
    const newRegistration = data[0] || []
    const registered = { ...this.previousFrame.registered, ...newRegistration }

    Object.keys(newRegistration).forEach(
      k => {
        this.interactive.push(+k)
      }
    )

    const frame = { registered, number: frameInfo.number }
    this.previousFrame = frame
    return frame
  }

  reinitScene(container) {
    if (!this.globalData) {
      return
    }
    this.tooltip = this.initTooltip()
    this.container = container
    container.interactive = true
    container.mousemove = getMouseMoveFunc(this.tooltip, container, this)
    container.addChild(this.tooltip)
  }

  handleGlobalData(players, globalData) {
    this.globalData = globalData
  }

  generateText(text, size, color, align) {
    var textEl = new PIXI.Text(text, {
      fontSize: Math.round(size / 1.2) + 'px',
      fontFamily: 'Lato',
      fontWeight: 'bold',
      fill: color
    })

    textEl.lineHeight = Math.round(size / 1.2)
    if (align === 'right') {
      textEl.anchor.x = 1
    } else if (align === 'center') {
      textEl.anchor.x = 0.5
    }

    return textEl
  };

  initTooltip() {
    var tooltip = new PIXI.Container()
    var background = tooltip.background = new PIXI.Graphics()
    var label = tooltip.label = this.generateText('', 36, 0xFFFFFF, 'left')

    background.beginFill(0x0, 0.7)
    background.drawRect(0, 0, 200, 185)
    background.endFill()
    background.x = -10
    background.y = -10

    tooltip.visible = false
    tooltip.inside = {}

    tooltip.addChild(background)
    tooltip.addChild(label)

    tooltip.interactiveChildren = false
    return tooltip
  };
}
