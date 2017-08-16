package edu.pdx.cs410J.jgraalum.client;


import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.i18n.client.DateTimeFormat;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import edu.pdx.cs410J.AirportNames;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic GWT class that makes sure that we can send an airline back from the server
 */
public class AirlineGwt implements EntryPoint {

    private final Alerter alerter;
    private final AirlineServiceAsync airlineService;
    private final Logger logger;
    private       DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM/dd/yyyy");


    private VerticalPanel airlineInfoPanel;
    private ListBox airlineListBox;

    private VerticalPanel flightTablePanel;

    private Button importAirlineXMLButton;
    private Button exportAirlineXMLButton;
    private Button addAirlineButton;
    private Button deleteAirlineButton;
    private Button deleteAllAirlineButton;


    private CellTable<Flight> flightTable;
    private HorizontalPanel flightDepartureFilterPanel;
    private HorizontalPanel flightArrivalFilterPanel;
    private VerticalPanel flightFilterPanel;
    private ListBox sourceAirportNamesDropBox;
    private ListBox destinationAirportNamesDropBox;

    private Button filterFlightsButton;
    private Button clearFilterButton;
    private Button addFlightButton;
    private Button helpButton;
    private Button printFlightsButton;
    private HorizontalPanel flightButtonPanel;


    static String newAirlineName = "";

    private flightSearchPopup flightSearchPopupWindow;
    private newAirlinePopup newAirlinePopupWindow;
    private newFlightPopup newFlightPopupWindow;

    private String sourceAirportFilterString;
    private String destinationAirportFilterString;
    private String departureDateFilterString;
    private String arrivalDateFilterString;

    private List<Flight> flightCellTableData;
    private ListDataProvider<Flight> flightDataProvider;
    private List<Flight> flightDataProviderList;

    private helpPopup helpPopupWindow;
    /**
     *
     */
    public AirlineGwt() {
        this(new Alerter() {
            @Override
            public void alert(String message) {
                Window.alert(message);
            }
        });
    }

