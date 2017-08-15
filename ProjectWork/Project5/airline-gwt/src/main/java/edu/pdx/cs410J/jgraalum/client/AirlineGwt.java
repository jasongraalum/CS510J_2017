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
import com.google.gwt.user.datepicker.client.DatePicker;
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

    @VisibleForTesting
    private Button showAirlineButton;

    @VisibleForTesting
    private Button showUndeclaredExceptionButton;

    @VisibleForTesting
    private Button showDeclaredExceptionButton;

    @VisibleForTesting
    private Button showClientSideExceptionButton;

    private VerticalPanel airlineInfoPanel;
    private ListBox airlineListBox;

    private VerticalPanel flightTablePanel;

    private Button importAirlineXMLButton;
    private Button exportAirlineXMLButton;
    private Button addAirlineButton;
    private Button deleteAirlineButton;
    private Button deleteAllAirlineButton;


    private CellTable<Flight> flightTable;
    ArrayList<Flight> flightCellTableData;
    private HorizontalPanel flightDepartureFilterPanel;
    private HorizontalPanel flightArrivalFilterPanel;
    private VerticalPanel flightFilterPanel;
    private ListBox departureAirportNamesDropBox;
    private ListBox arrivalAirportNamesDropBox;


    private Button filterFlightsButton;
    private Button clearFlightsFilterButton;

    static String newAirlineName = "";

    //private  ArrayList<String> airlineNames;

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
     * @param alerter
     */
    @VisibleForTesting
    AirlineGwt(Alerter alerter) {
        this.alerter = alerter;
        this.airlineService = GWT.create(AirlineService.class);
        this.logger = Logger.getLogger("airline");
        Logger.getLogger("").setLevel(Level.INFO);  // Quiet down the default logging
        //this.airlineNames = new ArrayList<String>();
        //createTestAirlines();
    }

    /**
     *
     * @param throwable
     */
    private void alertOnException(Throwable throwable) {
        Throwable unwrapped = unwrapUmbrellaException(throwable);
        StringBuilder sb = new StringBuilder();
        sb.append(unwrapped.toString());
        sb.append('\n');

        for (StackTraceElement element : unwrapped.getStackTrace()) {
            sb.append("  at ");
            sb.append(element.toString());
            sb.append('\n');
        }

        this.alerter.alert(sb.toString());
    }

    /**
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
     *
     */
    private void throwClientSideException() {
        logger.info("About to throw a client-side exception");
        throw new IllegalStateException("Expected exception on the client side");
    }

    /**
     *
     */
    private void showUndeclaredException() {
        logger.info("Calling throwUndeclaredException");
        airlineService.throwUndeclaredException(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable ex) {
                alertOnException(ex);
            }

            @Override
            public void onSuccess(Void aVoid) {
                alerter.alert("This shouldn't happen");
            }
        });
    }

    /**
     *
     */
    private void showDeclaredException() {
        logger.info("Calling throwDeclaredException");
        airlineService.throwDeclaredException(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable ex) {
                alertOnException(ex);
            }

            @Override
            public void onSuccess(Void aVoid) {
                alerter.alert("This shouldn't happen");
            }
        });
    }



    /**
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
     *
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
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        alertOnException(throwable);
                    }
                    @Override
                    public void onSuccess(Void aVoid) {
                        logger.info("Updating Flight Table");
                        updateFlightCellTable();
                    }
                });

    }

    /**
     *
     */
    private void createAirlinePanel() {

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

        importAirlineXMLButton = new Button("Import");
        importAirlineXMLButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                importAirlineXML();
            }
        });

        exportAirlineXMLButton = new Button("Export");
        exportAirlineXMLButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                exportAirlineXML();
            }
        });

        HorizontalPanel importExportButtonPanel = new HorizontalPanel();
        importExportButtonPanel.add(importAirlineXMLButton);
        importExportButtonPanel.add(exportAirlineXMLButton);

        airlineInfoPanel.add(addDeleteButtonPanel);
        airlineInfoPanel.add(importExportButtonPanel);

        airlineListBox = new ListBox();
        airlineListBox.ensureDebugId("airline-multiBox");
        airlineListBox.setMultipleSelect(false);
        airlineListBox.setVisibleItemCount(10);
        airlineListBox.setWidth("210px");
        airlineInfoPanel.add(airlineListBox);

        airlineListBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                updateFlightCellTable();
            }
        });
    }

    /**
     *
     */
    private void exportAirlineXML() {
        Window.alert("Need to implement Airline Export Command");
    }

    /**
     *
     */
    private void importAirlineXML() {
        Window.alert("Need to implement Airline Import Command");
    }

    /**
     *
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
     *
     */
    private class newFlightPopup extends PopupPanel
    {
        public newFlightPopup()
        {
            super(false);

            final Label   airlineNameLabel = new Label("Airline Name:");
            final ListBox newFlightAirlineListBox = new ListBox();


            newFlightAirlineListBox.ensureDebugId("airline-dropBox");
            newFlightAirlineListBox.setMultipleSelect(false);
            for(int i = 0; i < airlineListBox.getItemCount(); i++)
                newFlightAirlineListBox.addItem(airlineListBox.getValue(i));

            VerticalPanel airlineNameDropBoxPanel = new VerticalPanel();
            airlineNameDropBoxPanel.add(airlineNameLabel);
            airlineNameDropBoxPanel.add(newFlightAirlineListBox);

            final TextBox flightNumberTextBox = new TextBox();
            flightNumberTextBox.setTitle("Flight Number");
            final AirlineDateTimePanel departDateTime = new AirlineDateTimePanel("Departure");
            final AirportNamesSelector sourceAirport = new AirportNamesSelector("Departure");
            final AirlineDateTimePanel arrivalDateTime = new AirlineDateTimePanel("Arrival");
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
            topPanel.add(flightNumberTextBox);
            topPanel.add(departDateTime);
            topPanel.add(sourceAirport);
            topPanel.add(arrivalDateTime);
            topPanel.add(destinationAirport);
            topPanel.add(buttonPanel);
            setWidget(topPanel);
        }
    }

    private static final ProvidesKey<Flight> KEY_PROVIDER =
            new ProvidesKey<Flight>() {
                @Override
                public Object getKey(Flight item) {
                    return item.getNumber();
                }
            };

    private void createFlightCellTable()
    {
        flightTable = new CellTable<Flight>(KEY_PROVIDER);
        //flightTable.setSize("600px", "200px");
        flightTable.setWidth("100%",true);
        flightTable.setAutoHeaderRefreshDisabled(true);
        flightTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

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
                return flight.getSourceName();
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
                return flight.getDestinationName();
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
                if(selectedFlight != null)
                    Window.alert("Selected flight " + selectedFlight.getNumber());
            }
        });

        flightTable.setRowCount(10);

    }
    private void updateFlightCellTable()
    {
        final int[] count = {0};
        flightCellTableData = new ArrayList<Flight>();
        logger.info("Looking at " + airlineListBox.getItemCount() + " airlines");

        for(int i = 0; i < airlineListBox.getItemCount(); i++)
        {
            if(airlineListBox.isItemSelected(i))
            {
                 logger.info("Adding flights from " + airlineListBox.getItemText(i) );
                 airlineService.getAirline(airlineListBox.getItemText(i), new AsyncCallback<Airline>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        alertOnException(throwable);
                    }
                    @Override
                    public void onSuccess(Airline airline) {
                        for(Flight flight: airline.getFlights())
                        {
                            logger.info("Adding flight number " + flight.getNumber());
                            flightCellTableData.add(flight);
                            count[0]++;
                        }
                        logger.info("Added " + count[0] + " flights to the table");
                        flightTable.setRowCount(flightCellTableData.size()+2);
                        flightTable.setRowData(0,flightCellTableData);

                    }
                });
            }
        }

    }
    /**
     *
     */
    private class AirlineDateTimePanel extends AbsolutePanel {

        DatePicker datePicker;
        Label datePickerLabel;
        Label boxLabel;
        Label dateLabel;
        DateBox dateBox;
        Label timeLabel;
        TextBox timeBox;

        public AirlineDateTimePanel(String label) {

            super();
            datePicker = new DatePicker();
            datePickerLabel = new Label();
            boxLabel = new Label(label);
            dateLabel = new Label("Date:");
            dateBox = new DateBox();
            timeLabel = new Label("Time:");
            timeBox = new TextBox();

            datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(ValueChangeEvent<Date> event) {
                    Date date = event.getValue();
                    String dateString =
                            DateTimeFormat.getFormat("MM/dd/yyyy").format(date);
                    datePickerLabel.setText(dateString);
                }
            });
            // Set the default value
            datePicker.setValue(new Date(), true);

            // Create a DateBox
            dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));

            this.setSize("250px","70px");
            this.add(boxLabel,0, 15);
            this.add(dateLabel, 60, 0);
            this.add(timeLabel, 60, 33);
            this.add(dateBox, 105, 7);
            this.add(timeBox, 105, 40);

        }
        public String getDate()
        {
            return(datePickerLabel.getText());
        }
        public String getTime()
        {
            return(timeBox.getValue());
        }
    }

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
            String airportName = this.dropBox.getSelectedItemText();
            return(airportName.substring(airportName.length()-4, airportName.length()-1));
        }

    }

    private void createFlightSearchPanel()
    {
        // departureDateFilter();
        //arrivalDateFilter();

        departureAirportNamesDropBox = new ListBox();
        arrivalAirportNamesDropBox = new ListBox();

        Map<String, String> airportNamesMap = AirportNames.getNamesMap();
        departureAirportNamesDropBox.addItem("");
        arrivalAirportNamesDropBox.addItem("");
        for(Map.Entry<String, String> airport : airportNamesMap.entrySet())
        {
            String airportItemString = airport.getValue() + "(" + airport.getKey() + ")";
            departureAirportNamesDropBox.addItem(airportItemString);
            arrivalAirportNamesDropBox.addItem(airportItemString);
        }

        departureAirportNamesDropBox.ensureDebugId("AirlineGWT-FlightSearch-DepartureAirport-DropBox");
        departureAirportNamesDropBox.ensureDebugId("AirlineGWT-FlightSearch-ArrivalAirport-DropBox");


        final AirlineDateTimePanel departDate = new AirlineDateTimePanel("Departure");
        final AirportNamesSelector sourceAirport = new AirportNamesSelector("Depature");
        flightDepartureFilterPanel = new HorizontalPanel();
        flightDepartureFilterPanel.setHeight("20px");
        flightDepartureFilterPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        flightDepartureFilterPanel.add(departDate);
        flightDepartureFilterPanel.add(sourceAirport);

        final AirlineDateTimePanel arrivalDate = new AirlineDateTimePanel("Arrival");
        final AirportNamesSelector destinationAirport = new AirportNamesSelector("Arrival");
        flightArrivalFilterPanel = new HorizontalPanel();
        flightArrivalFilterPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        flightArrivalFilterPanel.add(arrivalDate);
        flightArrivalFilterPanel.add(destinationAirport);


        flightFilterPanel = new VerticalPanel();
        flightFilterPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        flightFilterPanel.add(new Label("Flight Filter"));
        flightFilterPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        flightFilterPanel.add(flightDepartureFilterPanel);
        flightFilterPanel.add(flightArrivalFilterPanel);

        HorizontalPanel flightFilterButtonPanel = new HorizontalPanel();
        flightFilterButtonPanel.setBorderWidth(0);
        flightFilterButtonPanel.setHeight("20px");

        filterFlightsButton = new Button("Filter Flights");
        filterFlightsButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                filterFlights();
            }
        });
        flightFilterButtonPanel.add(filterFlightsButton);

        clearFlightsFilterButton = new Button("Clear Filter");
        clearFlightsFilterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                clearFlightsFilter();
            }
        });
        flightFilterButtonPanel.add(clearFlightsFilterButton);

        flightFilterButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        Button addFlightButton = new Button("Add Flight");
        addFlightButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                new newFlightPopup().show();
                updateFlightList();
            }
        });
        flightFilterButtonPanel.add(addFlightButton);

        flightFilterPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        flightFilterPanel.add(flightFilterButtonPanel);
    }

    private void updateFlightList()
    {

    }
    private void filterFlights() {
        Window.alert("Need to implement Filter Flights Command");
    }

    private void clearFlightsFilter() {
        Window.alert("Need to implement Clear Flight Filter Command");
    }

    private void setupUI() {

        DockPanel dock = new DockPanel();
        dock.setStyleName("dockpanel2");
        dock.setSpacing(4);
        dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);

        airlineInfoPanel = new VerticalPanel();
        airlineInfoPanel.setWidth("250px");
        airlineInfoPanel.setStyleName("airlinePanel");
        createAirlinePanel();
        updateAirlineListBox();
        dock.add(airlineInfoPanel, DockPanel.WEST);


        createFlightCellTable();

        flightTablePanel = new VerticalPanel();
        flightTablePanel.setSize( "800px", "300px");
        //TextBox noFlights = new TextBox();
        //noFlights.setText("No Flights Available");
        //flightTablePanel.add(noFlights);
        flightTablePanel.add(flightTable);
        dock.add(flightTablePanel, DockPanel.CENTER);

        createFlightSearchPanel();
        dock.add(flightFilterPanel, DockPanel.SOUTH);

        dock.ensureDebugId("cwDockPanel");

        RootPanel rootPanel = RootPanel.get();
        rootPanel.add(dock);

    }

    private void setUpUncaughtExceptionHandler() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable throwable) {
                alertOnException(throwable);
            }
        });
    }

    @VisibleForTesting
    interface Alerter {
        void alert(String message);
    }
}
