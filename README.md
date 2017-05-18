# Multi-Agent
>It is a solution of Design an intelligent Agent coursework in the university of Nottingham.<br/>
>Multiple cooperating agents must
collect waste from stations and dispose of it in wells. The goal of the agents is to
dispose of as much waste as possible in a fixed period of time.
## Task environment
<ul>
<li> the environment is an infinite 2D grid that contains randomly distributed
stations, wells and refuelling points</li>
<li> stations periodically generate tasks – requests to dispose of a specified amount
of waste</li>
<li> tasks persist until they are achieved (a station has at most one task at any time)</li>
<li> the maximum amount of waste that must be disposed of in a single task is
1,000 litres</li>
<li> wells can accept an infinite amount of waste</li>
<li> refuelling points contain an infinite amount of fuel</li>
<li> in each run, there is always a refuelling station in the centre of the
environment</li>
<li> a run lasts 10,000 timesteps</li>
<li> if a station is visible, the agent can see if it has a task, and if so, how much
waste is to be disposed of</li>
<li> the agent can take waste from a station and dispose of it in a well</li>
<li> moving around the environment requires fuel, which the agent must replenish
at a fuel station</li>
<li> the agent can carry a maximum of 100 litres of fuel and 1,000 litres of waste</li>
<li> the agent starts out in the centre of the environment (at the fuel station) with
100 litres of fuel and no waste</li>
<li> the agent moves at 1 cell / timestep and consumes 1 litre of fuel / cell</li>
<li> filling the fuel and waste tanks and delivering waste to a well takes one
timestep and no fuel</li>
<li> if the agent runs out of fuel, it can do nothing for the rest of the run</li>
<li> the success (score) of an age</li>
</ul>
