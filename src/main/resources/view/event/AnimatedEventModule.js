import * as utils from '../core/utils.js';
import { WIDTH, HEIGHT } from '../core/constants.js';
import { FRAMES, ANCHORS } from './AnimData.js';
import { api as entityModule } from '../entity-module/GraphicEntityModule.js';
import { SplashAnimator } from './SplashAnimator.js'
import { ShockAnimator } from './ShockAnimator.js'
import { initFilters } from './filters/pixi-filters.js'

const ANIMATED_SPRITE = 'play'
const FLASH = 'flash'
const SHAKE = 'shake'
const SPEED = 'speed'
const SPLASH = 'splash'
const SHOCK = 'shock'

const DURATIONS = {
  [ANIMATED_SPRITE]: 0.8,
  [FLASH]: 0.3,
  [SPEED]: 2
}

export class AnimatedEventModule {
  constructor(assets) {

    initFilters()

    this.loadState = 1;
    this.animData = [];
    this.speedData = [];
    this.pool = {};
    this.frames = 0;
    this.currentData = { number: 0 };
    this.progress = 0;
    this.filterMap = {}
    this.pivotMap = {}

    this.asyncAnimators = {
      [SPLASH]: SplashAnimator,
      [SHOCK]: ShockAnimator
    };
    this.activeAnimators = [];
    this.speedFramesMap = {}

  }

  static get name() {
    return 'event';
  }

  updateScene(previousData, currentData, progress, speed) {
    this.currentData = currentData;
    this.progress = progress;
    this.speed = speed


    for (let key of Object.keys(this.filterMap)) {
      const { blur, color } = this.filterMap[key]
      blur.enabled = false
      color.enabled = false
    }

    for (let key of Object.keys(this.pivotMap)) {
      const entity = this.pivotMap[key]
      entity.container.pivot.set(0)
    }


    for (let key of Object.keys(this.speedFramesMap)) {
      for (let tab of this.speedFramesMap[key]) {
        for (let sprite of tab.animation) {
          sprite.sprite.visible = false
        }
      }
    }

    for (let data of this.animData) {
      let visible = false;
      let start = data.started.frame + data.started.t;
      let end = start + data.duration;
      let now = currentData.number + progress;

      if (start <= now && end > now) {
        visible = true;
      }

      if (data.id === ANIMATED_SPRITE) {
        if (!visible && data.sprite) {
          data.sprite.visible = false;
          data.sprite.busyp = false;
          data.sprite = null;
        } else if (visible && !data.sprite) {
          data.sprite = this.getAnimFromPool(data.params.name);
          data.sprite.visible = true;
        }

        if (visible) {
          if (this.loadState > 0) {
            const frameName = data.params.name

            let image = FRAMES[frameName][0];
            const animationProgress = utils.unlerp(start, end, now);
            const animationIndex = Math.floor(FRAMES[frameName].length * animationProgress);
            image = (FRAMES[frameName][animationIndex] || FRAMES[frameName][FRAMES[frameName].length - 1]);

            data.sprite.texture = PIXI.Texture.fromFrame(image);
          }
          if (data.params.x && data.params.y) {
            data.sprite.position.x = +data.params.x;
            data.sprite.position.y = +data.params.y;
          }
          if (data.params.name === 'charge') {
            const scale = 1 - utils.unlerp(0.85, 1, progress)
            data.sprite.scale.set(this.scale * scale * .4)
            const alpha = 1 - utils.unlerp(0.7, 1, progress)
            data.sprite.alpha = alpha
          }

          if (data.params.a != null) {
            data.sprite.rotation = data.params.a;
          }
        }
      } else if (data.id === FLASH) {
        const { blur, color } = this.filterMap[data.params.id]
        const pac = entityModule.entities.get(data.params.id)
        const animationProgress = utils.unlerp(start, end, now);

        if (visible) {
          color.enabled = true
          color.matrix = [1, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1 - animationProgress, 1, 0]
          blur.blur = 2 * (1 - animationProgress)
          pac.graphics.filters = [color]
        }
      } else if (data.id === SHAKE) {
        const pac = entityModule.entities.get(data.params.id)

        if (visible) {
          const animationProgress = utils.unlerp(start, end, now);
          this.shake(pac, animationProgress)
          this.pivotMap[data.params.id] = pac
        }
      } else if (data.id === SPEED) {
        const color = data.params.color
        const from = { x: data.params.fromX, y: data.params.fromY }
        const to = { x: data.params.toX, y: data.params.toY }
        const flip = data.params.walkFlip

        const speedboostGroup = entityModule.entities.get(this.speedboostGroupId)
        const numberOfMoments = 5

        if (!(data.params.id in this.speedFramesMap)) {
          this.speedFramesMap[data.params.id] = [this.createSpeedAnimation(numberOfMoments, from, to, color, speedboostGroup, flip)]
        } else {
          let element = this.speedFramesMap[data.params.id].find(element => element.from.x === from.x
            && element.from.y === from.y
            && element.to.x === to.x
            && element.to.y === to.y)
          if (element == null) {
            this.speedFramesMap[data.params.id].push(this.createSpeedAnimation(numberOfMoments, from, to, color, speedboostGroup, flip))
          }
        }
        if (visible) {
          const animationProgress = utils.unlerp(start, end, now)
          this.animateSpeed(data.params.id, animationProgress, from, to)
        }
      }
    }

    const frame = currentData.number;
    const prevFrame = previousData.number

    for (const event of currentData.events) {
      if (frame === this.lastFrame && event.t > this.lastProgress && event.t <= progress) {
        this.launchAsyncAnimation(event);
      } else if (this.lastFrame === prevFrame && event.t < progress) {
        this.launchAsyncAnimation(event);
      }
    }

    for (const event of previousData.events) {
      if (this.lastFrame == prevFrame && event.t > this.lastProgress) {
        this.launchAsyncAnimation(event);
      }
    }

    this.lastProgress = progress;
    this.lastFrame = frame;
  }