    /**
     *
     */
    @Override
    public void onModuleLoad() {
        setUpUncaughtExceptionHandler();

        // The UncaughtExceptionHandler won't catch exceptions during module load
        // So, you have to set up the UI after module load...
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                setupUI();
            }
        });

    }

    /**
     *
     */
    private void setupUI() {

        // Create top level Dock Panel
        DockPanel dock = new DockPanel();
        dock.setStyleName("dockpanel2");
        dock.setSpacing(4);
        dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);

        // Create helpPopup window - will be "showed" on help button push
        helpPopupWindow = new helpPopup();


        sourceAirportFilterString = "";
        destinationAirportFilterString = "";
        arrivalDateFilterString = "";
        departureDateFilterString = "";


        // Create Airline Panel
        airlineInfoPanel = new VerticalPanel();
        airlineInfoPanel.setWidth("250px");
        airlineInfoPanel.setStyleName("airlinePanel");
        createAirlinePanel();
        updateAirlineListBox();
        dock.add(airlineInfoPanel, DockPanel.WEST);

        // Create Flight Panel with data table.
        createFlightCellTable();
        flightTablePanel = new VerticalPanel();
        flightTablePanel.setSize( "800px", "300px");
        flightTablePanel.add(flightTable);
        dock.add(flightTablePanel, DockPanel.CENTER);

        // Create Add Flight and Flight Search popups and Flight button panel
        newFlightPopupWindow = new newFlightPopup();
        createFlightButtonPanel();
        dock.add(flightButtonPanel, DockPanel.SOUTH);
        flightSearchPopupWindow = new flightSearchPopup();
        flightSearchPopupWindow.clear();

        dock.ensureDebugId("cwDockPanel");
        RootPanel rootPanel = RootPanel.get();
        rootPanel.add(dock);
    }

    /**
     * Creates PopUp Widget to display Help Text
     * Need to replace with a reference to the Help HTML file in the resources.
     */
    private class helpPopup extends PopupPanel {


        public helpPopup() {
            super(true);

            this.setSize("500px", "500px");
            VerticalPanel helpTextPanel = new VerticalPanel();
            helpTextPanel.setSize("500px", "500px");


            List<String> helpTextStrings = Arrays.asList(
                    "Airline Functions - Left side of the window",
                    "In the top left corner, there are three buttons:",
                    " ",
                    "Add - Brings up the Add Airline window",
                    "Delete - Deletes the Airline selected in the Airline Selection window",
                    "Delete All - Deletes all Airlines",
                    "",
                    "Below the buttons in the Airline Selection window.",
                    "Click an Airline name to display its flights.",
                    "",
                    "Flight Functions - Right side of the window",
                    "",
                    "The top window shows all of the Flight for the selected Airline.",
                    "This list is also filtered based as defined by the Flight Filters",
                    "",
                    "Below the Flight information window are the function buttons:",
                    "Add Flight - Brings up the Add Flight window",
                    "Flight Flights - Brings up the Filter window",
                    "Clear Filters - Removes all filters",
                    "Print Flights - Opens a Text Window with the selected flights",
                    "Help - Opens this window\n",
                    "Click outside of the help window to dismiss"
            );
            for (String line : helpTextStrings) {
                TextBox helpText = new TextBox();
                helpText.setText(line);
                helpText.setWidth("500px");
                helpTextPanel.add(helpText);
            }

            setWidget(helpTextPanel);
        }
    }

    /**
     *  Create Panel containing Airline Buttons and Airline List Box
     */
    private void createAirlinePanel() {

        // Create Buttons
        addAirlineButton = new Button("Add");
        addAirlineButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                new newAirlinePopup().show();
            }
        });

        deleteAirlineButton = new Button("Delete");
        deleteAirlineButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                String selectedAirline = airlineListBox.getSelectedItemText();
                if(selectedAirline != null)
                    deleteAirline(selectedAirline);
            }
        });

        deleteAllAirlineButton = new Button("Delete All");
        deleteAllAirlineButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                deleteAllAirline();
            }
        });

        HorizontalPanel addDeleteButtonPanel = new HorizontalPanel();
        addDeleteButtonPanel.add(addAirlineButton);
        addDeleteButtonPanel.add(deleteAirlineButton);
        addDeleteButtonPanel.add(deleteAllAirlineButton);
        airlineInfoPanel.add(addDeleteButtonPanel);

        // Create ListBox for displaying Airline Names
        airlineListBox = new ListBox();
        airlineListBox.ensureDebugId("airline-multiBox");
        airlineListBox.setMultipleSelect(false);
        airlineListBox.setVisibleItemCount(10);
        airlineListBox.setWidth("210px");
        airlineInfoPanel.add(airlineListBox);

        // If an item in the ListBox is Clicked, update the FlightCellTable
        airlineListBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                updateFlightCellTable();
            }
        });
    }

    /**
     * Method to create Popup Panel for adding new Airline
     */
    private class newAirlinePopup extends PopupPanel
    {
        public newAirlinePopup()
        {
            super(false);

            final Label   airlineNameLabel = new Label("New Airline Name");
            final TextBox airlineNameTextBox = new TextBox();
            airlineNameTextBox.ensureDebugId("newAirlineNameTextBox");

            final Button OKNewAirlineButton = new Button("OK");
            OKNewAirlineButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    newAirlineName = airlineNameTextBox.getValue();
                    newAirlinePopup.super.hide();
                    if(newAirlineName.length() > 0)
                        addAirline(newAirlineName);
                }
            });

            final Button CancelNewAirlineButton = new Button("Cancel");
            CancelNewAirlineButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    newAirlineName = null;
                    newAirlinePopup.super.hide();
                }
            });
            HorizontalPanel textPanel = new HorizontalPanel();
            HorizontalPanel buttonPanel = new HorizontalPanel();
            textPanel.add(airlineNameLabel);
            textPanel.add(airlineNameTextBox);
            buttonPanel.add(OKNewAirlineButton);
            buttonPanel.add(CancelNewAirlineButton);
            VerticalPanel topPanel = new VerticalPanel();
            topPanel.add(textPanel);
            topPanel.add(buttonPanel);
            setWidget(topPanel);
        }
    }

    /**
     * Method called by the Add Airline Button
     *
     * @param airlineName
     */
    private void addAirline(String airlineName) {
        logger.info("Calling addAirline with: " + airlineName);
        airlineService.addAirline(airlineName, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
                alertOnException(throwable);
            }
            @Override
            public void onSuccess(String name) {
                updateAirlineListBox();
            }
        });
    }

    /**
     * Method called by Delete Airline Button
     *
     * @param airlineName
     */
    private void deleteAirline(String airlineName) {
        logger.info("Calling deleteAirline with: " + airlineName);
        airlineService.deleteAirline(airlineName, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
                alertOnException(throwable);
            }
            @Override
            public void onSuccess(String name) {
                updateAirlineListBox();
            }
        });
    }

    /**
     * Method call by Delete All Airlines Button
     */
    private void deleteAllAirline() {
        logger.info("Calling deleteAllAirline");
        airlineService.deleteAllAirline(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                alertOnException(throwable);
            }
            @Override
            public void onSuccess(Void aVoid) {
                updateAirlineListBox();
            }
        });
    }

    /**
     * Update the Airline List Box with the latest list of Airlines
     * from the Server
     */
    private void updateAirlineListBox()
    {
        airlineService.getAirlineNames(new AsyncCallback<ArrayList<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
            }
            @Override
            public void onSuccess(ArrayList<String> airlineNames) {
                airlineListBox.clear();
                for(String name : airlineNames)
                    airlineListBox.addItem(name);
            }
        });
    }


    /**
     *  Create the Flight Cell Table.
     *
     *  The cellTable is not editable. It uses a ListDataProvider to update the
     *  rows of flights.
     *  The CellTable has columns for each of the Flight data fields - including the Airline name and Duration
     *
     *  The updateFlighCellTable method fills loads the data from the server.
     */
    private void createFlightCellTable()
    {

        flightTable = new CellTable<Flight>(KEY_PROVIDER);
        //flightTable.setSize("600px", "200px");
        flightTable.setWidth("100%",true);
        flightTable.setAutoHeaderRefreshDisabled(true);
        flightTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        flightTable.setEmptyTableWidget(new Label("No flights available"));

        // Airline Name Column
        TextColumn<Flight>  airlineNameColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return flight.getAirlineName();
            }
        };
        flightTable.addColumn(airlineNameColumn,"Airline");

        // Flight Number Column
        TextColumn<Flight>  flightNumberColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return String.valueOf(flight.getNumber());
            }
        };
        flightTable.addColumn(flightNumberColumn,"Number");

        // Source Airport Column
        TextColumn<Flight>  sourceAirportColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return flight.getSourceName() + " (" + flight.getSourceAirportCode() + ")";
            }
        };
        flightTable.addColumn(sourceAirportColumn,"Source Airport");

        // Departure Date
        TextColumn<Flight>  departureDateColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return flight.getDepartureDateString();
            }
        };
        flightTable.addColumn(departureDateColumn,"Departure Date");

        // Departure Time
        TextColumn<Flight>  departureTimeColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return flight.getDepartureTimeString();
            }
        };
        flightTable.addColumn(departureTimeColumn,"Departure Time");

        // Destination Airport Column
        TextColumn<Flight>  destinationAirportColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return flight.getDestinationName() + " (" + flight.getDestinationAirportCode() + ")";
            }
        };
        flightTable.addColumn(destinationAirportColumn,"Destination Airport");

        // Arrival Date
        TextColumn<Flight>  arrivalDateColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return flight.getArrivalDateString();
            }
        };
        flightTable.addColumn(arrivalDateColumn,"Departure Date");

        // Arrival Time
        TextColumn<Flight>  arrivalTimeColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return flight.getArrivalTimeString();
            }
        };
        flightTable.addColumn(arrivalTimeColumn,"Arrival Time");

        // Duration
        TextColumn<Flight>  flightDurationColumn = new TextColumn<Flight>() {
            @Override
            public String getValue(Flight flight) {
                return flight.getFlightDuration();
            }
        };
        flightTable.addColumn(flightDurationColumn,"Duration");


        final SingleSelectionModel<Flight> flightSelectionModel = new SingleSelectionModel<Flight>();
        flightTable.setSelectionModel(flightSelectionModel);
        flightSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                Flight selectedFlight = flightSelectionModel.getSelectedObject();
                //if(selectedFlight != null)
                    //Window.alert("Selected flight " + selectedFlight.getNumber());
            }
        });
        flightTable.setRowCount(10);

        flightDataProvider = new ListDataProvider<Flight>();
        flightDataProvider.addDataDisplay(flightTable);
        flightDataProviderList = flightDataProvider.getList();
    }

    /**
     * Simple method to provide sort Key in FlightCellTable.
     */
    private static final ProvidesKey<Flight> KEY_PROVIDER =
            new ProvidesKey<Flight>() {
                @Override
                public Object getKey(Flight item) {
                    return item.getNumber();
                }
            };

    /**
     * Method to create the Flight Button Panel - which sits below the FlightCellTable
     */
    private void createFlightButtonPanel()
    {
        flightButtonPanel = new HorizontalPanel();

        flightButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        addFlightButton = new Button("Add Flight");
        addFlightButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                newFlightPopupWindow.showPopup();
            }
        });
        flightButtonPanel.add(addFlightButton);

        filterFlightsButton = new Button("Filter Flights");
        filterFlightsButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(airlineListBox.getSelectedIndex() == -1)
                    Window.alert("Please select an airline to filter");
                else {
                    flightSearchPopupWindow.show();
                    updateFlightCellTable();
                }
            }
        });
        flightButtonPanel.add(filterFlightsButton);

        clearFilterButton = new Button("Clear Filters");
        clearFilterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                flightSearchPopupWindow.clear();
                updateFlightCellTable();
            }
        });
        flightButtonPanel.add(clearFilterButton);

        flightButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        printFlightsButton = new Button("Print Flights");
        printFlightsButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                printSelectedFlights();
            }
        });
        flightButtonPanel.add(printFlightsButton);

        helpButton = new Button("Help");
        helpButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                helpPopupWindow.show();
            }
        });
        flightButtonPanel.add(helpButton);
    }

    /**
     *  Popup Panel to Add new Flight - this is persistent - created only once
     *
     */
    private class newFlightPopup extends PopupPanel
    {
        final ListBox newFlightAirlineListBox;

        public newFlightPopup()
        {
            super(false);

            final Label   airlineNameLabel = new Label("Airline Name:");
            newFlightAirlineListBox = new ListBox();

            newFlightAirlineListBox.ensureDebugId("airline-dropBox");
            newFlightAirlineListBox.setMultipleSelect(false);
            for(int i = 0; i < airlineListBox.getItemCount(); i++)
                newFlightAirlineListBox.addItem(airlineListBox.getValue(i));

            HorizontalPanel airlineNameDropBoxPanel = new HorizontalPanel();
            airlineNameDropBoxPanel.add(airlineNameLabel);
            airlineNameDropBoxPanel.add(newFlightAirlineListBox);

            HorizontalPanel airlineNumberPanel = new HorizontalPanel();
            final TextBox flightNumberTextBox = new TextBox();
            final Label flightNumberLabel = new Label("Flight Number");
            airlineNumberPanel.add(flightNumberLabel);
            airlineNumberPanel.add(flightNumberTextBox);

            final AirlineDateTimePanel departDateTime = new AirlineDateTimePanel("addFlightDepart","Departure");
            final AirportNamesSelector sourceAirport = new AirportNamesSelector("Departure");
            final AirlineDateTimePanel arrivalDateTime = new AirlineDateTimePanel("addFlightArrival", "Arrival");
            final AirportNamesSelector destinationAirport = new AirportNamesSelector("Arrival");

            final Button OKNewFlightButton = new Button("OK");
            OKNewFlightButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {

                    String departureDateString = departDateTime.getDate();
                    String arrivalDateString = arrivalDateTime.getDate();
                    String departureTimeString = departDateTime.getTime();
                    String arrivalTimeString = arrivalDateTime.getTime();


                    logger.info("AirlineGWT - Adding new flight with fields: ");
                    logger.info(newFlightAirlineListBox.getSelectedItemText() + " " +
                            flightNumberTextBox.getText() + " " +
                            sourceAirport.getSelectedAirportCode() + " " +
                            departureDateString + " " + departureTimeString + " " +
                            destinationAirport.getSelectedAirportCode() + " " +
                            arrivalDateString + " " + arrivalTimeString);
                    addFlight(newFlightAirlineListBox.getSelectedItemText(),
                            flightNumberTextBox.getText(),
                            sourceAirport.getSelectedAirportCode(),
                            departureDateString,
                            departureTimeString,
                            destinationAirport.getSelectedAirportCode(),
                            arrivalDateString,
                            arrivalTimeString);


                    newFlightPopup.super.hide();
                }
            });

            final Button CancelNewFlightButton = new Button("Cancel");
            CancelNewFlightButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    newFlightPopup.super.hide();
                }
            });
            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.add(OKNewFlightButton);
            buttonPanel.add(CancelNewFlightButton);

            VerticalPanel topPanel = new VerticalPanel();
            //topPanel.setSize("200px","200px");
            topPanel.add(airlineNameDropBoxPanel);
            topPanel.add(airlineNumberPanel);
            topPanel.add(departDateTime);
            topPanel.add(sourceAirport);
            topPanel.add(arrivalDateTime);
            topPanel.add(destinationAirport);
            topPanel.add(buttonPanel);
            setWidget(topPanel);
        }

        // Need method to update the airline - as this is a persistent panel
        public void showPopup()
        {
            newFlightAirlineListBox.clear();
            for(int i = 0; i < airlineListBox.getItemCount(); i++)
                newFlightAirlineListBox.addItem(airlineListBox.getValue(i));
            super.show();
        }
    }

    /**
     *  Create a commonly used sub-panel with Date and Time fields
     *  This is a private class extending the AbsolutePanel class
     */
    private class AirlineDateTimePanel extends AbsolutePanel {

        private Label boxLabel;

        private Label dateLabel = new Label("Date:");
        private DateBox dateBox;
        private String dateString;

        private Label timeLabel = new Label("Time:");
        private TextBox timeBox;
        private RadioButton amButton;
        private RadioButton pmButton;
        private String timeValue= "12:00";
        private String groupName;
        boolean AM;
        boolean PM;

        public AirlineDateTimePanel(String group, String label) {

            super();
            groupName = group;
            boxLabel = new Label(label);

            DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM/dd/yyyy");
            dateBox = new DateBox();
            dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
            dateBox.getDatePicker().setYearArrowsVisible(true);
            dateBox.setValue(new Date());
            dateString = DateTimeFormat.getFormat("MM/dd/yyyy").format(dateBox.getValue());
            dateBox.setSize("80px","15px");

            dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(ValueChangeEvent<Date> event) {
                    Date date = event.getValue();
                    dateString = DateTimeFormat.getFormat("MM/dd/yyyy").format(date);
                }
            });

            timeBox = new TextBox();
            timeBox.setText(timeValue);
            timeBox.setSize("50px","15px");

            amButton = new RadioButton(groupName,"AM");
            pmButton = new RadioButton(groupName,"PM");
            amButton.setValue(true);

            timeBox.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    String newTime = event.getValue();
                    if(newTime.matches("1[0-2]:[0-5][0-9]") ||
                            newTime.matches("0[0-9]:[0-5][0-9]") ||
                            newTime.matches("[0-9]:[0-5][0-9]"))
                        timeValue = event.getValue();
                    else {
                        Window.alert("Invalid Time Format: " + newTime + "\nPlease specify time as: HH:MM");
                        timeBox.setValue(timeValue);
                    }
                }
            });

            amButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                   AM = true;
                   PM = false;
                }
            });

            pmButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    PM = true;
                    AM = false;
                }
            });
            this.setSize("250px","70px");
            this.add(boxLabel,0, 15);
            this.add(dateLabel, 60, 0);
            this.add(timeLabel, 60, 33);
            this.add(dateBox, 105, 7);
            this.add(timeBox, 105, 40);
            this.add(amButton, 170, 45);
            this.add(pmButton, 210, 45);
        }

        // Local methods to grab data from the fields.
        public String getDate()
        {
            return(dateString);
        }
        public String getTime()
        {
            return(timeValue + (this.AM ? " AM" : " PM"));
        }

    }

    /**
     * Commonly used Panel to build Airline Name selector
     */
    private class AirportNamesSelector extends AbsolutePanel
    {
        ListBox dropBox = new ListBox();
        Label selectorLabel;

        public AirportNamesSelector(String label) {
            super();
            selectorLabel = new Label(label + " Airport");

            Map<String, String> airportNamesMap = AirportNames.getNamesMap();
            this.dropBox.addItem("");

            for (Map.Entry<String, String> airport : airportNamesMap.entrySet()) {
                String airportItemString = airport.getValue() + "(" + airport.getKey() + ")";
                dropBox.addItem(airportItemString);
            }
            this.setSize("310px","70px");
            this.add(selectorLabel,100,0);
            this.add(dropBox,1,30);
        }

        public String getSelectedAirportCode()
        {
            if(this.dropBox.getSelectedIndex() != -1) {
                String airportName = this.dropBox.getSelectedItemText();
                return (airportName.substring(airportName.length() - 4, airportName.length() - 1));
            }
            else
                return null;
        }

        public void clear() {
            dropBox.setSelectedIndex(0);
        }
    }

    /**
     * Method to add Flights to airlineName via a server call
     * Following successful call, the flightTableCell is updated
     * The airline for the new flight is selected in the Airline DropBox list
     * before the flight data is updated.
     *
     * @param airlineName
     */
    private void addFlight(String airlineName, String flightNumber,
                           String departureAirportCode,
                           String departureDate,
                           String departureTime,
                           String arrivalAirportCode,
                           String arrivalDate,
                           String arrivalTime) {

        logger.info("AirlineGWT - Adding flight to airline: " + airlineName);
        logger.info("Flight details: " + flightNumber + " " +
                departureAirportCode + " " +
                departureDate + " " + departureTime + " " +
                arrivalAirportCode + " " +
                arrivalDate + " " + arrivalTime);

        airlineService.addFlight(airlineName,
                flightNumber,
                departureAirportCode,
                departureDate,
                departureTime,
                arrivalAirportCode,
                arrivalDate,
                arrivalTime,
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        alertOnException(throwable);
                    }
                    @Override
                    public void onSuccess(String name) {
                        logger.info("Updating Flight Table");
                        for(int i = 0; i < airlineListBox.getItemCount(); i++)
                        {
                            logger.info(name);
                            if(airlineListBox.getItemText(i).equals(name)) {
                                airlineListBox.setSelectedIndex(i);
                                logger.info("Selecting " + name + " " + i);
                            }
                        }
                        updateFlightCellTable();
                    }
                });

    }

    /**
     *  Defines new derived of PopupPanel to implement a Popup panel for applying
     *  search/filter parameters to the Fligh Cell Table.
     *  On a successful application of the filters, the cell table is updated.
     */
    private class flightSearchPopup extends PopupPanel {

        private final AirlineDatePanel departDate = new AirlineDatePanel("Departure");
        private final AirportNamesSelector sourceAirport = new AirportNamesSelector("Departure");
        private final AirlineDatePanel arrivalDate = new AirlineDatePanel("Arrival");
        private final AirportNamesSelector destinationAirport = new AirportNamesSelector("Arrival");

        public flightSearchPopup() {
            super(false);

            AbsolutePanel flightSearchPanel = new AbsolutePanel();

            sourceAirportNamesDropBox = new ListBox();
            destinationAirportNamesDropBox = new ListBox();

            Map<String, String> airportNamesMap = AirportNames.getNamesMap();
            sourceAirportNamesDropBox.addItem("");
            destinationAirportNamesDropBox.addItem("");
            for (Map.Entry<String, String> airport : airportNamesMap.entrySet()) {
                String airportItemString = airport.getValue() + "(" + airport.getKey() + ")";
                sourceAirportNamesDropBox.addItem(airportItemString);
                destinationAirportNamesDropBox.addItem(airportItemString);
            }

            sourceAirportNamesDropBox.ensureDebugId("AirlineGWT-FlightSearch-SourceAirport-DropBox");
            destinationAirportNamesDropBox.ensureDebugId("AirlineGWT-FlightSearch-DestinationAirport-DropBox");

            final Button OKFlightSearchButton = new Button("OK");
            OKFlightSearchButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    sourceAirportFilterString = sourceAirport.getSelectedAirportCode();
                    destinationAirportFilterString = destinationAirport.getSelectedAirportCode();
                    arrivalDateFilterString = arrivalDate.getDate().toString();
                    departureDateFilterString = departDate.getDate().toString();
                    updateFlightCellTable();
                    flightSearchPopup.super.hide();
                }
            });

            final Button CancelFlightSearchButton = new Button("Cancel");
            CancelFlightSearchButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    flightSearchPopup.super.hide();
                }
            });

            flightSearchPanel.setSize("350px", "250px");
            flightSearchPanel.add(new Label("Flight Filter"),95,0);
            flightSearchPanel.add(departDate,10,15);
            flightSearchPanel.add(sourceAirport,10,60);
            flightSearchPanel.add(arrivalDate,10, 105);
            flightSearchPanel.add(destinationAirport,10,150);
            flightSearchPanel.add(OKFlightSearchButton,40,220);
            flightSearchPanel.add(CancelFlightSearchButton,90,220);
            setWidget(flightSearchPanel);
        }

        public void clear()
        {
            sourceAirportFilterString = "";
            destinationAirportFilterString = "";
            arrivalDateFilterString = "";
            departureDateFilterString = "";
            departDate.clearDate();
            arrivalDate.clearDate();
            sourceAirport.clear();
            destinationAirport.clear();
        }

    }
    /**
     * Simplified version of the DateTime panel - only includes Date
     * Could probably combine with the DateTime panel - alas No Time <- That's a joke.
     */
    private class AirlineDatePanel extends AbsolutePanel {

        private Label dateBoxLabel;
        private DateBox dateBox;
        private String dateString;

        public AirlineDatePanel(String label) {

            super();
            dateBoxLabel = new Label(label + " Date:");

            DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM/dd/yyyy");
            dateBox = new DateBox();
            dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
            dateBox.getDatePicker().setYearArrowsVisible(true);
            dateBox.setValue(new Date());
            dateString = DateTimeFormat.getFormat("MM/dd/yyyy").format(dateBox.getValue());
            dateBox.setSize("80px","15px");

            dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(ValueChangeEvent<Date> event) {
                    Date date = event.getValue();
                    dateString = DateTimeFormat.getFormat("MM/dd/yyyy").format(date);
                }
            });

            this.setSize("220px", "50px");
            this.add(dateBoxLabel, 0, 18);
            this.add(dateBox, 105, 27);
        }

        public String getDate() {
            return (dateString);
        }

        public void clearDate()
        {
            dateString = "";
            dateBox.setValue(null);
        }

    }

    /**
     * Quick and dirty print of flight - removing as the FlightCellTable
     * provides the same functionality
     */
    public void printSelectedFlights()
    {
        ArrayList<Flight> flightsToPrint = new ArrayList<Flight>();

        PopupPanel printWindow = new PopupPanel();
        TextBox flightText = new TextBox();
        flightText.setText(flightTable.toString());
        printWindow.setWidget(flightText);
        printWindow.setAutoHideEnabled(true);
        printWindow.show();

    }
    /**
     * Method to update the FlightCellTable
     * First, clears out the data provide list <- must do this to avoid duplicates in the list.
     * Grabs the airline(with flights) from the Server
     * Checks if each flight meet search criteria. If yes, adds the flight to the data provider list.
     */
    private void updateFlightCellTable()
    {
        logger.info("Adding flights from " + airlineListBox.getSelectedItemText() );
        //flightDataProviderList = flightDataProvider.getList();
        for(Flight flight: flightDataProviderList)
            flightDataProviderList.remove(flight);
        flightDataProviderList.clear();
        flightDataProvider.flush();

        airlineService.getAirline(airlineListBox.getSelectedItemText(), new AsyncCallback<Airline>() {

            @Override
            public void onFailure(Throwable throwable) {
                alertOnException(throwable);
            }
            @Override
            public void onSuccess(Airline airline) {
                logger.info("Airline " + airline.getName() + " has " + airline.getFlights().size() + " flights");
                //flightDataProviderList = flightDataProvider.getList();
                for(Flight flight: airline.getFlights())
                {
                    logger.info("Checking flight number " + flight.getNumber());
                    if(checkFlightFilter(flight)) {
                        logger.info("Adding flight number " + flight.getNumber());
                        flightDataProviderList.add(flight);
                    }
                }
                flightDataProvider.flush();
                flightTable.setRowCount(5);
            }
        });
    }

    /**
     * Method that checks if a flight meets the filter/search criteria
     * @param flight
     * @return boolean - true if flight matches criteria.
     */
    private boolean checkFlightFilter(Flight flight) {
        String flightSourceCode = flight.getSourceAirportCode();
        String flightDestinationCode = flight.getDestinationAirportCode();
        String flightDepartureDate = flight.getDepartureDateString();
        String flightArrivalDate = flight.getArrivalDateString();

        logger.info("Checking codes: " + flightSourceCode + " " + flightDestinationCode);
        logger.info("Checking date: " + flightDepartureDate + " " + flightArrivalDate);
        logger.info("Checking filter: " + sourceAirportFilterString + " " + destinationAirportFilterString);
        logger.info("Checking filter: " + departureDateFilterString + " " + arrivalDateFilterString);

        return((sourceAirportFilterString.equals("") || sourceAirportFilterString.equals(flightSourceCode))           &&
                (destinationAirportFilterString.equals("") || destinationAirportFilterString.equals(flightDestinationCode)) &&
                (departureDateFilterString.equals("") || departureDateFilterString.equals(flightArrivalDate))          &&
                (arrivalDateFilterString.equals("") || arrivalDateFilterString.equals(flightDepartureDate))
        );
    }

    /*************************************/

    /**
     * Provided method to log exceptions
     *
     * @param alerter
     */
    @VisibleForTesting
    AirlineGwt(Alerter alerter) {
        this.alerter = alerter;
        this.airlineService = GWT.create(AirlineService.class);
        this.logger = Logger.getLogger("airline");
        Logger.getLogger("").setLevel(Level.INFO);  // Quiet down the default logging
    }

    /**
     * Provided method to alert on Exception
     *
     * @param throwable
     */
    private void alertOnException(Throwable throwable) {
        Throwable unwrapped = unwrapUmbrellaException(throwable);
        StringBuilder sb = new StringBuilder();
        sb.append(unwrapped.toString());
        sb.append('\n');
        Window.alert(sb.toString());
    }

    /**
     * Provided method to unwrap multiple exceptions
     *
     * @param throwable
     * @return
     */
    private Throwable unwrapUmbrellaException(Throwable throwable) {
        if (throwable instanceof UmbrellaException) {
            UmbrellaException umbrella = (UmbrellaException) throwable;
            if (umbrella.getCauses().size() == 1) {
                return unwrapUmbrellaException(umbrella.getCauses().iterator().next());
            }

        }
        return throwable;
    }

    /**
     * Provided method to catch exceptions
     */
    private void setUpUncaughtExceptionHandler() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable throwable) {
                alertOnException(throwable);
            }
        });
    }

    /**
     * Provided method to alert.
     */
    @VisibleForTesting
    interface Alerter {
        void alert(String message);
    }
}
