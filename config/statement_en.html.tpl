<!-- LEAGUES level1 level2 level3 level4 -->
<div id="statement_back" class="statement_back" style="display: none"></div>
<div class="statement-body">
  <!-- LEAGUE ALERT -->
  <!-- BEGIN level1 level2 level3 -->
  <div style="color: #7cc576; 
    background-color: rgba(124, 197, 118,.1);
    padding: 20px;
    margin-right: 15px;
    margin-left: 15px;
    margin-bottom: 10px;
    text-align: left;">
    <div style="text-align: center; margin-bottom: 6px">
      <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" />
    </div>
    <!-- BEGIN level1 -->
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      This is a <b>league based</b> challenge.
    </p>
    <span class="statement-league-alert-content">
      For this challenge, multiple leagues for the same game are available. Once you have proven your skills against the
      first Boss, you will access a higher league and extra rules will be available.
    </span>
    <!-- END -->
    <!-- BEGIN level2 -->
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      Summary of new rules
    </p>
    <span class="statement-league-alert-content">
      You can now control multiple Pacs!<br>
      <br>See the updated statement for details.</span>
    <!-- END -->
    <!-- BEGIN level3 -->
    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
      Summary of new rules
    </p>
    <span class="statement-league-alert-content">
      All the rules are now unlocked!
      <ul>
        <li>You can now give a speed boost to your pacs</li>
        <li>You can now switch their type to attack your opponent's pacs!</li>
        <li>You can only see pacs and pellets that are in the line of sight of your pacs</li>
      </ul>
      <br>See the updated statement for details.</span>
    <!-- END -->
  </div>
  <!-- END -->
  <!-- GOAL -->
  <div class="statement-section statement-goal">
    <h2>
      <span class="icon icon-goal">&nbsp;</span>
      <span>Goal</span>
    </h2>
    <div class="statement-goal-content">



      <div>
        Eat more pellets than your opponent!

        <!-- BEGIN level3 level4 -->
        And avoid getting killed!
        <!-- END -->

      </div>

      <div style="text-align: center; margin: 15px">

        <!-- BEGIN level1 -->
        <img src="https://static.codingame.com/servlet/fileservlet?id=43832304725167"
          alt="Eat more pellets than your opponent!" style="width: 60%" />
        <!-- END -->

        <!-- BEGIN level2 -->
        <img src="https://static.codingame.com/servlet/fileservlet?id=43832320304748"
          alt="Eat more pellets than your opponent!" style="width: 60%" />
        <!-- END -->

        <!-- BEGIN level3 level4 -->
        <img src="https://static.codingame.com/servlet/fileservlet?id=43832345828298"
          alt="Eat more pellets than your opponent!" style="width: 60%" />
        <!-- END -->
      </div>

    </div>
  </div>
  <!-- RULES -->
  <div class="statement-section statement-rules">
    <h2>
      <span class="icon icon-rules">&nbsp;</span>
      <span>Rules</span>
    </h2>

    <div class="statement-rules-content">
      <p>The game is played on a grid given to you at the start of each run. The grid is made up of walls and
        floors.<br><br>
        <!-- BEGIN level1 -->
        In this first league, each player controls a single pac that can move along the grid.
        <!-- END -->
        <!-- BEGIN level2 level3 level4 -->
        Each player controls a team of pacs that can move along the grid.
        <!-- END -->
      </p>
      <br>
      <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
        🗺️ The Map</h3>

      <p>The grid is generated randomly, and can vary in <var>width</var> and <var>height</var>.</p>
      <br>
      <p>Each cell of the map is either:
        <ul>
          <li>
            A wall (represented by a pound character: <action>#</action>)
          </li>
          <li>
            A floor (represented by a space character: <action>&nbsp;</action>)
          </li>
        </ul>

        <br>
        <p>Maps are always symetrical across the central vertical axis. Most grids will have floor tiles on the left and
          right edges, pacs can <strong>wrap around the map</strong> and appear on the other side by moving through
          these tiles.</p>
        <br>

        <p>
          When the game begins, the map is filled with <strong>pellets</strong> and the occasional
          <strong>super-pellet</strong>.
          <!-- BEGIN level1 -->
          Landing on a pellet with your <strong>pac</strong> scores you <const>1&nbsp;point</const>.
          <!-- END -->
          <!-- BEGIN level2 level3 level4 -->
          Landing on a pellet with one of your <strong>pacs</strong> scores you <const>1&nbsp;point</const>.
          <!-- END -->
        </p>
        Super-pellets are worth <const>10&nbsp;points</const>. The pellet is then removed.

        <div style="text-align: center; margin: 15px">

          <img src="https://static.codingame.com/servlet/fileservlet?id=43832401302931"
            style="width: 400px; max-width: 100%" />
          <div><em>A pellet is worth <const>1&nbsp;point</const> and a super-pellet is worth <const>10&nbsp;points
              </const>.</em></div>
        </div>


        <br>

        <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
          🔵🔴 The Pacs</h3>

        <!-- BEGIN level1 -->
        <p>Each player controls one pac. But in the next leagues you will control up to <const>5</const> pacs each!</p>
        <!-- END -->

        <!-- BEGIN level2 -->
        <div class="statement-new-league-rule">
          <!-- END -->

          <!-- BEGIN level2 level3 level4 -->
          <p>Each player starts with the same number of pacs, up to <const>5</const> each.</p>
          <!-- END -->
          <!-- BEGIN level2 -->
        </div>
        <!-- END -->

        <br>

        <!-- BEGIN level1 level2 -->
        <p>On each turn you have vision on all of the pellets and pacs in the grid (this will change in a future
          league).</p>
        <br>
        <!-- END -->

        <!-- BEGIN level3 -->
        <div class="statement-new-league-rule">
          <!-- END -->

          <!-- BEGIN level3 level4 -->
          <p>Your pacs <strong>cannot see through walls</strong>.
            On each turn you have vision on all of the pellets and enemy pacs that can be connected by a continuous
            straight line to any one of your pacs. Super-pellets are so bright that they can be seen from everywhere!

            <div style="text-align: center; margin: 15px">

              <img src="https://static.codingame.com/servlet/fileservlet?id=43832388706627"
                style="width: 400px; max-width: 100%" />
              <div><em>The line of sight of the pacs is blocked by the walls.</em></div>
            </div>

          </p>
          <br>
          <!-- END -->

          <!-- BEGIN level3 -->
        </div>
        <!-- END -->

        <p>At each turn, you are given information regarding the visible pacs and pellets. For each pac, you are given
          its identifier, whether it's yours or not and its coordinates. For each pellet, you are given its coordinates
          and value.</p>
        <br>

        <!-- BEGIN level3 -->
        <div class="statement-new-league-rule">
          <!-- END -->

          <!-- BEGIN level3 level4 -->
          <p>
            Each pac is of a given <var>type</var> (<action>ROCK</action>, <action>PAPER</action> or <action>SCISSORS
            </action>).
          </p>

          <p>Each pac has access to two <b>abilities</b> (<action>SWITCH</action> and <action>SPEED</action>) that share
            the same <b>cooldown period</b> of <const>10</const> turns. The abilities of a pac are already available at
            the start of the game.</p>
          <br>
          <!-- END -->

          <!-- BEGIN level3 -->
        </div>
        <!-- END -->

        <!-- BEGIN level1 -->
        <p>Pacs can receive the following command:</p>
        <!-- END -->

        <!-- BEGIN level2 level3 level4 -->
        <p>Pacs can receive the following commands:</p>
        <!-- END -->

        <ul>
          <li>
            <action>MOVE</action>: Give the pac a target position, the pac will find a shortest route to that position
            and <b>move the first step of the way</b>. The pac will not take into account the presence of pellets or
            other
            pacs when choosing a route.

            <div style="text-align: center; margin: 15px">
              <img src="https://static.codingame.com/servlet/fileservlet?id=43832368078694"
                style="width: 400px; max-width: 100%" />
              <div style="margin: auto; width: 400px; max-width: 100%"><em>Each pac that received a <action>MOVE
                  </action> order will move toward the target by going either up, down, left or right.</em></div>
            </div>
          </li>

          <!-- BEGIN level3 -->
        </ul>
        <div class="statement-new-league-rule">
          <ul>
            <!-- END -->

            <!-- BEGIN level3 level4 -->
            <li>
              <action>SWITCH</action>: it will morph into a new form. The available pac types are:
              <ul>
                <li>
                  <action>ROCK</action>
                </li>
                <li>
                  <action>PAPER</action>
                </li>
                <li>
                  <action>SCISSORS</action>
                </li>
              </ul>

              <div style="text-align: center; margin: 15px">

                <img src="https://static.codingame.com/servlet/fileservlet?id=43832448614162"
                  style="width: 400px; max-width: 100%" />
                <div style="margin: auto; width: 400px; max-width: 100%"><em>A <action>SWITCH</action> allows a pac to
                    change its type.</em></div>
              </div>

            </li>
            <li>
              <action>SPEED</action>: it will speed up for the next <const>5</const> turns,
              making it take the first <strong>2 steps</strong> along its path when moving. This means the pac can move
              twice as far as usual on each turn.

              <div style="text-align: center; margin: 15px">

                <img src="https://static.codingame.com/servlet/fileservlet?id=43832428009785"
                  style="width: 400px; max-width: 100%" />
                <div style="margin: auto; width: 400px; max-width: 100%"><em>A <action>SPEED</action> allows a pac to
                    move by 2 steps instead of 1 during the next 5 turns.</em></div>
              </div>
            </li>
            <!-- END -->
            <!-- BEGIN level3 -->
          </ul>
        </div>
        <ul>
          <!-- END -->
        </ul>
        <br>

        <p>See the <b>Game Protocol</b> section for more information on sending commands to your pacs.</p>
        <br>

        <!-- BEGIN level1 level2 -->
        <p>
          Crossing paths or landing on the same cell as another pac will cause a <b>collision</b> to occur. In that
          case, the movements of the colliding pacs are cancelled.
          <!-- BEGIN level2 -->
          <div class="statement-new-league-rule">
            Cancelling a movement can itself engender another collision that will be resolved the same way, until no new collisions occur.
          </div>
          <!-- END -->
        </p>
        <!-- END -->

        <!-- BEGIN level3 -->
        <div class="statement-new-league-rule">
          <!-- END -->

          <!-- BEGIN level3 level4 -->
          <p>
            Crossing paths or landing on the same cell as another pac will cause a <b>collision</b> to occur. This is how collisions are resolved:
          </p>
          <ol>
            <li>All moving pacs move <const>1</const> step, regardless of their speed.</li>
            <li>If the pacs are of the same type or belong to the same player, both pacs will go back to the cell they moved from. If the pacs are of different types, they can land on the same cell, but a pac can't cross the path of a stronger pac: it will be blocked.</li>
            <li>Cancelling a move may create new collisions. For this reason, the previous step is repeated until no new collisions are created.</li>
            <li>All pacs that share the same cell than a pac that can beat them are killed. <action>ROCK</action> beats <action>SCISSORS</action>, <action>SCISSORS</action> beats <action>PAPER</action> and <action>PAPER</action> beats <action>ROCK</action>.</li>
            <li>Repeat for any pac with an activated SPEED ability.</li>
          </ol>
          <br>
          <!-- END -->

          <!-- BEGIN level3 level4 -->
          <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
            🎬 Action order for one turn</h3>
          <ol>
            <li>Decrement cooldown timers</li>
            <li>Decrement <action>SPEED</action> duration timers</li>
            <li>Execute <b>abilities</b></li>
            <li>Resolve movement, including collisions</li>
            <li>Kill pacs that were beaten during a collision</li>
            <li>Eat pellets</li>
          </ol>
          <!-- END -->

          <!-- BEGIN level3 -->
        </div>
        <!-- END -->

        <br>
        <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
          ⛔ Game end</h3>

        <p>
          The game stops when there are no enough pellets in game to change the outcome of the game.<br>
          <br>
          The game stops automatically after <const>200 turns</const>.
          <br>

          <!-- BEGIN level3 -->
          <div class="statement-new-league-rule">
            <!-- END -->

            <!-- BEGIN level3 level4 -->
            <br>
            If all of a player's pacs are dead, all remaining pellets are automatically scored by any surviving pacs and
            the game is stopped.
            <!-- END -->

            <!-- BEGIN level3 -->
          </div>
          <!-- END -->

        </p>

        <!-- Victory conditions -->
        <div class="statement-victory-conditions">
          <div class="icon victory"></div>
          <div class="blk">
            <div class="title">Victory Conditions</div>
            <div class="text">
              <ul style="padding-top:0; padding-bottom: 0;">
                The winner is the player with the highest score, regardless of the amount of surviving pacs.
              </ul>
            </div>
          </div>
        </div>
        <!-- Lose conditions -->
        <div class="statement-lose-conditions">
          <div class="icon lose"></div>
          <div class="blk">
            <div class="title">Defeat Conditions</div>
            <div class="text">
              <ul style="padding-top:0; padding-bottom: 0;">
                Your program does not provide a command in the alloted time or one of the commands is invalid.
              </ul>
            </div>
          </div>
        </div>
        <br>
        <h3 style="font-size: 14px;
      font-weight: 700;
      padding-top: 5px;
      padding-bottom: 15px;">
          🐞 Debugging tips</h3>
        <ul>
          <li>Hover over the grid to see the coordinates of the cell under your mouse</li>
          <li>Hover over pacs to see information about them</li>
          <li>Append text after any command for a pac and that text will appear above that pac</li>
          <li>Press the gear icon on the viewer to access extra display options</li>
          <li>Use the keyboard to control the action: space to play/pause, arrows to step 1 frame at a time</li>
        </ul>




    </div>
    </div>

    <!-- BEGIN level3 level4 -->
    <!-- EXPERT RULES -->
    <div class="statement-section statement-expertrules">
      <h2>
        <span class="icon icon-expertrules">&nbsp;</span>
        <span>Technical Details</span>
      </h2>
      <div class="statement-expert-rules-content">
        <ul style="padding-left: 20px;padding-bottom: 0">
          <li>
            You can check out the source code of this game <a rel="nofollow" target="_blank"
            href="https://github.com/CodinGame/SpringChallenge2020">on this GitHub repo</a>.
          </li>
        </ul>
      </div>
    </div>
    <!-- END -->

  <!-- PROTOCOL -->
  <div class="statement-section statement-protocol">
    <h2>
      <span class="icon icon-protocol">&nbsp;</span>
      <span>Game Input</span>
    </h2>
    <!-- Protocol block -->
    <div class="blk">
      <div class="title">Initialization Input</div>
      <div class="text">
        <span class="statement-lineno">Line 1:</span> two integers <var>width</var> and <var>height</var> for the size
        of the map.<br>
        <span class="statement-lineno">Next <var>height</var> lines:</span> a string of <var>width</var> characters each
        representing one cell of this row: <const>' '</const> is a floor and <const>'#'</const> is a wall.
        <br>
      </div>
    </div>
    <!-- Protocol block -->
    <div class="blk">
      <div class="title">Input for One Game Turn</div>
      <div class="text">
        <span class="statement-lineno">First line:</span> Two space-separated integers: <br>
        <ul style="padding-left: 20px;padding-top:0;padding-bottom:0px">
          <li><var>myScore</var> your current score</li>
          <li><var>opponentScore</var> the score of your opponent</li>
        </ul>
        <span class="statement-lineno">Second line:</span> One integer: <br>
        <ul style="padding-left: 20px;padding-top:0;padding-bottom:0px">
          <li>
            <var>visiblePacCount</var> for the amount of pacs visible to you
          </li>
        </ul>
        <span class="statement-lineno">Next <var>visiblePacCount</var> lines:</span> Six integers: <br>
        <ul style="padding-left: 20px;padding-top:0;padding-bottom:0px">
          <li>
            <var>pacId</var>: the pac's unique id
          </li>
          <li>
            <var>mine</var>: the pac's owner (1 if this pac is yours, 0 otherwise)
          </li>
          <li>
            <var>x</var> & <var>y</var>: the pac's position
          </li>
          <!-- BEGIN level1 level2 -->
          <li>
            <var>typeId</var>: unused in this league
          </li>
          <li>
            <var>speedTurnsLeft</var>: unused in this league
          </li>
          <li>
            <var>abilityCooldown</var>: unused in this league
          </li>
          <!-- END -->


          <!-- BEGIN level3 -->
        </ul>
        <div class="statement-new-league-rule">
          <ul>
            <!-- END -->

            <!-- BEGIN level3 level4 -->
            <li>
              <var>typeId</var>: the pac's type (<action>ROCK</action> or <action>PAPER</action> or <action>SCISSORS
              </action>)
            </li>
            <li>
              <var>speedTurnsLeft</var>: the number of remaining turns before the speed effect fades
            </li>
            <li>
              <var>abilityCooldown</var>: the number of turns until you can request a new ability for this pac (<action>
                SWITCH</action> and <action>SPEED</action>)
            </li>
            <!-- END -->


            <!-- BEGIN level3 -->
        </div>
        <!-- END -->

        </ul>
        <span class="statement-lineno">Next line:</span> one integer
        <var>visiblePelletCount</var> for the amount of pellets visible to you</span><br>
        <span class="statement-lineno">Next <var>visiblePelletCount</var> lines:</span> three integers: <br>
        <ul style="padding-left: 20px;padding-top:0;padding-bottom:0px">
          <li>
            <var>x</var> & <var>y</var>: the pellet's position
          </li>
          <li>
            <var>value</var>: the pellet's score value
          </li>
        </ul>
      </div>
    </div>
    <!-- Protocol block -->
    <div class="blk">
      <div class="title">Output</div>
      <div class="text">
        <span class="statement-lineno">
          <!-- BEGIN level1 -->
          A single line with your action:
          <!-- END -->

          <!-- BEGIN level2 -->
          <div class="statement-new-league-rule">
            <!-- END -->

            <!-- BEGIN level2 level3 level4 -->
            A single line with one or multiple commands separated by <const>|</const>. For example: <action>MOVE 0 5 7 |
              MOVE 1 16 10</action>.
            <!-- END -->

            <!-- BEGIN level2 -->
          </div>
          <!-- END -->

          <ul style="padding-left: 20px;padding-top: 0">
            <li>
              <action>MOVE pacId x y</action>: the pac with the identifier <const>pacId</const> moves towards the given
              cell
              <!-- BEGIN level1 -->
              (<action>pacId</action> is always <const>0</const> in this league).
              <!-- END -->
            </li>


            <!-- BEGIN level3 -->
          </ul>
          <div class="statement-new-league-rule">
            <ul>
              <!-- END -->

              <!-- BEGIN level3 level4 -->
              <li>
                <action>SPEED pacId</action>: the pac will be able to move by 2 steps during the next 5 turns.
              </li>
              <li>
                <action>SWITCH pacId pacType</action>: the pac switches to the <var>pacType</var>.
              </li>
              <!-- END -->



            </ul>
            <!-- BEGIN level3 -->
          </div>
          <!-- END -->
      </div>
    </div>
    <div class="blk">
      <div class="title">Constraints</div>
      <div class="text">
        Response time per turn ≤
        <const>50</const>ms
        <br>Response time for the first turn ≤
        <const>1000</const>ms
      </div>
    </div>
  </div>

  <!-- BEGIN level1 level2 -->
  <!-- LEAGUE ALERT -->
  <div style="color: #7cc576; 
background-color: rgba(124, 197, 118,.1);
padding: 20px;

margin-top: 10px;
text-align: left;">
    <div style="text-align: center; margin-bottom: 6px"><img
        src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" /></div>

    <p style="text-align: center; font-weight: 700; margin-bottom: 6px;">What is in store for me in the higher leagues ?
    </p>
    The extra rules available in higher leagues are:<ul>
      <li>Pacs will be in rock, paper or scissor form.</li>
      <li>Pacs will be able to kill each other.</li>
      <li>Pacs will be able to swap their type or perform a speed boost.</li>
    </ul>
  </div>
  <!-- END -->

</div>