  shake(entity, progress) {
    let shakeForceMax = 2.8;
    let strength = 100000 * (Math.random() * 0.5 + 0.5);

    let shakeForce = shakeForceMax * utils.unlerp(0, 0.5, progress)
    let shakeX = shakeForce * Math.cos(2 * progress * strength);
    let shakeY = shakeForce * Math.sin(progress * strength);

    entity.container.pivot.x = shakeX;
    entity.container.pivot.y = shakeY;
  }

  createSpeedAnimation(numberOfMoments, from, to, color, speedboostGroup, flip) {
    return {
      animation: this.createSpeedAnimationSprites(
        numberOfMoments,
        from,
        to,
        color,
        speedboostGroup,
        flip
      ),
      from: from,
      to: to
    }
  }

  rotateSprite(sprite, from, to) {
    if (from.y > to.y) {
      sprite.rotation = Math.PI / 2
    } else if (from.y < to.y) {
      sprite.rotation = -Math.PI / 2
    } else if (from.x > to.x) {
      sprite.rotation = 0
    } else if (from.x < to.x) {
      sprite.rotation = Math.PI
    }
  }

  createSpeedAnimationSprites(numberOfMoments, from, to, color, speedboostGroup, flip) {
    let arr = []
    for (let i = 0; i < numberOfMoments; i++) {
      let sprite = new PIXI.Sprite(PIXI.Texture.fromFrame(`trail_head.png`))

      let step = i / (numberOfMoments - 1)
      sprite.position.copy(utils.lerpPosition(from, to, step))
      sprite.alpha = 0
      sprite.anchor.set(0.5)
      sprite.tint = color == 0 ? 0xE54348 : 0x2F8CF1

      if (flip) {
        this.rotateSprite(sprite, to, from)
      } else {
        this.rotateSprite(sprite, from, to)
      }
      arr.push({
        time: step / 2,
        sprite: sprite
      })
      speedboostGroup.container.addChild(arr[i].sprite)
    }
    return arr;
  }

  animateSpeed(entityId, animProgress, from, to) {
    let element = this.speedFramesMap[entityId].find(element => element.from.x === from.x
      && element.from.y === from.y
      && element.to.x === to.x
      && element.to.y === to.y)
    let fadeDuration = 0.50
    let elementSprites = element.animation
    for (let i = 0; i < elementSprites.length; i++) {
      let alphaProgress = utils.unlerp(elementSprites[i].time, elementSprites[i].time + fadeDuration, animProgress)
      if (elementSprites[i].time >= animProgress) {
        elementSprites[i].sprite.alpha = 0
      } else {
        elementSprites[i].sprite.scale.x = elementSprites[i].sprite.scale.y = 0.5
        elementSprites[i].sprite.alpha = 1 - alphaProgress
        elementSprites[i].sprite.visible = true;
      }
    }
  }

  parseEvent(raw) {
    const tokens = raw.split(' ')
    const t = +tokens[0]
    const id = tokens[1]
    let params = {}
    switch (id) {
      case SPEED: {
        params.color = +tokens[2]
        params.fromX = +tokens[3]
        params.fromY = +tokens[4]
        params.id = +tokens[5]
        params.toX = +tokens[6]
        params.toY = +tokens[7]
        params.walkEnd = +tokens[8]
        params.walkFlip = tokens[9] === '1'
        break
      }
      case SHAKE: {
        params.id = +tokens[2]
        params.length = +tokens[3]
        break
      }
      case FLASH: {
        params.id = +tokens[2]
        break
      }
      case ANIMATED_SPRITE: {
        if (tokens.length === 6) {
          params = {
            a: +tokens[2],
            name: tokens[3],
            x: +tokens[4],
            y: +tokens[5]
          }
        } else {
          params = {
            name: tokens[2],
            x: +tokens[3],
            y: +tokens[4]
          }
        }
        break
      }
      case SPLASH: {
        params.value = +tokens[2]
        params.x = +tokens[3]
        params.y = +tokens[4]
        break
      }
    }
    return {
      t, id, params
    }
  }

