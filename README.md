Electric Vehicle Charging Time Dependent Tariff
===============================================

FEATHERS and WIDRS
------------------

FEATHERS is a schedule predictor. It generates agendas for individuals before the start of the day. WIDRS is a schedule execution simulator that supports adaptation of partially executed schedules.

EVC-WIDRS
---------

New modules have been added to create EVC-WIDRS (EV charging WIDRS). It serves to simulate EV using individuals. They try to charge their batteries at the lowest possible cost. Travel times are calculated once; recalculation is not relevant in this context and has been dropped because it is very time consuming. The utility maximization mechanism using the same monotonically decreasing marginal utility functions, has been retained. The individual can buy electric energy at a time dependent cost that is communicated to all interested individuals at once (i.e. simultaneously) and once every 24[h].

Manuals
-------

Our software is compatible with Linux and Windows. For both operating systems, there is a manual.
