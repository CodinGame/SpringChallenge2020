import * as utils from '../core/utils.js'



export class ShockAnimator {
  constructor(event, layer, module) {
    this.time = 0;
    this.duration = 600
    this.layer = layer
    this.position = {
      x: event.params.x,
      y: event.params.y
    }
    this.filter = module.getAnimFromPool('shockwaveFilter')
    this.module = module
  }

  get target() {
    return this.layer.parent.parent
  }

  animate(delta) {
    if (this.target.parent != null) {
      // Running in the intro replay
      this.time = this.duration + 1
      this.filter.busyp = false
      return
    }

    const speed = this.module.speed || 10
    this.time += delta * speed;
    const progress = utils.unlerp(0, this.duration, this.time)

    if (this.target.filters == null) {
      this.target.filters = []
    }
    if (!this.target.filters.includes(this.filter)) {
      this.target.filters = [...this.target.filters, this.filter]
    }
    this.filter.time = progress
    this.filter.amplitude = 15 * (1 - progress)
    this.filter.speed = 130
    this.filter.wavelength = 160 / 4

    this.filter.center[0] = this.position.x * this.target.scale.x
    this.filter.center[1] = this.position.y * this.target.scale.y


    if (this.time <= this.duration) {
      this.filter.busyp = false
    }
  }

  isActive() {
    return this.time <= this.duration
  }
}