  handleFrameData(frameInfo, events = []) {
    const number = (frameInfo.number == 0) ? 0 : ++this.frames;
    const asyncEvents = []

    for (let rawEvent of events) {
      const event = this.parseEvent(rawEvent)
      if (event.id === ANIMATED_SPRITE) {
        event.started = { frame: number, t: event.t }
        event.duration = DURATIONS[event.params.name] || 1
        this.animData.push(event);

        if (event.params.name == 'charge') {
          asyncEvents.push({
            id: 'shock',
            t: 0.5,
            params: {
              x: event.params.x,
              y: event.params.y
            }
          })
        }

      } else if (event.id === FLASH) {
        event.started = { frame: number, t: event.t }
        event.duration = DURATIONS[event.id]
        this.animData.push(event);
        if (!(event.params.id in this.filterMap)) {
          const blur = new PIXI.filters.BlurFilter()
          const color = new PIXI.filters.ColorMatrixFilter()
          this.filterMap[event.params.id] = {
            blur,
            color
          }
          blur.blur = 2
          color.matrix = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0]

          blur.enabled = false
          color.enabled = false
        }
      } else if (event.id === SHAKE) {
        event.started = { frame: number, t: event.t }
        event.duration = +event.params.length
        this.animData.push(event);
      } else if (event.id === SPEED) {
        event.started = { frame: number, t: event.t }
        event.duration = DURATIONS[event.id] * (event.params.walkEnd - event.t)
        this.animData.push(event);
      } else if (event.id === SPLASH) {
        asyncEvents.push(event)
      }
    }
    return { number, events: asyncEvents };
  }

  getAnimFromPool(name) {
    if (this.pool[name] == null) {
      this.pool[name] = []
    }

    for (let a of this.pool[name]) {
      if (!a.busyp) {
        a.busyp = true;
        return a;
      }
    }

    let a = this.createAnim(name);
    this.pool[name].push(a);
    a.busyp = true;
    return a;
  };

  createAnim(name) {
    if (name === 'shockwaveFilter') {
      return new PIXI.filters.ShockwaveFilter()
    } else if (name === 'splashParticles') {
      const particles = []
      for (var p = 0; p < 20; ++p) {
        var particle = PIXI.Sprite.fromFrame('particle');
        particle.anchor.x = particle.anchor.y = 0.5;
        particle.angle = Math.random() * Math.PI * 2;
        particle.blendMode = PIXI.BLEND_MODES.ADD;
        particle.tint = 0x76a635 // pellet green
        particles.push(particle)
      }

      return particles
    } else {
      const sprite = new PIXI.Sprite(PIXI.Texture.EMPTY);
      if (ANCHORS[name]) {
        sprite.anchor.copy(ANCHORS[name]);
      }
      sprite.blendMode = PIXI.BLEND_MODES.ADD;
      sprite.alpha = 0.9
      this.container.addChild(sprite);
      if (name === 'choc') {
        sprite.scale.set(this.scale * 0.8)
      } else {
        sprite.scale.set(this.scale)
      }
      return sprite;
    }
  }

  reinitScene(container) {
    this.container = container;
    for (let a of this.animData) {
      a.sprite = null;
    }
    for (let key in this.pool) {
      this.pool[key] = [];
    }
    this.pivotMap = {}
    this.speedFramesMap = {}
    this.activeAnimators = [];

    this.plasmaBalls = []
    this.generatePlasmaBalls()
  }

  generatePlasmaBalls() {

    for (let id of this.plasmaBallIds) {
      const entity = entityModule.entities.get(id)
      const plasmaBall = PIXI.extras.AnimatedSprite.fromFrames(FRAMES.plasma)
      plasmaBall.loop = true
      plasmaBall.gotoAndPlay(Math.random() * FRAMES.plasma.length)
      plasmaBall.blendMode = PIXI.BLEND_MODES.ADD
      plasmaBall.width = 60
      plasmaBall.height = 60
      plasmaBall.anchor.set(0.5)
      entity.container.addChild(plasmaBall)
    }
  }

  animateScene(delta) {
    for (const animatorData of this.activeAnimators) {
      animatorData.animator.animate(delta);
    }
    this.activeAnimators = this.activeAnimators.reduce((stillActive, animatorData) => {
      const animator = animatorData.animator;
      if (animator.isActive()) {
        return [...stillActive, animatorData];
      } else {
        animatorData.layer.parent.removeChild(animatorData.layer);
        return stillActive;
      }
    }, []);
  }

  handleGlobalData(players, [scale, speedboostGroupId, plasmaBallIds]) {
    this.scale = scale
    this.speedboostGroupId = speedboostGroupId
    this.plasmaBallIds = plasmaBallIds
  }


  launchAsyncAnimation(event) {
    const asyncAnimationsLimit = 10

    if (this.speed === 0 && event.id === SHOCK) {
      return
    }

    if (this.activeAnimators.length <= asyncAnimationsLimit) {
      if (this.asyncAnimators.hasOwnProperty(event.id)) {
        const layer = new PIXI.Container();
        const animator = new this.asyncAnimators[event.id](event, layer, this);
        this.activeAnimators.push({ animator, layer });
        this.container.addChild(layer);
      }
    }
  }

}